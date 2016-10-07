package com.mindlin.mdns;

import java.nio.ByteBuffer;

public class DnsMessage {
	public static DnsMessage parse(ByteBuffer buf) {
		//See http://wiki.hevs.ch/uit/index.php5/Standards/Ethernet/Bonjour
		final int id      = buf.getShort() & 0xFF_FF;
		final int flags   = buf.getShort() & 0xFF_FF;
		final int qdCount = buf.getShort() & 0xFF_FF;
		final int anCount = buf.getShort() & 0xFF_FF;
		final int nsCount = buf.getShort() & 0xFF_FF;
		final int arCount = buf.getShort() & 0xFF_FF;
		MdnsQuery[] questions = new MdnsQuery[qdCount];
		for (int i = 0; i < qdCount; i++)
			questions[i] = MdnsQuery.readNext(buf);
		DnsAnswer[] answers = new DnsAnswer[anCount];
		for (int i = 0; i < anCount; i++)
			answers[i] = DnsAnswer.readNext(buf);
		//TODO finish
		return null;
	}
	//See http://www.zytrax.com/books/dns/ch15/
	int id;
	int flags;
	/**
	 * Number of items in question section
	 */
	int qdCount;
	/**
	 * Number of items in answer section
	 */
	int anCount;
	/**
	 * Number of items in authority section
	 */
	int nsCount;
	/**
	 * Number of items in additional section
	 */
	int arCount;
	
}
