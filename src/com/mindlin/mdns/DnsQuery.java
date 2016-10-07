package com.mindlin.mdns;

import java.nio.ByteBuffer;

public class DnsQuery {
	public static DnsQuery readNext(ByteBuffer buf) {
		FQDN fqdn = FQDN.readNext(buf);
		DnsType qType = DnsType.of(buf.getShort());
		DnsClass qClass = DnsClass.of(buf.getShort());
		return new DnsQuery(fqdn, qType, qClass);
	}

	FQDN fqdn;
	DnsType qType;
	DnsClass qClass;

	public DnsQuery(FQDN fqdn, DnsType qType, DnsClass qClass) {
		this.fqdn = fqdn;
		this.qType = qType;
		this.qClass = qClass;
	}

	public FQDN getName() {
		return this.fqdn;
	}

	public DnsType getQTYPE() {
		return this.qType;
	}

	public DnsClass getQCLASS() {
		return this.qClass;
	}

	public int getSize() {
		return fqdn.getSize() + 4;
	}

	public void writeTo(ByteBuffer buf) {
		getName().writeTo(buf);
		buf.putShort(getQTYPE().getValue());
		buf.putShort(getQCLASS().getValue());
	}
}
