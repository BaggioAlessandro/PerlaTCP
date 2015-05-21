package org.dei.perla.channel.tcp;

import org.apache.log4j.Logger;
import org.dei.perla.core.channel.Channel;
import org.dei.perla.core.channel.ChannelFactory;
import org.dei.perla.core.descriptor.ChannelDescriptor;
import org.dei.perla.core.descriptor.InvalidDeviceDescriptorException;

public class TcpChannelFactory implements ChannelFactory {
	
	private Server server;
	
	public TcpChannelFactory(Server server){
		this.server = server;
	}
	
	private static final String ERR_CHANNEL_CREATION = "Cannot create "
			+ TcpChannel.class.getCanonicalName() + ": %s";
	
	private final Logger logger = Logger.getLogger(TcpChannelFactory.class);

	@Override
	public Class<? extends ChannelDescriptor> acceptedChannelDescriptorClass() {
		return TcpChannelDescriptor.class;
	}

	@Override
	public Channel createChannel(ChannelDescriptor descriptor)
			throws InvalidDeviceDescriptorException {
		
		TcpChannelDescriptor tcpDescriptor;
		
		if(!(descriptor instanceof TcpChannelDescriptor)){
			String message = String.format(ERR_CHANNEL_CREATION, "expected "
					+ TcpChannelDescriptor.class.getCanonicalName()
					+ " but received "
					+ descriptor.getClass().getCanonicalName() + ".");
			logger.error(message);
			throw new InvalidDeviceDescriptorException(message);
		}
		tcpDescriptor = (TcpChannelDescriptor) descriptor;
		TcpChannel channel = new TcpChannel(tcpDescriptor.getId(), tcpDescriptor.getSrcIpAddress(), tcpDescriptor.getSrcPort(), tcpDescriptor.getDestIpAddress(), tcpDescriptor.getDestPort());
		server.addChannel(channel);
		return channel;
	}

}
