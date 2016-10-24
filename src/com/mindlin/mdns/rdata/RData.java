package com.mindlin.mdns.rdata;

import java.nio.ByteBuffer;

import com.mindlin.mdns.DnsType;

public interface RData {
	public static RData readNext(DnsType type, ByteBuffer buf) {
		final int len = buf.getShort() & 0xFFFF;
//		System.out.println("Len: " + len);
		ByteBuffer slice = buf.duplicate();
		slice.limit(slice.position() + len);
		buf.position(buf.position() + len);
		if (type == null)
			return new ByteArrayRDATA(buf);
		switch (type) {
			case A:
			case AAAA:
			case NULL:
				return new ByteArrayRDATA(slice);
			case HINFO:
				return new HinfoRDATA(slice);
			case MX:
				return new MxRDATA(slice);
			case MINFO:
				return new MinfoRDATA(slice);
			case SOA:
				return new SoaRDATA(slice);
			case TXT:
				return new TxtRDATA(slice);
			case CNAME:
			case MB:
			case MD:
			case MF:
			case MG:
			case MR:
			case NS:
			case PTR:
				return new FqdnRDATA(slice);
			case WKS:
				//TODO finish these
			default:
				System.out.println("Defaulting to byte array (unsupported type " + type + ", len " + len + ", rem " + slice.remaining() + ")");
				return new ByteArrayRDATA(slice);
		}
	}
	
	int getLength();
	void writeTo(ByteBuffer buf);
}
