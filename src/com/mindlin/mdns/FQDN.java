package com.mindlin.mdns;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FQDN {
	public static FQDN readNext(ByteBuffer buf) {
		List<String> labels = readLabels(buf);
		return new FQDN(labels.toArray(new String[labels.size()]));
	}
	
	private static List<String> readLabels(ByteBuffer buf) {
		int len;
		ArrayList<String> labels = new ArrayList<String>();
		byte[] tmp = new byte[63];
		while ((len = buf.get() & 0xFF) > 0) {
			if (len > 191) {
				//TODO remove recursion
				//Thanks to publib.boulder.ibm.com/html/as400/v4r5/ic2979/info/RZAB6DNSFORMAT.HTM
				ByteBuffer dup = buf.duplicate();
				int offset = ((len & 63) << 8) | (buf.get() & 0xFF);
//				System.out.print("/O(" + offset + "," + buf.position() + "){");
				dup.position(offset);
				labels.addAll(readLabels(dup));
//				System.out.print('}');
				return labels;
			}
//			System.out.print("/L(" + len + "," + buf.position()+")");
			buf.get(tmp, 0, len);
			labels.add(new String(tmp, 0, len, StandardCharsets.US_ASCII));
		}
		return labels;
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
			if (sb.length() > 0)
				sb.setLength(sb.length() - 1);
			this.toStringCache = sb.toString();
		}
		return this.toStringCache;
	}

	public int getSize() {
		final int l = this.labels.length;
		int len = l + 1;
		for (int i = 0; i < l; i++)
			len += labels[i].getBytes(StandardCharsets.US_ASCII).length;
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
