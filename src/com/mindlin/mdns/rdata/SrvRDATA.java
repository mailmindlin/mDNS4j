package com.mindlin.mdns.rdata;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import com.mindlin.mdns.FQDN;

public class SrvRDATA implements RData {
	protected final int priority;
	protected final int weight;
	protected final int port;
	protected final FQDN target;
	
	public SrvRDATA(ByteBuffer buf) {
		this(buf.getShort() & 0xFFFF, buf.getShort() & 0xFFFF, buf.getShort() & 0xFFFF, FQDN.readNext(buf));
		if (buf.hasRemaining())
			throw new BufferOverflowException();
	}
	
	public SrvRDATA(int priority, int weight, int port, FQDN target) {
		this.priority = priority;
		this.weight = weight;
		this.port = port;
		this.target = target;
	}
	
	public int getPriority() {
		return this.priority;
	}
	
	public int getWeight() {
		return this.weight;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public FQDN getTarget() {
		return this.target;
	}
	
	@Override
	public int getLength() {
		return 2 + 2 + 2 + getTarget().getSize();
	}
	
	@Override
	public void writeTo(ByteBuffer buf) {
		buf.putShort((short) getLength());
		buf.putShort((short) getPriority());
		buf.putShort((short) getWeight());
		buf.putShort((short) getPort());
		getTarget().writeTo(buf);
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
			.append(getPriority())
			.append(' ').append(getWeight())
			.append(' ').append(getPort())
			.append(' ').append(getTarget())
			.toString();
	}
}
