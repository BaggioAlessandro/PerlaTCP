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
				new Handler(serverSocketChannel.accept()).start();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					serverSocketChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	public class Handler extends Thread{
		
		private SocketChannel socketChannel;
		ByteBuffer in;
		ByteBuffer out;
		
		public Handler(SocketChannel socketChannel){
			this.socketChannel = socketChannel;
			in = ByteBuffer.allocate(48);
			out = ByteBuffer.allocate(48);
		}
		
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
