package com.mindlin.mdns;

import java.nio.ByteBuffer;

import com.mindlin.mdns.rdata.RData;

public class DnsRecord {
	public static DnsRecord readNext(ByteBuffer buf) {
		int st = buf.position();
		FQDN fqdn = FQDN.readNext(buf);
		short ts = buf.getShort();
		DnsType type = DnsType.of(ts);
		DnsClass clazz = DnsClass.of(buf.getShort());
		int ttl = buf.getInt();
		RData data = RData.readNext(type, buf);
		
		return new DnsRecord(fqdn, type, clazz, ttl, data);
	}
	
	protected final FQDN name;
	protected final DnsType type;
	protected final DnsClass clazz;
	protected final int ttl;
	protected final RData data;
	
	public DnsRecord(FQDN name, DnsType type, DnsClass clazz, int ttl, RData data) {
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
	
	public RData getData() {
		return data;
	}
	
	public int getSize() {
		return getName().getSize() + 10 + getData().getLength();
	}
	
	public void writeTo(ByteBuffer buf) {
		this.getName().writeTo(buf);
		buf.putShort(getType().getValue());
		buf.putShort(getClazz().getValue());
		buf.putInt(getTTL());
		buf.putShort((short) getData().getLength());
		getData().writeTo(buf);
	}
	
	@Override
	public String toString() {
		return new StringBuffer()
				.append("DnsRecord{name:").append(name.toString())
				.append(",type:").append(getType())
				.append(",class:").append(getClazz())
				.append(",ttl:").append(getTTL())
				.append(",dLen:").append(getData().getLength())
				.append(",data:").append(getData().toString())
				.append('}')
				.toString();
	}
}
