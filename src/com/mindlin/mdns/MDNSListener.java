package com.mindlin.mdns;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import com.mindlin.mdns.rdata.DnsUtils;

public class MDNSListener implements Runnable, Closeable {
	private static InetAddress lookup(String host, int... addr) {
		try {
			byte[] bAddr = new byte[addr.length];
			for (int i = 0; i < addr.length; i++)
				bAddr[i] = (byte) addr[i];
			return InetAddress.getByAddress(host, bAddr);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static final int MDNS_PORT = 5353;
	public static final int MAX_DNS_PACKET_SIZE = 576;
	/**
	 * IP4 multicast address.
	 */
	public static final InetAddress MDNS_IP4_ADDR = lookup("224.0.0.251", 224, 0, 0, 251);
	public static final InetAddress MDNS_IP6_ADDR = lookup("FF02::FB", 0xFF, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xFB);
	
	protected final SocketAddress group;
	protected final AtomicReference<MulticastSocket> socket = new AtomicReference<>(null);
	protected final NetworkInterface netIf;
	protected Consumer<DnsMessage> handler = this::defaultHandler;
	
	public MDNSListener() throws SecurityException, IOException {
		this(null);
	}
	
	public MDNSListener(NetworkInterface netIf) throws SecurityException, IOException {
		this(new InetSocketAddress(MDNS_IP4_ADDR, MDNS_PORT), netIf);
	}
	
	public MDNSListener(SocketAddress group, NetworkInterface netIf) throws SecurityException, IOException {
		System.out.println("Connecting via group " + group);
		this.group = group;
		this.netIf = netIf;
		this.resetSocket();
	}
	
	public void setHandler(Consumer<DnsMessage> handler) {
		this.handler = handler;
	}
	
	protected synchronized void resetSocket() throws IOException {
		MulticastSocket newSocket = new MulticastSocket();
		//We don't necessarily want to receive our own messages
		newSocket.setLoopbackMode(false);
		//Sharing is caring.
		newSocket.setReuseAddress(true);
		//TODO bind to same address as old socket
		newSocket.joinGroup(this.group, this.netIf);
		MulticastSocket oldSocket = this.socket.getAndSet(newSocket);
		//Close old socket, if applicable
		if (oldSocket != null)
			oldSocket.close();
	}
	
	@Override
	public void run() {
		ByteBuffer buf = ByteBuffer.allocate(MAX_DNS_PACKET_SIZE);
		DatagramPacket packet = new DatagramPacket(buf.array(), 0, MAX_DNS_PACKET_SIZE);
		while (true) {
			buf.clear();
			packet.setLength(buf.capacity());
			try {
				MulticastSocket socket;
				//Loop until we get a packet.
				while (true) {
					socket = this.socket.get();
					if (socket == null)
						//This listener is closed
						return;
					try {
						socket.receive(packet);
						break;
					} catch (SocketTimeoutException e) {
						continue;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			buf.position(packet.getOffset());
			buf.mark();
			buf.limit(packet.getLength());
			if (DnsUtils.DEBUG) {
				System.out.println("Read " + buf.remaining());
				System.out.println(DnsUtils.toHexString(buf.array(), ' ', '\n'));
				if (buf.remaining() % 32 != 0)
					System.out.println();
			}
			DnsMessage message;
			try {
				message = DnsMessage.parse(buf);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			try {
				handler.accept(message);
			} catch (Exception e) {
				Exception ePrime = new RuntimeException("Exception in handler for mDNS Listener", e);
				ePrime.fillInStackTrace();
				ePrime.printStackTrace();
			}
		}
	}
	
	protected void defaultHandler(DnsMessage message) {
		System.out.println(message);
		System.out.print("\tFLAG ");
		if (message.isResponse())
			System.out.print("QR ");
		if (message.isAuthorative())
			System.out.print("AA ");
		if (message.isTruncated())
			System.out.print("TC ");
		if (message.isRecursionDesired())
			System.out.print("RD ");
		if (message.isRecursionAvailable())
			System.out.print("RA ");
		if (message.getZBit())
			System.out.print("Z  ");
		if (message.isAuthenticatedData())
			System.out.print("AD ");
		if (message.isCheckingDisabled())
			System.out.print("CD");
		System.out.println();
		for (int i = 0; i < message.questions.length; i++) {
			DnsQuery question = message.questions[i];
			System.out.println("\tQUERY\t" + question.getName().toString() + '\t' + question.getQclassStr() + '\t' + question.getType());
		}
		for (int i = 0; i < message.answers.length; i++) {
			DnsRecord answer = message.answers[i];
			System.out.println("\tRESP\t" + answer.getName().toString() + '\t' + answer.getClazz() + '\t' + answer.getType() + '\t' + answer.getData());
		}
		for (int i = 0; i < message.authRecords.length; i++) {
			DnsRecord record = message.authRecords[i];
			System.out.println("\tAUTH\t" + record.getName().toString() + '\t' + record.getClazz() + '\t' + record.getType() + '\t' + record.getData());
		}
		for (int i = 0; i < message.additionalRecords.length; i++) {
			DnsRecord record = message.additionalRecords[i];
			System.out.println("\tADDTL\t" + record.getName().toString() + '\t' + record.getClazz() + '\t' + record.getType() + '\t' + record.getData());
		}
	}
	
	public void query(String name) throws IOException {
		query(DnsType.ANY, name);
	}
	
	public void query(DnsType type, String name) throws IOException {
		sendMessage(DnsMessage.builder()
				.setRecursionDesired(true)
				.ask(DnsQuery.builder()
						.setType(type)
						.setName(name)
						.setClass(DnsClass.IN)
						.build())
				.build());
	}
	
	public void sendMessage(DnsMessage message) throws IOException {
		int size = message.getSize();
		ByteBuffer buf = ByteBuffer.allocate(size);
		message.writeTo(buf);
		DatagramPacket packet = new DatagramPacket(buf.array(), 0, size, this.group);
		this.sendPacket(packet, true);
	}
	
	protected void sendPacket(DatagramPacket packet, boolean retry) throws IOException {
		MulticastSocket socket = this.socket.get();
		if (socket == null)
			return;
		try {
			socket.send(packet);
		} catch (IOException e) {
			if (!retry)
				throw e;
			this.resetSocket();
			sendPacket(packet, false);
		}
	}
	
	@Override
	public void close() {
		MulticastSocket socket = this.socket.getAndSet(null);
		if (socket != null)
			socket.close();
	}
}
