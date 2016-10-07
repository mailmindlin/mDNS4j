package com.mindlin.mdns;

/**
 * DNS record types.
 * @author mailmindlin
 * @see <a href="https://en.wikipedia.org/wiki/List_of_DNS_record_types">List of DNS record types</a>
 */
public enum DnsType {
	//Record types
	A(1),
	AAAA(28),
	AFSDB(18),
	APL(42),
	CAA(257),
	CDNSKEY(60),
	CDS(59),
	CERT(37),
	CNAME(5),
	DHCID(49),
	DLV(32769),
	DNAME(39),
	DNSKEY(48),
	DS(43),
	HIP(55),
	IPSECKEY(45),
	KEY(25),
	KX(36),
	LOC(29),
	MX(15),
	NAPTR(35),
	NS(2),
	NSEC(47),
	NSEC3(50),
	NSEC3PARAM(51),
	PTR(12),
	RRSIG(46),
	RP(17),
	SIG(24),
	SOA(6),
	SRV(33),
	SSHFP(44),
	TA(32768),
	TKEY(249),
	TLSA(52),
	TSIG(250),
	TXT(16),
	URI(256),
	
	//Other types
	ANY(255),
	AXFR(252),
	IXFR(251),
	OPT(41),
	;
	
	public static DnsType of(short value) {
		switch (value) {
			case 1:
				return A;
			case 2:
				return NS;
			case 5:
				return CNAME;
			case 6:
				return SOA;
			case 12:
				return PTR;
			case 15:
				return MX;
			case 16:
				return TXT;
			case 17:
				return RP;
			case 18:
				return AFSDB;
			case 24:
				return SIG;
			case 25:
				return KEY;
			case 28:
				return AAAA;
			case 29:
				return LOC;
			case 33:
				return SRV;
			case 35:
				return NAPTR;
			case 36:
				return KX;
			case 37:
				return CERT;
			case 39:
				return DNAME;
			case 41:
				return OPT;
			case 42:
				return APL;
			case 43:
				return DS;
			case 44:
				return SSHFP;
			case 45:
				return IPSECKEY;
			case 46:
				return RRSIG;
			case 47:
				return NSEC;
			case 48:
				return DNSKEY;
			case 49:
				return DHCID;
			case 50:
				return NSEC3;
			case 51:
				return NSEC3PARAM;
			case 52:
				return TLSA;
			case 55:
				return HIP;
			case 59:
				return CDS;
			case 60:
				return CDNSKEY;
			case 249:
				return TKEY;
			case 250:
				return TSIG;
			case 251:
				return IXFR;
			case 252:
				return AXFR;
			case 255:
				return ANY;
			case 256:
				return URI;
			case 257:
				return CAA;
			case (short) 32768:
				return TA;
			case (short) 32769:
				return DLV;
			default:
				return null;
		}
	}
	private final short value;
	DnsType(int value) {
		this.value = (short) value;
	}
	
	public short getValue() {
		return this.value;
	}
}
