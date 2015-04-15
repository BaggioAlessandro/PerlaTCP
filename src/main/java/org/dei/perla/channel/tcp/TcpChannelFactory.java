package org.dei.perla.channel.tcp;

import org.dei.perla.core.channel.Channel;
import org.dei.perla.core.channel.ChannelFactory;
import org.dei.perla.core.channel.http.HttpChannel;
import org.dei.perla.core.descriptor.ChannelDescriptor;
import org.dei.perla.core.descriptor.InvalidDeviceDescriptorException;

public class TcpChannelFactory implements ChannelFactory {
	
	private static final String ERR_CHANNEL_CREATION = "Cannot create "
			+ TcpChannel.class.getCanonicalName() + ": %s";

	@Override
	public Class<? extends ChannelDescriptor> acceptedChannelDescriptorClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Channel createChannel(ChannelDescriptor descriptor)
			throws InvalidDeviceDescriptorException {
		// TODO Auto-generated method stub
		return null;
	}

}
