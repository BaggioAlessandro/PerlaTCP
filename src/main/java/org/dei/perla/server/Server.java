package org.dei.perla.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Optional;

import org.dei.perla.channel.tcp.TcpChannelFactory;
import org.dei.perla.channel.tcp.TcpIORequestBuilderFactory;
import org.dei.perla.core.channel.IOHandler;
import org.dei.perla.core.channel.Payload;
import org.dei.perla.core.utils.Conditions;

public class Server {
	
	private Demux demux;	
	ServerSocketChannel serverSocketChannel;
	TcpChannelFactory channelFactory;
	TcpIORequestBuilderFactory requestBuilderFactory;
	private IOHandler factoryHandler = null;
	
	public Server(){
		channelFactory = new TcpChannelFactory(this);
		requestBuilderFactory = TcpIORequestBuilderFactory.getInstance();
		demux = new Demux(this);
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(9999));
			serverSocketChannel.configureBlocking(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public TcpChannelFactory getChannelFactory(){
		return this.channelFactory;
	}
	
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
	
	public void run(){
		while(true){
			try {
				System.out.println("Waiting for connection");
				SocketChannel socket = serverSocketChannel.accept();
				if(socket != null){
					new Handler(socket).start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	public class Handler extends Thread{
		
		private SocketChannel socketChannel;
		ByteBuffer in;
		ByteBuffer out;
		
		public Handler(SocketChannel socketChannel){
			System.out.println("prova");
			this.socketChannel = socketChannel;
			in = ByteBuffer.allocate(48);
			out = ByteBuffer.allocate(48);
		}
		
		@SuppressWarnings("unused")
		@Override
		public void run(){
			try {
				int bytesRead = socketChannel.read(in);
			
			} catch (IOException e) {
				e.printStackTrace();
			}
			demux.demux(in.array());
		}
	}
	
	

}
