package com.mindlin.mdns.rdata;

import java.nio.ByteBuffer;

public class ByteArrayRDATA implements RData {
	protected final byte[] data;
	
	public ByteArrayRDATA(ByteBuffer buf) {
		this.data = new byte[buf.remaining()];
		buf.get(this.data);
	}
	
	public ByteArrayRDATA(byte[] data) {
		this.data = data;
	}
	
	@Override
	public int getLength() {
		return data.length + 1;
	}
	
	@Override
	public void writeTo(ByteBuffer buf) {
		buf.putShort((short) data.length);
		buf.put(data, 0, data.length);
	}
	
	@Override
	public String toString() {
		return '[' + DnsUtils.toHexString(this.data, ' ') + ']';
	}
}
