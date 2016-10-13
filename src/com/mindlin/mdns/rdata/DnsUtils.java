package com.mindlin.mdns.rdata;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class DnsUtils {
	public static String readCharacterString(ByteBuffer buf) {
		StringBuffer sb = new StringBuffer();
		byte b = buf.get();
		if (((char) b) == '"') {
			while (true) {
				char c = (char) buf.get();
				if (c == '"') {
					break;
				} else if (c == '\\') {
					c = (char) buf.get();
					if (c >= '0' && c <= '9')
						sb.append((char) (c - '0' << 16 | (char)buf.get() - '0' << 8 | (char)buf.get() - '0'));
					else
						sb.append(c);
				} else if (c == ';') {
					while (((char)buf.get()) != '\n');
				} else {
					sb.append(c);
				}
			}
		} else {
			throw new UnsupportedOperationException("Not finished");
		}
		return sb.toString();
	}
	
	public static String readSetLengthString(ByteBuffer buf) {
		int len = buf.get() & 0xFF;
		byte[] bytes = new byte[len];
		buf.get(bytes, 0, len);
		return new String(bytes, 0, len, StandardCharsets.US_ASCII);
	}
	
	public static void writeSetLengthString(String str, ByteBuffer buf) {
		byte[] bytes = str.getBytes(StandardCharsets.US_ASCII);
		buf.put((byte)bytes.length);
		buf.put(bytes, 0, bytes.length);
	}
	
	public static final char[] hexChars = "0123456789ABCDEF".toCharArray();
}
