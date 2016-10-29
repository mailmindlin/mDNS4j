package com.mindlin.mdns;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mindlin.mdns.rdata.DnsUtils;

public class DnsMessage {
	//Masks for the flags
	public static final int QR_MASK = 1 << 15;
	public static final int OPCODE_MASK = 0b1111 << 11;
	public static final int AA_MASK = 1 << 10;
	public static final int TC_MASK = 1 << 9;
	public static final int RD_MASK = 1 << 8;
	public static final int RA_MASK = 1 << 7;
	public static final int Z_MASK  = 1 << 6;
	public static final int AD_MASK = 1 << 5;
	public static final int CD_MASK = 1 << 4;
	public static final int RCODE_MASK = 0b1111;
	
	@SuppressWarnings("unused")
	public static DnsMessage parse(ByteBuffer buf) {
		// See http://www.networksorcery.com/enp/protocol/dns.htm
		final int id = buf.getShort() & 0xFF_FF;
		final int flags = buf.getShort() & 0xFF_FF;
		final int qdCount = buf.getShort() & 0xFF_FF;
		final int anCount = buf.getShort() & 0xFF_FF;
		final int nsCount = buf.getShort() & 0xFF_FF;
		final int arCount = buf.getShort() & 0xFF_FF;
		if (DnsUtils.DEBUG)
			System.out.println("Id: " + id + " Flags: " + flags + " QD: " + qdCount + " AN: " + anCount + " NS: " + nsCount + " AR: " + arCount);
		
		if (qdCount > 0 && DnsUtils.DEBUG)
			System.out.println("Questions:");
		DnsQuery[] questions = new DnsQuery[qdCount];
		for (int i = 0; i < qdCount; i++) {
			if (!DnsUtils.CORRECT && !buf.hasRemaining()) {
				System.err.println("Unable to read all QD (" + i + "/" + qdCount + ")");
				DnsQuery[] tmp = questions;
				questions = new DnsQuery[i];
				System.arraycopy(tmp, 0, questions, 0, i);
				break;
			}
			questions[i] = DnsQuery.readNext(buf);
		}
		
		if (anCount > 0 && DnsUtils.DEBUG)
			System.out.println("Answers:");
		DnsRecord[] answers = new DnsRecord[anCount];
		for (int i = 0; i < anCount; i++) {
			if (!DnsUtils.CORRECT && !buf.hasRemaining()) {
				System.err.println("Unable to read all AN (" + i + "/" + arCount + ")");
				DnsRecord[] tmp = answers;
				answers = new DnsRecord[i];
				System.arraycopy(tmp, 0, answers, 0, i);
				break;
			}
			answers[i] = DnsRecord.readNext(buf);
		}
		
		if (nsCount > 0 && DnsUtils.DEBUG)
			System.out.println("Authorities:");
		DnsRecord[] authRecords = new DnsRecord[nsCount];
		for (int i = 0; i < nsCount; i++) {
			if (!DnsUtils.CORRECT && !buf.hasRemaining()) {
				System.err.println("Unable to read all NS (" + i + "/" + nsCount + ")");
				DnsRecord[] tmp = authRecords;
				authRecords = new DnsRecord[i];
				System.arraycopy(tmp, 0, authRecords, 0, i);
				break;
			}
			authRecords[i] = DnsRecord.readNext(buf);
		}

		if (arCount > 0 && DnsUtils.DEBUG)
			System.out.println("Additional:");
		DnsRecord[] additional = new DnsRecord[arCount];
		for (int i = 0; i < arCount; i++) {
			if (!DnsUtils.CORRECT && !buf.hasRemaining()) {
				System.err.println("Unable to read all AR (" + i + "/" + arCount + ")");
				DnsRecord[] tmp = additional;
				additional = new DnsRecord[i];
				System.arraycopy(tmp, 0, additional, 0, i);
				break;
			}
			additional[i] = DnsRecord.readNext(buf);
		}
		
		return new DnsMessage(id, flags, questions, answers, authRecords, additional);
	}
	
	public static DnsMessageBuilder builder() {
		return new DnsMessageBuilder();
	}
	
	// See http://www.zytrax.com/books/dns/ch15/
	protected final int id;
	
	protected final int flags;
	
	/**
	 * Question section
	 */
	protected final DnsQuery[] questions;
	
	/**
	 * Answer section
	 */
	protected final DnsRecord[] answers;

	/**
	 * Authority section
	 */
	protected final DnsRecord[] authRecords;
	/**
	 * Additional section
	 */
	protected final DnsRecord[] additionalRecords;
	
	public DnsMessage(int id, int flags, DnsQuery[] questions, DnsRecord[] answers,
			DnsRecord[] authRecords, DnsRecord[] additionalRecords) {
		this.id = id;
		this.flags = flags;
		this.questions = questions;
		this.answers = answers;
		this.authRecords = authRecords;
		this.additionalRecords = additionalRecords;
	}
	
	public int getId() {
		return this.id;
	}
	
	public boolean isResponse() {
		return (this.flags & QR_MASK) != 0;
	}
	
	public byte getOpcode() {
		return (byte) ((this.flags & OPCODE_MASK) >>> 11);
	}
	
	public boolean isAuthorative() {
		return (this.flags & AA_MASK) != 0;
	}
	
	public boolean isTruncated() {
		return (this.flags & TC_MASK) != 0;
	}
	
	public boolean isRecursionDesired() {
		return (this.flags & RD_MASK) != 0;
	}
	
	public boolean isRecursionAvailable() {
		return (this.flags & RA_MASK) != 0;
	}
	
	public boolean getZBit() {
		return (this.flags & Z_MASK) != 0;
	}
	
	public boolean isAuthenticatedData() {
		return (this.flags & AD_MASK) != 0;
	}
	
	public boolean isCheckingDisabled() {
		return (this.flags & CD_MASK) != 0;
	}
	
	public byte getReturnCode() {
		return (byte) (this.flags & RCODE_MASK);
	}
	
	public DnsQuery[] getQuestions() {
		return questions;
	}

	public DnsRecord[] getAnswers() {
		return answers;
	}

	public DnsRecord[] getAuthRecords() {
		return authRecords;
	}

	public DnsRecord[] getAdditionalRecords() {
		return additionalRecords;
	}
	
	public int getSize() {
		int result = 12;// Header length
		for (int i = 0, l = this.questions.length; i < l; i++)
			result += this.questions[i].getSize();
		
		for (int i = 0, l = this.answers.length; i < l; i++)
			result += this.answers[i].getSize();
		
		for (int i = 0, l = this.authRecords.length; i < l; i++)
			result += this.authRecords[i].getSize();
		
		for (int i = 0, l = this.additionalRecords.length; i < l; i++)
			result += this.additionalRecords[i].getSize();
		return result;
	}
	
	public void writeTo(ByteBuffer buf) {
		buf.putShort((short) this.id);
		buf.putShort((short) this.flags);
		buf.putShort((short) this.questions.length);
		buf.putShort((short) this.answers.length);
		buf.putShort((short) this.authRecords.length);
		buf.putShort((short) this.additionalRecords.length);
		
		for (int i = 0, l = this.questions.length; i < l; i++)
			this.questions[i].writeTo(buf);
		
		for (int i = 0, l = this.answers.length; i < l; i++)
			this.answers[i].writeTo(buf);
		
		for (int i = 0, l = this.authRecords.length; i < l; i++)
			this.authRecords[i].writeTo(buf);
		
		for (int i = 0, l = this.additionalRecords.length; i < l; i++)
			this.additionalRecords[i].writeTo(buf);
	}
	
	public String toString() {
		return new StringBuilder()
			.append("DnsMessage{id:").append(this.id)
			.append(",flags:").append(Integer.toBinaryString(this.flags))
			.append(",qdCount:").append(this.questions.length)
			.append(",anCount:").append(this.answers.length)
			.append(",nsCount:").append(this.authRecords.length)
			.append(",arCount:").append(this.additionalRecords.length)
			.append(",questions:").append(Arrays.toString(this.questions))
			.append(",answers:").append(Arrays.toString(this.answers))
			.append(",auth:").append(Arrays.toString(this.authRecords))
			.append(",additional:").append(Arrays.toString(this.additionalRecords))
			.append('}')
			.toString();
	}
	
	public static class DnsMessageBuilder {
		protected int id;
		protected int flags;
		protected List<DnsQuery> questions = new ArrayList<>();
		protected List<DnsRecord> answers = new ArrayList<>();
		protected List<DnsRecord> authorities = new ArrayList<>();
		protected List<DnsRecord> additional = new ArrayList<>();
		
		protected void setFlag(boolean value, int mask) {
			this.flags = value ? (this.flags | mask) : (this.flags & ~mask);
		}
		
		public DnsMessageBuilder setId(int id) throws IllegalArgumentException {
			if (id > 65535 || id < 0)
				throw new IllegalArgumentException("ID must be within the range (0, 2^16 - 1). Invalid value " + id);
			this.id = id;
			return this;
		}
		
		public DnsMessageBuilder setFlags(int flags) throws IllegalArgumentException {
			if (id > 65535 || id < 0)
				throw new IllegalArgumentException("Flags must be within the range (0, 2^16 - 1). Invalid value " + flags);
			this.flags = flags;
			return this;
		}
		
		public DnsMessageBuilder setQuery(boolean isQuery) {
			setFlag(isQuery, QR_MASK);
			return this;
		}
		
		public DnsMessageBuilder setOpcode(byte opcode) throws IllegalArgumentException {
			if (opcode > 15 || opcode < 0)
				throw new IllegalArgumentException("Opcode must be within the range (0, 2^4 - 1). Invalid value " + opcode);
			this.flags = (this.flags & ~OPCODE_MASK) | (opcode << 11);
			return this;
		}
		
		public DnsMessageBuilder setAuthorative(boolean authorative) {
			setFlag(authorative, AA_MASK);
			return this;
		}
		
		public DnsMessageBuilder setRecursionDesired(boolean recurse) {
			setFlag(recurse, RD_MASK);
			return this;
		}
		
		public DnsMessageBuilder setRecursionAvailable(boolean availability) {
			setFlag(availability, RA_MASK);
			return this;
		}
		
		public DnsMessageBuilder setZBit(boolean value) {
			setFlag(value, Z_MASK);
			return this;
		}
		
		public DnsMessageBuilder setIsAuthenticatedData(boolean value) {
			setFlag(value, AD_MASK);
			return this;
		}
		
		public DnsMessageBuilder setCheckingDisabled(boolean disabled) {
			setFlag(disabled, CD_MASK);
			return this;
		}
		
		public DnsMessageBuilder setReturnCode(byte rcode) {
			if (rcode > 15 || rcode < 0)
				throw new IllegalArgumentException("Return code must be within the range (0, 2^4 - 1). Invalid value " + rcode);
			this.flags = (this.flags & ~RCODE_MASK) | rcode;
			return this;
		}
		
		public DnsMessageBuilder ask(DnsQuery query) {
			this.questions.add(query);
			return this;
		}
		
		public DnsMessageBuilder answer(DnsRecord answer) {
			this.answers.add(answer);
			return this;
		}
		
		public DnsMessageBuilder addAuthority(DnsRecord authority) {
			this.authorities.add(authority);
			return this;
		}
		
		public DnsMessageBuilder addAdditional(DnsRecord record) {
			this.additional.add(record);
			return this;
		}
		
		public DnsMessage build() {
			return new DnsMessage(this.id, this.flags,
					this.questions.toArray(new DnsQuery[questions.size()]),
					this.answers.toArray(new DnsRecord[this.answers.size()]),
					this.authorities.toArray(new DnsRecord[this.authorities.size()]),
					this.additional.toArray(new DnsRecord[this.additional.size()]));
		}
	}
}
