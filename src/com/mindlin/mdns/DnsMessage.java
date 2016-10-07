package com.mindlin.mdns;

import java.nio.ByteBuffer;

public class DnsMessage {
	public static DnsMessage parse(ByteBuffer buf) {
		//See http://www.networksorcery.com/enp/protocol/dns.htm
		final int id      = buf.getShort() & 0xFF_FF;
		final int flags   = buf.getShort() & 0xFF_FF;
		final int qdCount = buf.getShort() & 0xFF_FF;
		final int anCount = buf.getShort() & 0xFF_FF;
		final int nsCount = buf.getShort() & 0xFF_FF;
		final int arCount = buf.getShort() & 0xFF_FF;
		DnsQuery[] questions = new DnsQuery[qdCount];
		for (int i = 0; i < qdCount; i++)
			questions[i] = DnsQuery.readNext(buf);
		DnsAnswer[] answers = new DnsAnswer[anCount];
		for (int i = 0; i < anCount; i++)
			answers[i] = DnsAnswer.readNext(buf);
		//TODO finish
		return null;
	}
	//See http://www.zytrax.com/books/dns/ch15/
	protected final int id;
	protected final int flags;
	/**
	 * Number of items in question section
	 */
	protected final int qdCount;
	protected final DnsQuery[] questions;
	/**
	 * Number of items in answer section
	 */
	protected final int anCount;
	protected final DnsAnswer[] answers;
	/**
	 * Number of items in authority section
	 */
	protected final int nsCount;
	/**
	 * Number of items in additional section
	 */
	protected final int arCount;
	
	public DnsMessage(int id, int flags, DnsQuery[] questions, DnsAnswer[] answers, int nsCount, int arCount) {
		this.id = id;
		this.flags = flags;
		this.questions = questions == null ? new DnsQuery[0] : questions;
		this.qdCount = this.questions.length;
		this.answers = answers == null ? new DnsAnswer[0] : answers;
		this.anCount = this.answers.length;
		this.nsCount = nsCount;
		this.arCount = arCount;
	}
	
	public int getSize() {
		int result = 12;//Header length
		for (int i = 0; i < qdCount; i++)
			result += questions[i].getSize();
		for (int i = 0; i < anCount; i++)
			result += answers[i].getSize();
		return result; 
	}
	
	public void writeTo(ByteBuffer buf) {
		buf.putShort((short) id);
		buf.putShort((short) flags);
		buf.putShort((short) qdCount);
		buf.putShort((short) anCount);
		buf.putShort((short) nsCount);
		buf.putShort((short) arCount);
		
		for (int i = 0; i < qdCount; i++)
			questions[i].writeTo(buf);
		
		for (int i = 0; i < anCount; i++)
			answers[i].writeTo(buf);
	}
	
}
