package com.mindlin.mdns.rdata;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import com.mindlin.mdns.FQDN;

public class SoaRDATA implements RData {
	/**
	 * The <domain-name> of the name server that was the original or primary
	 * source of data for this zone.
	 */
	protected final FQDN mName;
	/**
	 * A <domain-name> which specifies the mailbox of the person responsible for
	 * this zone.
	 */
	protected final FQDN rName;
	/**
	 * The unsigned 32 bit version number of the original copy of the zone. Zone
	 * transfers preserve this value. This value wraps and should be compared
	 * using sequence space arithmetic.
	 * 
	 */
	protected final int serial;
	/**
	 * A 32 bit time interval before the zone should be refreshed.
	 */
	protected final int refresh;
	/**
	 * A 32 bit time interval that should elapse before a failed refresh should
	 * be retried.
	 */
	protected final int retry;
	/**
	 * A 32 bit time value that specifies the upper limit on the time interval
	 * that can elapse before the zone is no longer authoritative.
	 */
	protected final int expire;
	/**
	 * The unsigned 32 bit minimum TTL field that should be exported with any RR
	 * from this zone.
	 */
	protected final int minimum;
	
	public SoaRDATA(ByteBuffer buf) {
		this(FQDN.readNext(buf), FQDN.readNext(buf), buf.getInt(), buf.getInt(), buf.getInt(), buf.getInt(), buf.getInt());
		if (buf.hasRemaining())
			throw new BufferOverflowException();
	}
	public SoaRDATA(FQDN mName, FQDN rName, int serial, int refresh, int retry, int expire, int minimum) {
		this.mName = mName;
		this.rName = rName;
		this.serial = serial;
		this.refresh = refresh;
		this.retry = retry;
		this.expire = expire;
		this.minimum = minimum;
	}
	
	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeTo(ByteBuffer buf) {
		// TODO Auto-generated method stub
		
	}
	
}
