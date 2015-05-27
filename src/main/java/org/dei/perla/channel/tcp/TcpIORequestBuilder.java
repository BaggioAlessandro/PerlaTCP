package org.dei.perla.channel.tcp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.dei.perla.core.channel.IORequest;
import org.dei.perla.core.channel.IORequestBuilder;

/**
 * 
 * @author Luca Baggi (2015)
 * @author Alessandro Baggio (2015)
 *
 */
public class TcpIORequestBuilder implements IORequestBuilder {
	
	private static final List<IORequestParameter> paramList;
	private static final String PAYLOAD = "payload";
	
	static{
		IORequestParameter[] paramArray = new IORequestParameter[1];
		paramArray[0] = new IORequestParameter(PAYLOAD, true);
		paramList = Collections.unmodifiableList(Arrays.asList(paramArray));
	}
	
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
		return paramList;
	}

}
