package com.mindlin.mdns;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class DnsAnswer {
	public static DnsAnswer readNext(ByteBuffer buf) {
		StringBuffer fqdn = new StringBuffer();
		int len;
		while ((len = buf.get() & 0xFF) > 0) {
			if (fqdn.length() > 0)
				fqdn.append('.');
			byte[] tmp = new byte[len];
			buf.get(tmp, 0, len);
			fqdn.append(new String(tmp, 0, len, StandardCharsets.US_ASCII));
		}
		int delim = buf.get() & 0xFF;
		DnsType type = DnsType.of(buf.getShort());// TODO lookup
		DnsClass clazz = DnsClass.of(buf.getShort());
		
		len = buf.getShort() & 0xFF_FF;
		byte[] data = new byte[len];
		buf.get(data, 0, len);
		return new DnsAnswer(fqdn.toString(), type, clazz, data);
	}
	
	protected final String name;
	protected final DnsType type;
	protected final DnsClass clazz;
	protected final byte[] data;
	
	public DnsAnswer(String name, DnsType type, DnsClass clazz, byte[] data) {
		this.name = name;
		this.type = type;
		this.clazz = clazz;
		this.data = data;
	}
	
	public String getName() {
		return this.name;
	}
	
	public DnsType getType() {
		return this.type;
	}
	
	public DnsClass getClazz() {
		return this.clazz;
	}
	
	public byte[] getData() {
		return data;
	}
}
