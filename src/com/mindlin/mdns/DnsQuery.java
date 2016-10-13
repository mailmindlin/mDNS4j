package com.mindlin.mdns;

import java.nio.ByteBuffer;

public class DnsQuery {
	public static DnsQuery readNext(ByteBuffer buf) {
		FQDN fqdn = FQDN.readNext(buf);
		DnsType qType = DnsType.of(buf.getShort());
		short qClass = buf.getShort();
		return new DnsQuery(fqdn, qType, qClass);
	}
	
	public static DnsQueryBuilder builder() {
		return new DnsQueryBuilder();
	}

	protected final FQDN name;
	protected final DnsType qType;
	protected final short qClass;

	public DnsQuery(FQDN fqdn, DnsType qType, short qClass) {
		this.name = fqdn;
		this.qType = qType;
		this.qClass = qClass;
	}

	public FQDN getName() {
		return this.name;
	}

	public DnsType getType() {
		return this.qType;
	}
	
	public String getQclassStr() {
		DnsClass r = DnsClass.of(qClass);
		if (r == null)
			return "" + qClass;
		return r.toString();
	}

	public DnsClass getQCLASS() {
		return DnsClass.of(this.qClass);
	}

	public int getSize() {
		return name.getSize() + 4;
	}

	public void writeTo(ByteBuffer buf) {
		getName().writeTo(buf);
		buf.putShort(getType().getValue());
		buf.putShort(getQCLASS().getValue());
	}
	
	@Override
	public String toString() {
		return new StringBuffer()
				.append("DnsQuery{name:").append(name.toString())
				.append(",type:").append(getType())
				.append(",class:").append(getQclassStr())
				.append('}')
				.toString();
	}
	
	public static class DnsQueryBuilder {
		FQDN name;
		DnsType type;
		short clazz = DnsClass.IN.value;
		
		public DnsQueryBuilder() {
			
		}
		
		public DnsQueryBuilder setName(String name) {
			this.name = new FQDN(name);
			return this;
		}
		
		public DnsQueryBuilder setName(String...labels) {
			this.name = new FQDN(labels);
			return this;
		}
		
		public DnsQueryBuilder setName(FQDN name) {
			this.name = name;
			return this;
		}
		
		public DnsQueryBuilder setType(short type) {
			return setType(DnsType.of(type));
		}
		
		public DnsQueryBuilder setType(DnsType type) {
			this.type = type;
			return this;
		}
		
		public DnsQueryBuilder setClass(short clazz) {
			this.clazz = clazz;
			return this;
		}
		
		public DnsQueryBuilder setClass(DnsClass clazz) {
			this.clazz = clazz.value;
			return this;
		}
		
		public DnsQuery build() {
			return new DnsQuery(name, type, clazz);
		}
	}
}
