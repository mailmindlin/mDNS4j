package com.mindlin.mdns.rdata;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class DnsUtils {
	public static final boolean DEBUG = false;
	public static final boolean CORRECT = true;
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
	
	public static String toHexString(byte[] data, char separator) {
		StringBuffer sb = new StringBuffer(data.length * 3 + 1);
		for (int i = 0; i < data.length; i++) {
			int b = data[i] & 0xFF;
			sb.append(DnsUtils.hexChars[b >>> 4]);
			sb.append(DnsUtils.hexChars[b & 0xF]);
			sb.append(separator);
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}
	
	public static final char[] hexChars = "0123456789ABCDEF".toCharArray();
	
	public static String toHexString(byte[] data, char separator, char lineSeparator) {
		StringBuffer sb = new StringBuffer(data.length * 3 + 1);
		for (int i = 0; i < data.length; i++) {
			int b = data[i] & 0xFF;
			sb.append(DnsUtils.hexChars[b >>> 4]);
			sb.append(DnsUtils.hexChars[b & 0xF]);
			sb.append(i % 32 == 31 ? lineSeparator : separator);
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}
}
