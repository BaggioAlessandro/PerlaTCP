package org.dei.perla.channel.tcp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.dei.perla.client.Client;
import org.dei.perla.core.descriptor.DeviceDescriptor;
import org.dei.perla.core.descriptor.InvalidDeviceDescriptorException;
import org.dei.perla.server.Demux;
import org.dei.perla.server.Server;
import org.junit.BeforeClass;
import org.junit.Test;

public class TcpChannelTest {
	
	private static TcpChannelFactory channelFactory;
	private static Server server;
	private static Client client;
	private static TcpChannel channel;
	
	private static DeviceDescriptor device;
	private static TcpChannelDescriptor descriptor;
	private static final String descriptorPath =
            "src/test/java/org/dei/perla/channel/tcp/tcp_descriptor.xml";
	
	@BeforeClass
	public static void initialize() throws Exception{
		
		server = new Server();
		Thread serverThread = new Thread(new ServerRunnable());
        serverThread.start();
		
		//creazione del Client per creare il ServerSocket al quale il Channel dovrà connettersi quando 
		//verrà creato
		String serverIpAddress = "127.0.0.1";
		int serverPort = Server.port;
		int clientPort = 3456;
		InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(serverIpAddress), serverPort);
		
		client = new Client(address, clientPort);
		Thread clientThread = new Thread(new ClientRunnable());
		clientThread.start();
		
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
	public void startupTest() throws InvalidDeviceDescriptorException {
		channel = (TcpChannel) channelFactory.createChannel(descriptor);
		assertNotNull(channel);
		assertFalse(channel.isClosed());
		int port = descriptor.getPort();
		String ipAddress = descriptor.getIpAddress();
		assertEquals(channel.getPort(), port);
		assertEquals(channel.getIpAddress(), ipAddress);
	}
	
	
	@Test
	public void lookUpTableTest(){
		Demux demux = server.getDemux();
		demux.getLookupTable();
		int dimension = 1;
		assertEquals(demux.getLookupTable().size(), dimension);
		String ipAddress = channel.getIpAddress();
		int port = channel.getPort();
		InetSocketAddress socketAddress = null;
		try {
			socketAddress = new InetSocketAddress(InetAddress.getByName(ipAddress),port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		assertEquals(demux.getLookupTable().get(socketAddress), channel);
	}
	

	@Test
	public void sendPacketToClient(){
		
	}
	
	@Test
	public void sendPacketToServer() throws InterruptedException{
		byte[] packet = {0,0,0,1000,1};
		client.sendPacket(packet);
		//Thread.sleep(1000);
		byte[] lastReceived = server.getLastReceived();
		for(int i=0; i < lastReceived.length; i++){
			System.out.println(lastReceived[i]);
		}
		assertEquals(packet, lastReceived);
	}
	
	@Test
	public void shutDownTest(){
		channel.close();
		assertTrue(channel.isClosed());
	}
	
	
	
	static class ServerRunnable implements Runnable{

		@Override
		public void run() {
			server.run();			
		}
		
	}
	
	static class ClientRunnable implements Runnable{

		@Override
		public void run() {
			client.run();			
		}
		
	}

}
