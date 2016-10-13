package com.mindlin.mdns.rdata;

import java.nio.ByteBuffer;

public class ByteArrayRDATA implements RData {
	protected final byte[] data;
	
	public ByteArrayRDATA(ByteBuffer buf) {
		this.data = new byte[buf.remaining()];
		buf.get(this.data, 0, this.data.length);
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
		StringBuffer sb = new StringBuffer(data.length * 3 + 1);
		sb.append('[');
		for (int i = 0; i < data.length; i++) {
			int b = this.data[i] & 0xFF;
			sb.append(DnsUtils.hexChars[b >>> 4]);
			sb.append(DnsUtils.hexChars[b & 0xF]);
			sb.append(' ');
		}
		if (data.length > 0)
			sb.setLength(sb.length() - 1);
		sb.append(']');
		return sb.toString();
	}
}
