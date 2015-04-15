package org.dei.perla.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server {
	
	private Demux demux;	
	ServerSocketChannel serverSocketChannel;
	
	public Server(){
		demux = new Demux();
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(9999));
			serverSocketChannel.configureBlocking(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		while(true){
			try {
				SocketChannel socketChannel = serverSocketChannel.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	public class MyCommunication extends Thread{
		
		SocketChannel socketChannel;
		
		public MyCommunication(SocketChannel socketChannel){
			this.socketChannel = socketChannel;
		}
		
		@Override
		public void run()
	}
	*/
	

}
