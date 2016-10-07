package com.mindlin.mdns;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;

public class MDNSListener implements Runnable {
	public static final int MDNS_PORT = 5353;
	/**
	 * IP4 multicast address.
	 */
	public static final byte[] MDNS_IP4_ADDR = new byte[]{(byte) 0xE0, (byte) 0x00, (byte) 0x00, (byte) 0xFB};
	protected final MulticastSocket socket;
	public MDNSListener() throws SecurityException, IOException {
		this.socket = new MulticastSocket(MDNS_PORT);
		this.socket.joinGroup(InetAddress.getByAddress("224.0.0.251", MDNS_IP4_ADDR));
	}
	
	@Override
	public void run() {
		while (true) {
			System.out.println("Waiting...");
			ByteBuffer buf = ByteBuffer.allocate(512);
			DatagramPacket packet = new DatagramPacket(buf.array(), 0, 512);
			try {
				this.socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			buf.position(packet.getOffset());
			buf.limit(packet.getOffset() + packet.getLength());
			char[] hexChars = "0123456789ABCDEF".toCharArray();
			System.out.println("RECV " + buf.remaining());
			for (int i = 0, l = buf.remaining(); i < l; i++) {
				int b = buf.get();
				System.out.print(hexChars[b & 0xF]);
				System.out.print(hexChars[b >>> 4]);
				System.out.print(' ');
				if (i % 16 == 15)
					System.out.println();
			}
			System.out.println();
		}
	}
	
	public void sendQuery(short id, short flags, short qdCount, short anCount, short nsCount, short arCount, byte[] data) {
	}
	
}
