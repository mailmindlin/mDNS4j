package com.mindlin.mdns.rdata;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class AddressRDATA extends ByteArrayRDATA {
	protected transient InetAddress addr;
	protected final String hostname;
	
	public AddressRDATA(ByteBuffer buf, String hostname) {
		super(buf);
		if (super.data.length != 4 && super.data.length != 16)
			throw new IllegalArgumentException("Illegal address length " + super.data.length);
		this.hostname = hostname;
	}
	
	public AddressRDATA(byte[] addr, String hostname) {
		super(addr);
		if (addr.length != 4 && addr.length != 16)
			throw new IllegalArgumentException("Illegal IP address length " + addr.length);
		this.hostname = hostname;
	}
	
	public AddressRDATA(InetAddress addr) {
		super(addr.getAddress());
		this.addr = addr;
		this.hostname = addr.getHostName();
	}
	
	public InetAddress getAddress() {
		if (addr == null) {
			try {
				addr = InetAddress.getByAddress(this.hostname, super.data);
			} catch (UnknownHostException e) {
				//Can't happen, data length already validated
			}
		}
		return addr;
	}
	@Override
	public String toString() {
		if (super.data.length == 4)
			return new StringBuffer(super.data.length * 4)
					.append(data[0] & 0xFF)
					.append('.').append(data[1] & 0xFF)
					.append('.').append(data[2] & 0xFF)
					.append('.').append(data[3] & 0xFF)
					.toString();
		return DnsUtils.toHexString(super.data, ':');
	}
}
