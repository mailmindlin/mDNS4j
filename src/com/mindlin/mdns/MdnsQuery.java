package com.mindlin.mdns;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MdnsQuery {
	public static MdnsQuery readNext(ByteBuffer buf) {
		int len;
		ArrayList<String> labels = new ArrayList<>();
		while ((len = buf.get() & 0xFF) > 0) {
			byte[] tmp = new byte[len];
			buf.get(tmp, 0, len);
			labels.add(new String(tmp, 0, len, StandardCharsets.US_ASCII));
		}
		int qType = buf.getShort() & 0xFF_FF;
		int qClass = buf.getShort() & 0xFF_FF;
		return new MdnsQuery(labels.toArray(new String[labels.size()]), qType, qClass);
	}
	String[] fqdnLabels;
	int qType;
	int qClass;
	
	public MdnsQuery(String[] fqdnLabels, int qType, int qClass) {
		this.fqdnLabels = fqdnLabels;
		this.qType = qType;
		this.qClass = qClass;
	}
}
