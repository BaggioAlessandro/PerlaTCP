package org.dei.perla.channel.tcp;

import org.apache.log4j.Logger;
import org.dei.perla.core.channel.AbstractChannel;
import org.dei.perla.core.channel.ChannelException;
import org.dei.perla.core.channel.IORequest;
import org.dei.perla.core.channel.Payload;
import org.dei.perla.core.channel.http.HttpChannel;
import org.dei.perla.core.channel.http.HttpIORequest;

public class TcpChannel extends AbstractChannel {
	
	private Logger logger = Logger.getLogger(TcpChannel.class);
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
		TcpIORequest tcpRequest;
		
		if (!(request instanceof TcpIORequest)) {
			String message = "Channel request error: Cannot cast from "
					+ request.getClass().getSimpleName() + " to "
					+ TcpIORequest.class.getSimpleName();
			logger.error(message);
			throw new ClassCastException(message);
		}
		
		tcpRequest = (TcpIORequest) request;
		
		return tcpRequest.getPayload();
	}

	
	

}
