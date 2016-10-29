package com.mindlin.mdns.rdata;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * RDATA for HINFO type
 * @author mailmindlin
 *
 */
public class HinfoRDATA implements RData {
	protected final String cpu;
	protected final String os;
	
	public HinfoRDATA(ByteBuffer buf) {
		this(DnsUtils.readSetLengthString(buf), DnsUtils.readSetLengthString(buf));
		if (buf.hasRemaining())
			throw new BufferOverflowException();
	}
	
	public HinfoRDATA(String cpu, String os) {
		this.cpu = cpu;
		this.os = os;
	}
	
	public String getCPUType() {
		return this.cpu;
	}
	
	public String getOS() {
		return this.os;
	}

	@Override
	public int getLength() {
		return 2 + this.getCPUType().getBytes(StandardCharsets.US_ASCII).length
				+ this.getOS().getBytes(StandardCharsets.US_ASCII).length;
	}

	@Override
	public void writeTo(ByteBuffer buf) {
		DnsUtils.writeSetLengthString(getCPUType(), buf);
		DnsUtils.writeSetLengthString(getOS(), buf);
	}
	
	@Override
	public String toString() {
		return new StringBuilder(cpu.length() + os.length() + 14)
				.append("{cpu:'").append(cpu)
				.append("',os:'").append(os)
				.append("'}")
				.toString();
	}
}
