package org.dei.perla.channel.tcp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.dei.perla.core.channel.http.HttpChannelDescriptor;
import org.dei.perla.core.descriptor.ChannelDescriptor;
import org.dei.perla.core.descriptor.DeviceDescriptor;
import org.dei.perla.core.descriptor.IORequestDescriptor;
import org.dei.perla.core.descriptor.InvalidDeviceDescriptorException;
import org.junit.BeforeClass;
import org.junit.Test;

public class TcpRequestBuilderFactoryTest {
	private static DeviceDescriptor device;
	private static TcpIORequestBuilderFactory factory;
	private static List<IORequestDescriptor> requests;
	
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
		factory = new TcpIORequestBuilderFactory();
		
		requests = device.getRequestList();
	}
	
	@Test
	public void tcpXmlDescriptorConsistency()
			throws InvalidDeviceDescriptorException {
		assertFalse(device.getChannelList().isEmpty());
		ChannelDescriptor channelDescriptor = device.getChannelList().get(0);

		assertTrue(channelDescriptor instanceof TcpChannelDescriptor);
		assertFalse(device.getRequestList().isEmpty());
	}
	
	
	@Test
	public void createRequestBuilder() 
			throws InvalidDeviceDescriptorException{
		TcpIORequestDescriptor requestDescriptor = (TcpIORequestDescriptor) requests.get(0);
		TcpIORequestBuilder requestBuilder = (TcpIORequestBuilder) factory.create(requestDescriptor);
		assertThat(requestBuilder.getRequestId(),equalTo(requestDescriptor.getId()));
		TcpIORequest request = (TcpIORequest) requestBuilder.create();
		assertThat(requestDescriptor.getId(), equalTo(request.getId()));
	}


}
