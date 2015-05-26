package org.dei.perla.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 
 * @author Luca Baggi (2015)
 * @author Alessandro Baggio (2015)
 *
 */
public class Client {
	
	private SocketChannel socket;
	private ServerSocketChannel serverSocketChannel;
	private byte[] lastReceived;

	public Client(InetSocketAddress address, int port){
		try {
			//Creazione del serverSocket per permettere la connessione al socket del Channel e leggere i 
			//messaggi del Channel
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(port));
			serverSocketChannel.configureBlocking(false);
			
			//Apertura del socket per comunicare con il Server
			socket = SocketChannel.open();
			socket.connect(address);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getSendingPort() throws IOException{
		InetSocketAddress addr = (InetSocketAddress) socket.getLocalAddress();
		return addr.getPort();
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
	
	public void sendPacket(byte[] packet){
		
		int packetLength = packet.length;
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + packet.length).putInt(packetLength);	
		buffer.put(packet);
		
		buffer.flip();
		
		while(buffer.hasRemaining()) {
			try {
				socket.write(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("invio finito");
	}
	
	public void sendChangeIP(String ip, int port){
		
	}

	public byte[] getLastReceived(){
		return lastReceived;
	}
	
	private class Handler extends Thread{
		
		private SocketChannel socketChannel;
		private ByteBuffer in;
		
		public Handler(SocketChannel socketChannel){
			this.socketChannel = socketChannel;
			in = ByteBuffer.allocate(48);
		}
		
		@Override
		public void run(){
			while(true){
				try {
					socketChannel.read(in);
				
				} catch (IOException e) {
					e.printStackTrace();
				}
				lastReceived = in.array();
				in.clear();
			}
		}
	}

}
