package com.mindlin.mdns;

public enum DnsClass {
	IN(1),
	CS(2),
	CH(3),
	HS(4),
	NONE(254),
	ANY(255),
	IN_MDNS(0x8001),
	;
	
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
			case 254:
				return NONE;
			case 255:
				return ANY;
			case (short) 0x8001:
				return IN_MDNS;
			default:
				System.out.println("Unknown class #" + (value & 0xFFFF));
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
