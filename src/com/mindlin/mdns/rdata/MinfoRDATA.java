package com.mindlin.mdns.rdata;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import com.mindlin.mdns.FQDN;

/**
 * RDATA for MINFO type
 * @author mailmindlin
 *
 */
public class MinfoRDATA implements RData {
	/**
	 * Specifies a mailbox which is responsible for the mailing list or mailbox.
	 * If this domain name names the root, the owner of the MINFO RR is
	 * responsible for itself. Note that many existing mailing lists use a
	 * mailbox X-request for the RMAILBX field of mailing list X, e.g.,
	 * Msgroup-request for Msgroup. This field provides a more general
	 * mechanism.
	 */
	protected final FQDN rMailbox;
	/**
	 * Specifies a mailbox which is to receive error messages related to the
	 * mailing list or mailbox specified by the owner of the MINFO RR (similar
	 * to the ERRORS-TO: field which has been proposed). If this domain name
	 * names the root, errors should be returned to the sender of the message.
	 */
	protected final FQDN eMailbox;
	
	public MinfoRDATA(ByteBuffer buf) {
		this(FQDN.readNext(buf), FQDN.readNext(buf));
		if (buf.hasRemaining())
			throw new BufferOverflowException();
	}
	
	public MinfoRDATA(FQDN rMailbox, FQDN eMailbox) {
		this.rMailbox = rMailbox;
		this.eMailbox = eMailbox;
	}
	
	public FQDN getRMailbox() {
		return this.rMailbox;
	}
	
	public FQDN getEMailbox() {
		return this.eMailbox;
	}
	
	@Override
	public int getLength() {
		return this.rMailbox.getSize() + this.eMailbox.getSize();
	}
	
	@Override
	public void writeTo(ByteBuffer buf) {
		this.rMailbox.writeTo(buf);
		this.eMailbox.writeTo(buf);
	}
	
	@Override
	public String toString() {
		return new StringBuffer(rMailbox.getSize() + eMailbox.getSize() + 15)
				.append("{rmail:").append(rMailbox)
				.append(",email:").append(eMailbox)
				.append('}')
				.toString();
	}
}
