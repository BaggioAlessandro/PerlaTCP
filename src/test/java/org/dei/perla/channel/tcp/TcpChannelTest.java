package org.dei.perla.channel.tcp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.dei.perla.core.descriptor.DeviceDescriptor;
import org.dei.perla.core.descriptor.InvalidDeviceDescriptorException;
import org.dei.perla.server.Server;
import org.junit.BeforeClass;
import org.junit.Test;

public class TcpChannelTest {
	
	private static TcpChannelFactory channelFactory;
	private static Server server;
	
	private static DeviceDescriptor device;
	private static TcpChannelDescriptor descriptor;
	private static final String descriptorPath =
            "src/test/java/org/dei/perla/channel/tcp/tcp_descriptor.xml";
	
	@BeforeClass
	public static void initialize() throws Exception{
		server = new Server();
		channelFactory = server.getChannelFactory();
		
		StringBuilder sb = new StringBuilder();
		sb.append("org.dei.perla.core.descriptor");
		sb.append(":org.dei.perla.core.descriptor.instructions");
		sb.append(":org.dei.perla.core.message.urlencoded");
		sb.append(":org.dei.perla.core.message.json");
		sb.append(":org.dei.perla.channel.tcp");

		JAXBContext jc = JAXBContext.newInstance(sb.toString());

		Unmarshaller unmarshaller = jc.createUnmarshaller();
		StreamSource xml = new StreamSource(descriptorPath);
		device = unmarshaller.unmarshal(xml, DeviceDescriptor.class).getValue();
		descriptor = (TcpChannelDescriptor) device.getChannelList().get(0);
	}
	
	@Test
	public void startupShutdownTest() throws InvalidDeviceDescriptorException {
		TcpChannel channel = (TcpChannel) channelFactory.createChannel(descriptor);
		assertNotNull(channel);
		assertFalse(channel.isClosed());
		int port = descriptor.getPort();
		String ipAddress = descriptor.getIpAddress();
		assertEquals(channel.getPort(), port);
		assertEquals(channel.getIpAddress(), ipAddress);
		channel.close();
		assertTrue(channel.isClosed());
	}

}
