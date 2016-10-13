package com.mindlin.mdns;

public enum DnsOpcode {
	QUERY(0),
	IQUERY(1),
	STATUS(2),
	NOTIFY(4),
	UPDATE(5);
	public static DnsOpcode lookup(byte value) {
		switch (value) {
			case 0:
				return QUERY;
			case 1:
				return IQUERY;
			case 2:
				return STATUS;
			case 4:
				return NOTIFY;
			case 5:
				return UPDATE;
			default:
				return null;
		}
	}
	
	public final byte value;
	
	DnsOpcode(int v) {
		value = (byte) v;
	}
}
