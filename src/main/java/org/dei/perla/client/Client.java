package org.dei.perla.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.dei.perla.server.Server.Handler;

public class Client {
	
	private SocketChannel socket;
	private ServerSocketChannel serverSocketChannel;

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
	
	public void sendPacket(byte[] packet){
		ByteBuffer buffer = ByteBuffer.wrap(packet);
		
		buffer.flip();
		
		while(buffer.hasRemaining()) {
			try {
				socket.write(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
