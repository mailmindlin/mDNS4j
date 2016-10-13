package com.mindlin.mdns;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

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
	protected final BlockingQueue<DatagramPacket> queue = new LinkedBlockingQueue<>();
	
	public MDNSListener() throws SecurityException, IOException {
		this(MDNS_IP4_ADDR, MDNS_PORT);
	}
	
	public MDNSListener(InetAddress group, int port) throws SecurityException, IOException {
		System.out.println("Connecting via group=" + group + ":" + port);
		this.group = group;
		this.port = port;
		this.socket = new MulticastSocket();
		this.socket.joinGroup(InetAddress.getByName("224.0.0.251"));
	}
	
	public void start(ExecutorService executor) {
		executor.submit(this::run);
		executor.submit(this::process);
	}
	
	@Override
	public void run() {
		while (true) {
			DatagramPacket packet = new DatagramPacket(new byte[512], 0, 512);
			try {
				this.socket.receive(packet);
				queue.put(packet);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	public void process() {
		while (true) {
			DatagramPacket packet;
			try {
				packet = queue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			ByteBuffer buf = ByteBuffer.wrap(packet.getData(), packet.getOffset(), packet.getLength());
			buf.mark();
			
			/*System.out.println(packet.getLength());
			char[] hexChars = "0123456789ABCDEF".toCharArray();
			for (int i = 0, l = buf.remaining(); i < l; i++) {
				int b = buf.get() & 0xFF;
				System.out.print(hexChars[b >>> 4]);
				System.out.print(hexChars[b & 0xF]);
				System.out.print(' ');
				if (i % 32 == 31)
					System.out.println();
			}
			System.out.println();*/
			
			buf.reset();
			DnsMessage message;
			try {
				message = DnsMessage.parse(buf);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
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
