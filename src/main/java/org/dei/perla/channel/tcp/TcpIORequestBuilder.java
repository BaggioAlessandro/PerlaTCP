package org.dei.perla.channel.tcp;

import java.util.List;

import org.dei.perla.core.channel.IORequest;
import org.dei.perla.core.channel.IORequestBuilder;

public class TcpIORequestBuilder implements IORequestBuilder {
	
	private final String requestId;

	protected TcpIORequestBuilder (String requestId) {
		this.requestId = requestId;
	}
	
	@Override
	public String getRequestId() {
		return requestId;
	}

	@Override
	public IORequest create() {
		TcpIORequest tcpChReq = new TcpIORequest(requestId);
		return tcpChReq;
	}

	@Override
	public List<IORequestParameter> getParameterList() {
		// TODO Auto-generated method stub
		return null;
	}

}
