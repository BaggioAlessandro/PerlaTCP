package org.dei.perla.channel.tcp;

import org.apache.log4j.Logger;
import org.dei.perla.core.channel.IORequestBuilder;
import org.dei.perla.core.channel.IORequestBuilderFactory;
import org.dei.perla.core.descriptor.IORequestDescriptor;
import org.dei.perla.core.descriptor.InvalidDeviceDescriptorException;
import org.dei.perla.server.Server;

public class TcpIORequestBuilderFactory implements IORequestBuilderFactory {
	
	
	public TcpIORequestBuilderFactory(){
		super();
	}

	private final Logger logger = Logger.getLogger(TcpIORequestBuilderFactory.class);
	
	@Override
	public Class<? extends IORequestDescriptor> acceptedIORequestClass() {
		return TcpIORequestDescriptor.class;
	}

	@Override
	public IORequestBuilder create(IORequestDescriptor descriptor)
			throws InvalidDeviceDescriptorException {
		
		TcpIORequestDescriptor tcpDesc;
		String requestId;
		TcpIORequestBuilder tcpRequestBuilder;

		// Check descriptor class
		if (!(descriptor instanceof TcpIORequestDescriptor)) {
			String message = String.format("Expected "
					+ IORequestDescriptor.class.getCanonicalName()
					+ " but received "
					+ descriptor.getClass().getCanonicalName() + ".");
			logger.error(message);
			throw new InvalidDeviceDescriptorException(message);
		}

		tcpDesc = (TcpIORequestDescriptor) descriptor;

		requestId = tcpDesc.getId();

		tcpRequestBuilder = new TcpIORequestBuilder(requestId);

		return tcpRequestBuilder;
	}

}
