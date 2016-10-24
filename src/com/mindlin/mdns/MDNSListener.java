package com.mindlin.mdns;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class MDNSListener implements Runnable {
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
	/**
	 * IP4 multicast address.
	 */
	public static final InetAddress MDNS_IP4_ADDR = lookup("224.0.0.251", (byte) 224, 0x00, (byte) 0x00, (byte) 251);
	public static final InetAddress MDNS_IP6_ADDR = lookup("FF02::FB", 0xFF, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xFB);
	
	protected final InetAddress group;
	protected final int port;
	protected final MulticastSocket socket;
	protected Consumer<DnsMessage> handler = this::defaultHandler;
	
	public MDNSListener() throws SecurityException, IOException {
		this(MDNS_IP4_ADDR, MDNS_PORT);
	}
	
	public MDNSListener(InetAddress group, int port) throws SecurityException, IOException {
		System.out.println("Connecting via group=" + group + ":" + port);
		this.group = group;
		this.port = port;
		this.socket = new MulticastSocket();
		this.socket.joinGroup(group);
	}
	
	public void setHandler(Consumer<DnsMessage> handler) {
		this.handler = handler;
	}
	
	@Override
	public void run() {
		ByteBuffer buf = ByteBuffer.allocate(512);
		DatagramPacket packet = new DatagramPacket(buf.array(), 0, 512);
		while (true) {
			buf.clear();
			packet.setLength(512);
			try {
				this.socket.receive(packet);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			buf.position(packet.getOffset());
			buf.mark();
			buf.limit(packet.getLength());
			System.out.println(buf.remaining());
			char[] hexChars = "0123456789ABCDEF".toCharArray();
			for (int i = 0, l = buf.remaining(); i < l; i++) {
				int b = buf.get() & 0xFF;
				System.out.print(hexChars[b >>> 4]);
				System.out.print(hexChars[b & 0xF]);
				System.out.print(' ');
				if (i % 32 == 31)
					System.out.println();
			}
			System.out.println();
			buf.flip();
			DnsMessage message;
			try {
				message = DnsMessage.parse(buf);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			
			handler.accept(message);
		}
	}
	
	public void defaultHandler(DnsMessage message) {
		System.out.println(message);
		for (int i = 0; i < message.questions.length; i++) {
			DnsQuery question = message.questions[i];
			System.out.println("QUERY " + question.getName().toString() + '\t' + question.getQclassStr() + '\t' + question.getType());
		}
		for (int i = 0; i < message.answers.length; i++) {
			DnsRecord answer = message.answers[i];
			System.out.println("RESP " + answer.getName().toString() + '\t' + answer.getClazz() + '\t' + answer.getType() + '\t' + answer.getData());
		}
		for (int i = 0; i < message.authRecords.length; i++) {
			DnsRecord answer = message.authRecords[i];
			System.out.println("AUTH " + answer.getName().toString() + '\t' + answer.getClazz() + '\t' + answer.getType());
		}
		for (int i = 0; i < message.additionalRecords.length; i++) {
			DnsRecord answer = message.additionalRecords[i];
			System.out.println("ADDTL " + answer.getName().toString() + '\t' + answer.getClazz() + '\t' + answer.getType());
		}
		System.out.println("DONE");
	}
	
	public void query(String fqdn) throws IOException {
		sendMessage(DnsMessage.builder()
				.setRecursionDesired(true)
				.ask(DnsQuery.builder().setName(fqdn).setClass(DnsClass.IN).setType(DnsType.ANY).build()).build());
	}
	
	public void sendMessage(DnsMessage message) throws IOException {
//		System.out.println("Sending " + message);
		int size = message.getSize();
		ByteBuffer buf = ByteBuffer.allocate(size);
		message.writeTo(buf);
		DatagramPacket packet = new DatagramPacket(buf.array(), 0, size, this.group, this.port);
		this.socket.send(packet);
	}
}
