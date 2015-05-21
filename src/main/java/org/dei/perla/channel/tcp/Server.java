package org.dei.perla.channel.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Optional;

import org.dei.perla.core.channel.IOHandler;
import org.dei.perla.core.channel.Payload;
import org.dei.perla.core.utils.Conditions;

public class Server {
	
	private Demux demux;	
	private ServerSocketChannel serverSocketChannel;
	private TcpChannelFactory channelFactory;
	private TcpIORequestBuilderFactory requestBuilderFactory;
	private IOHandler factoryHandler = null;
	public static final int port = 9999;
	
	private byte[] lastReceived;

	public Server(){
		channelFactory = new TcpChannelFactory(this);
		requestBuilderFactory = new TcpIORequestBuilderFactory();
		demux = new Demux(this);
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(port));
			serverSocketChannel.configureBlocking(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public TcpChannelFactory getChannelFactory(){
		return this.channelFactory;
	}
	
	public TcpIORequestBuilderFactory getIoRequestBuilderFactory(){
		return this.requestBuilderFactory;
	}
	
	/**
	 * Method to set the Handler that is used to handle the DeviceDescriptor 
	 * @param handler
	 * @throws IllegalStateException
	 */
	public void setFactoryIOHandler(IOHandler handler)
			throws IllegalStateException {
		if (this.factoryHandler != null) {
			throw new IllegalStateException(
					"A FactoryHandler has already been set for the Server");
		}
		this.factoryHandler = Conditions.checkNotNull(handler, "handler");
	}
	
	public void notifyDescriptor(Payload payload){
		if(this.factoryHandler == null)
			return;
		factoryHandler.complete(null, Optional.ofNullable(payload));
	}
	
	/**
	 * Method invoked by the TcpChannelFactory immediately after creating the channel. This 
	 * method calls the corresponding method on <class>Demux</class> to add the channel to the lookup table.
	 * @param channel
	 * @see org.dei.perla.channel.tcp.TcpChannelFactory
	 */
	public void addChannel(TcpChannel channel){
		int port = channel.getSrcPort();
		String ipAddress = channel.getSrcIpAddress();
		InetSocketAddress isAddr = null;
		try {
			isAddr = new InetSocketAddress(InetAddress.getByName(ipAddress), port);
			System.out.println(isAddr);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if(isAddr != null)
			demux.addChannel(isAddr, channel);
		else {
			//TODO gestire eccezione
			System.out.println("Error");
		}
	}
	
	public Demux getDemux(){
		return this.demux;
	}
	
	public void run(){
		while(true){
			try {
				SocketChannel socket = serverSocketChannel.accept();
				if(socket != null){
					new Handler(socket).start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public byte[] getLastReceived(){
		return lastReceived;
	}
	
	private class Handler extends Thread{
		
		private SocketChannel socketChannel;
		private ByteBuffer packetLengthBuffer;
		private ByteBuffer packetBuffer;
		
		public Handler(SocketChannel socketChannel){
			this.socketChannel = socketChannel;
			packetLengthBuffer = ByteBuffer.allocate(Integer.BYTES);
		}
		
		@Override
		public void run(){
			System.out.println("test!!!!!");
			while(true){
				try {
					System.out.println("leggi lunghezza pacchetto");
					socketChannel.read(packetLengthBuffer);
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
				try {
					byte[] packetLengthByte = packetLengthBuffer.array();
					ByteBuffer buffer = ByteBuffer.wrap(packetLengthByte);
					int packetLength = buffer.getInt();
					System.out.println("Lunghezza pacchetto = " + packetLength);
					packetBuffer = ByteBuffer.allocate(packetLength);
					socketChannel.read(packetBuffer);
					lastReceived = packetBuffer.array(); //utilizzato solo per i test
					demux.demux(packetBuffer.array(), socketChannel.getRemoteAddress());
					packetLengthBuffer.clear();
					packetBuffer.clear();
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}
	
	

}
