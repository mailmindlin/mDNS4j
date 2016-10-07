package com.mindlin.mdns;

public enum DnsClass {
	IN(1),
	CS(2),
	CH(3),
	HS(4),
	ANY(255);
	
	public static DnsClass of(short value) {
		switch (value) {
			case 1:
				return IN;
			case 2:
				return CS;
			case 3:
				return CH;
			case 4:
				return HS;
			case 255:
				return ANY;
			default:
				return null;
		}
	}
	
	final short value;
	DnsClass(int value) {
		this.value = (short) value;
	}
	
	public short getValue() {
		return this.value;
	}
}
