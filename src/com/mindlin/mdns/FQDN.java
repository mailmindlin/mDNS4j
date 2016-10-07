package com.mindlin.mdns;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FQDN {
	public static FQDN readNext(ByteBuffer buf) {
		int len;
		ArrayList<String> labels = new ArrayList<String>();
		byte[] tmp = new byte[63];
		while ((len = buf.get() & 0xFF) > 0) {
			if (len > 63)
				throw new IllegalArgumentException("Illegal length for FQDN label: " + len);
			buf.get(tmp, 0, len);
			labels.add(new String(tmp, 0, len, StandardCharsets.US_ASCII));
		}
		return new FQDN(labels.toArray(new String[labels.size()]));
	}

	protected transient String toStringCache = null;
	String[] labels;

	public FQDN(String name) {
		this(name.split("\\."));
		this.toStringCache = name;
	}

	public FQDN(String... labels) {
		this.labels = labels;
	}

	@Override
	public String toString() {
		if (toStringCache == null) {
			StringBuffer sb = new StringBuffer(getSize() - 1);
			for (int i = 0, l = this.labels.length; i < l; i++)
				sb.append(labels[i]).append('.');
			sb.setLength(sb.length() - 1);
			this.toStringCache = sb.toString();
		}
		return this.toStringCache;
	}

	public int getSize() {
		final int l = this.labels.length;
		int len = l;
		for (int i = 0; i < l; i++)
			len += labels[i].length();
		return len;
	}

	public void writeTo(ByteBuffer buf) {
		for (int i = 0, l = this.labels.length; i < l; i++) {
			byte[] label = this.labels[i].getBytes(StandardCharsets.US_ASCII);
			buf.put((byte) label.length);
			buf.put(label);
		}
		buf.put((byte) 0);
	}
}
