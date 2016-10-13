package com.mindlin.mdns;

/**
 * DNS record types.
 * @author mailmindlin
 * @see <a href="https://en.wikipedia.org/wiki/List_of_DNS_record_types">List of DNS record types</a>
 */
public enum DnsType {
	//Record types
	A(1),
	NS(2),
	/**
	 * Mail destination
	 * @deprecated Use {@link #MX}
	 */
	MD(3),
	/**
	 * Mail forwarder
	 * @deprecated Use {@link #MX}
	 */
	MF(4),
	/**
	 * Canonical name for an alias
	 */
	CNAME(5),
	/**
	 * Marks the start of a zone of authority
	 */
	SOA(6),
	/**
	 * a mailbox domain name (EXPERIMENTAL)
	 */
	MB(7),
	/**
	 * a mail group member (EXPERIMENTAL)
	 */
	MG(8),
	/**
	 * a mail rename domain name (EXPERIMENTAL)
	 */
	MR(9),
	/**
	 * a null RR (EXPERIMENTAL)
	 */
	NULL(10),
	/**
	 * a well known service description
	 */
	WKS(11),
	/**
	 * A domain name pointer
	 */
	PTR(12),
	/**
	 * host information
	 */
	HINFO(13),
	/**
	 * Mailbox or mail list information
	 */
	MINFO(14),
	/**
	 * Mail eXchange
	 */
	MX(15),
	/**
	 * Text strings
	 */
	TXT(16),
	/**
	 * for Responsible Person
	 */
	RP(17),
	/**
	 * for AFS Data Base location
	 */
	AFSDB(18),
	/**
	 * for X.25 PSDN address
	 */
	X25(19),
	/**
	 * for ISDN address
	 */
	ISDN(20),
	/**
	 * for Route Through
	 */
	RT(21),
	/**
	 * for NSAP address, NSAP style A record
	 */
	NSAP(22),
	/**
	 * for domain name pointer, NSAP style
	 */
	NSAP_PTR(23),
	/**
	 * for security signature
	 */
	SIG(24),
	/**
	 * for security signature
	 */
	KEY(25),
	/**
	 * X.400 mail mapping information
	 */
	PX(26),
	/**
	 * Geographical Position
	 */
	GPOS(27),
	/**
	 * IP6 address
	 */
	AAAA(28),
	/**
	 * Location information
	 */
	LOC(29),
	/**
	 * @deprecated
	 */
	NXT(30),
	/**
	 * Endpoint Identifier
	 */
	EID(31),
	/**
	 * Nimrod Locator
	 */
	NIMLOC(32),
	/**
	 * Server selection
	 */
	SRV(33),
	/**
	 * ATM address
	 */
	ATMA(34),
	/**
	 * Naming Authority Pointer
	 */
	NAPTR(35),
	/**
	 * Key Exchanger
	 */
	KX(36),
	CERT(37),
	/**
	 * @deprecated Use {@link #AAAA}
	 */
	A6(38),
	DNAME(39),
	SINK(40),
	OPT(41),
	APL(42),
	/**
	 * Delegation Signer
	 */
	DS(43),
	/**
	 * SSH Key Fingerprint
	 */
	SSHFP(44),
	IPSECKEY(45),
	RRSIG(46),
	NSEC(47),
	DNSKEY(48),
	DHCID(49),
	NSEC3(50),
	NSEC3PARAM(51),
	TLSA(52),
	/**
	 * S/MIME cert association
	 */
	SMIMEA(53),
	//54 Unassigned
	/**
	 * Host Identity Protocol
	 */
	HIP(55),
	NINFO(56),
	RKEY(57),
	/**
	 * Trust Anchor LINK
	 */
	TALINK(58),
	/**
	 * Child DS
	 */
	CDS(59),
	/**
	 * DNSKEY(s) the Child wants reflected in DS
	 */
	CDNSKEY(60),
	/**
	 * OpenPGP key
	 */
	OPENPGPKEY(61),
	/**
	 * Child-to-parent synchronization
	 */
	CSYNC(62),
	//63-98 Unassigned
	UINFO(100),
	UID(101),
	GID(102),
	UNSPEC(103),
	NID(104),
	L32(105),
	L64(106),
	LP(107),
	/**
	 * EUI-48 address
	 */
	EUI48(108),
	/**
	 * EUI-64 address
	 */
	EUI64(109),
	//110-248 Unassigned
	/**
	 * Transaction key
	 */
	TKEY(249),
	/**
	 * Transaction signature
	 */
	TSIG(250),
	/**
	 * Incremental transfer
	 */
	IXFR(251),
	/**
	 * Transfer of an entire zone
	 */
	AXFR(252),
	/**
	 * mailbox-related RRs (MB, MG or MR)
	 */
	MAILB(253),
	/**
	 * mail agent RRs
	 * @deprecated Use {@link #MX}
	 */
	MAILA(254),
	/**
	 * A request for all records the server/cache has available
	 */
	ANY(255),
	/**
	 * URI
	 */
	URI(256),
	/**
	 * Certification Authority Restriction
	 */
	CAA(257),
	/**
	 * Application Visibility and Control
	 */
	AVC(258),
	//259-32767 Unassigned
	/**
	 * DNSSEC Trust Authorities
	 */
	TA(32768),
	/**
	 * DNSSEC Lookaside Validation
	 */
	DLV(32769),
	// 32770-65279 Unassigned
	// 65280-65534 Private use
	// 65535 Reserved
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
