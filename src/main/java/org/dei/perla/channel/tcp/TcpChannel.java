package org.dei.perla.channel.tcp;

import org.dei.perla.core.channel.AbstractChannel;
import org.dei.perla.core.channel.ChannelException;
import org.dei.perla.core.channel.IORequest;
import org.dei.perla.core.channel.Payload;

public class TcpChannel extends AbstractChannel {
	
	private String ipAddress;
	private int port;

	public TcpChannel(String id, String ipAddress, int port) {
		super(id);
		this.ipAddress = ipAddress;
		this.port = port;
	}

	@Override
	public Payload handleRequest(IORequest request) throws ChannelException,
			InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

}
