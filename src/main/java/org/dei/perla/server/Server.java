package org.dei.perla.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
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
