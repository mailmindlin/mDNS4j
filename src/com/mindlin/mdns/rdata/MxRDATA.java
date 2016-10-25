package com.mindlin.mdns.rdata;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import com.mindlin.mdns.FQDN;

/**
 * RDATA for MX type
 * @author mailmindlin
 *
 */
public class MxRDATA implements RData {
	protected final short preference;
	protected final FQDN exchange;
	
	public MxRDATA(ByteBuffer buf) {
		this(buf.getShort(), FQDN.readNext(buf));
		if (buf.hasRemaining())
			throw new BufferOverflowException();
	}
	
	public MxRDATA(short preference, FQDN exchange) {
		this.preference = preference;
		this.exchange = exchange;
	}
	
	public short getPreference() {
		return this.preference;
	}
	
	public FQDN getExchange() {
		return this.exchange;
	}
	
	@Override
	public int getLength() {
		return 2 + exchange.getSize();
	}
	
	@Override
	public void writeTo(ByteBuffer buf) {
		buf.putShort(this.preference);
		this.exchange.writeTo(buf);
	}
	
}
