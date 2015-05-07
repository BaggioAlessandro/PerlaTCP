package org.dei.perla.channel.tcp;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.dei.perla.core.descriptor.ChannelDescriptor;
import org.dei.perla.core.descriptor.DeviceDescriptor;
import org.dei.perla.core.utils.Check;
import org.junit.BeforeClass;
import org.junit.Test;

public class TcpChannelDescriptorTest {
	
	private static DeviceDescriptor device;
	
	private static final String descriptorPath =
            "src/test/java/org/dei/perla/channel/tcp/tcp_descriptor.xml";

	@BeforeClass
	public static void parseDeviceDescriptor() throws Exception {
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
	}
	
	@Test
	public void tcpChannelDescriptorConsistency() {
		ChannelDescriptor channel = device.getChannelList().get(0);
		assertTrue(channel instanceof TcpChannelDescriptor);
	}
	
	@Test
	public void tcpChannelDescriptorFieldsConsistency(){
		TcpChannelDescriptor channel = (TcpChannelDescriptor) device.getChannelList().get(0);
		assertTrue(!Check.nullOrEmpty(channel.getIpAddress()));
		int port = 99;
		String ipAddress = "127.0.0.1";
		assertEquals(channel.getPort(),port);
		assertTrue(channel.getIpAddress().equals(ipAddress));
	}
	
	@Test
	public void tcpRequestDescriptorConsistency() {
		TcpIORequestDescriptor request = (TcpIORequestDescriptor) device.getRequestList().get(0);
		assertTrue(!Check.nullOrEmpty(request.getId()));
	}
	

}
