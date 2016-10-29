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
		while (buf.hasRemaining()) {
			int len = buf.get() & 0xFF;
			if (len == 0)
				break;
			if (len > buf.remaining()) {
				String msg = "Malformed TXT message (pos: " + buf.position() + " rem: " + buf.remaining() + " len: " + len + " delta: " + (buf.remaining() - len) + ")";
				if (DnsUtils.CORRECT)
					throw new IllegalStateException(msg);
				System.err.println(msg);
				break;
			}
			byte[] bytes = new byte[len];
			buf.get(bytes, 0, len);
			String label = new String(bytes, 0, len, StandardCharsets.US_ASCII);
			if (DnsUtils.DEBUG)
				System.out.println("[TXT]: \"" + label + '"');
			int idx = label.indexOf('=');
			this.data.put(label.substring(0, idx), label.substring(idx + 1));
		}
	}
	
	public TxtRDATA(Map<String, String> data) {
		this.data = data;
	}
	
	public Map<String, String> getMap() {
		return this.data;
	}
	
	public String getEntry(String key) {
		return data.get(key);
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
