package org.dei.perla.channel.tcp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.dei.perla.client.Client;
import org.dei.perla.channel.tcp.SynchronizerIOHandler;
import org.dei.perla.core.channel.Payload;
import org.dei.perla.core.descriptor.DeviceDescriptor;
import org.dei.perla.core.descriptor.InvalidDeviceDescriptorException;
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
		descriptor.setSrcPort(client.getSendingPort());
		clientPort = client.getSendingPort();
	}
	
	
	@Test
	public void startupTest() throws InvalidDeviceDescriptorException {
		channel = (TcpChannel) channelFactory.createChannel(descriptor);
		assertNotNull(channel);
		assertFalse(channel.isClosed());
		int destPort = descriptor.getDestPort();
		String ipAddress = descriptor.getipAddress();
		int srcPort = descriptor.getSrcPort();
		assertEquals(channel.getDestPort(), destPort);
		assertEquals(channel.getipAddress(), ipAddress);
		assertEquals(channel.getSrcPort(), srcPort);
		channel.close();
		assertTrue(channel.isClosed());
	}
	
	
	@Test
	public void lookUpTableTest() throws InvalidDeviceDescriptorException{
		channel = (TcpChannel) channelFactory.createChannel(descriptor);
		Demux demux = server.getDemux();
		demux.getLookupTable();
		int dimension = 1;
		assertEquals(demux.getLookupTable().size(), dimension);
		String ipAddress = channel.getipAddress();
		int port = channel.getSrcPort();
		InetSocketAddress socketAddress = null;
		try {
			socketAddress = new InetSocketAddress(InetAddress.getByName(ipAddress),port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		assertEquals(demux.getLookupTable().get(socketAddress), channel);
	}
	

	@Test
	public void sendPacketToClient() throws RuntimeException, ExecutionException, InterruptedException, InvalidDeviceDescriptorException{
		channel = (TcpChannel) channelFactory.createChannel(descriptor);
		int sequence = channel.getSequence();
		String requestId = "-test-";
		TcpIORequest request = new TcpIORequest(requestId);
		request.setParameter("payload", new StringPayload("request-test"));
		SynchronizerIOHandler syncHandler = new SynchronizerIOHandler();
		channel.submit(request, syncHandler);
		Thread.sleep(1000);
		int expectedSequence = sequence + 1;
		assertEquals(expectedSequence, channel.getSequence());
		assertTrue(channel.getMapperHandler().size() == 1);
		assertTrue(channel.getMapperRequest().size() == 1);
		assertEquals(request, channel.getMapperRequest().get(sequence));
		assertEquals(syncHandler, channel.getMapperHandler().get(sequence));
		
		byte[] lastReceived = client.getLastReceived();
		
		//verifica che il Payload ricevuto dal client corrisponda a quello inviato
		byte[] payload = new byte[lastReceived.length - Integer.BYTES];
		for(int i = Integer.BYTES; i < lastReceived.length; i++)
			payload[i-Integer.BYTES] = lastReceived[i];
		String decodedPayload = null;
		try {
			decodedPayload = new String(payload, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String expectedPayload = "request-test";
		assertEquals(expectedPayload.trim(), decodedPayload.trim());
		
		//verifica che il numero di sequenza ricevuto dal client corrisponda a quello inviato
		byte[] sequenceNumberByte = new byte[Integer.BYTES];
		for(int i = 0; i < Integer.BYTES; i++)
			sequenceNumberByte[i] = lastReceived[i];
		ByteBuffer buffer = ByteBuffer.wrap(sequenceNumberByte);
		int sequenceNumber = buffer.getInt();
		assertEquals(sequence, sequenceNumber);
		
		//verifica che il client risponda correttamente
		//il client risponde qualcosa (tipo = 0, seqNumb = 1, payload = 65537)
		byte[] packet = {0,0,0,0,0,0,0,1,0,1,0,1,1};
		client.sendPacket(packet);
		Payload response = syncHandler.getResult().orElseThrow(RuntimeException::new);
		ByteBuffer buff = response.asByteBuffer();
		int expected = 65537;
		assertEquals(expected, buff.getInt());
	}
	
	
	@Test
	public void sendPacketToServer() throws InterruptedException, InvalidDeviceDescriptorException{
		channel = (TcpChannel) channelFactory.createChannel(descriptor);
		byte[] packet = {0,0,0,0,1,0,0,0,0,1};
		client.sendPacket(packet);
		Thread.sleep(1000);
		byte[] lastReceived = server.getLastReceived();
		for(int i=0; i < packet.length; i++){
			assertEquals(packet[i], lastReceived[i]);
		}
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
