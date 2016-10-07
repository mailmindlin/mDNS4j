package com.mindlin.mdns;

import java.nio.ByteBuffer;

public class DnsAnswer {
	public static DnsAnswer readNext(ByteBuffer buf) {
		FQDN fqdn = FQDN.readNext(buf);
		DnsType type = DnsType.of(buf.getShort());// TODO lookup
		DnsClass clazz = DnsClass.of(buf.getShort());
		
		int ttl = buf.getInt();
		
		//Get data
		int len = buf.getShort() & 0xFF_FF;
		byte[] data = new byte[len];
		buf.get(data, 0, len);
		
		return new DnsAnswer(fqdn, type, clazz, ttl, data);
	}
	
	protected final FQDN name;
	protected final DnsType type;
	protected final DnsClass clazz;
	protected final int ttl;
	protected final byte[] data;
	
	public DnsAnswer(FQDN name, DnsType type, DnsClass clazz, int ttl, byte[] data) {
		this.name = name;
		this.type = type;
		this.clazz = clazz;
		this.ttl = ttl;
		this.data = data;
	}
	
	public FQDN getName() {
		return this.name;
	}
	
	public DnsType getType() {
		return this.type;
	}
	
	public DnsClass getClazz() {
		return this.clazz;
	}
	
	public int getTTL() {
		return this.ttl;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public int getSize() {
		return getName().getSize() + 10 + getData().length;
	}
	
	public void writeTo(ByteBuffer buf) {
		this.getName().writeTo(buf);
		buf.putShort(getType().getValue());
		buf.putShort(getClazz().getValue());
		buf.putInt(getTTL());
		buf.putShort((short) getData().length);
		buf.put(getData());
	}
}
