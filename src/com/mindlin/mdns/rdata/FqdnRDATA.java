package com.mindlin.mdns.rdata;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import com.mindlin.mdns.FQDN;

public class FqdnRDATA implements RData {
	protected final FQDN value;
	
	public FqdnRDATA(ByteBuffer buf) {
		this(FQDN.readNext(buf));
		if (buf.hasRemaining())
			throw new BufferOverflowException();
	}
	
	public FqdnRDATA(FQDN value) {
		this.value = value;
	}
	
	public FQDN getValue() {
		return this.value;
	}

	@Override
	public int getLength() {
		return value.getSize();
	}

	@Override
	public void writeTo(ByteBuffer buf) {
		value.writeTo(buf);
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
}
