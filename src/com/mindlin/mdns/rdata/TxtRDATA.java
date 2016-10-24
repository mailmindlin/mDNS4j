package com.mindlin.mdns.rdata;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class TxtRDATA implements RData {
	protected final Map<String, String> data;
	
	public TxtRDATA(ByteBuffer buf) {
		this(new HashMap<>());
		byte[] bytes = new byte[buf.remaining()];
		buf.get(bytes);
		for (int i = 0; i < bytes.length; i++) {
			byte len = bytes[i];
			String label = new String(bytes, i + 1, len, StandardCharsets.US_ASCII);
			i += len;
			System.out.println('"' + label + '"');
			int idx = label.indexOf('=');
			this.data.put(label.substring(0, idx), label.substring(idx + 1));
		}
		System.out.println(this.data);
	}
	
	public TxtRDATA(Map<String, String> data) {
		this.data = data;
	}
	
	@Override
	public int getLength() {
		int len = 0;
		for (Entry<String, String> e : data.entrySet())
			len += 1 + e.getKey().getBytes(StandardCharsets.US_ASCII).length + 1 + e.getValue().getBytes(StandardCharsets.US_ASCII).length;
		return len;
	}
	
	@Override
	public void writeTo(ByteBuffer buf) {
		buf.putShort((short) getLength());
		for (Entry<String, String> e : data.entrySet()) {
			String v = e.getKey() + '=' + e.getValue();
			byte[] bytes = v.getBytes(StandardCharsets.US_ASCII);
			buf.put((byte) bytes.length);
			buf.put(bytes);
		}
	}
	
	@Override
	public String toString() {
		return data.toString();
	}
}
