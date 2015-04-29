package org.dei.perla.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Client {
	
	private SocketChannel serverSocket;

	public Client(SocketAddress address){
		try {
			serverSocket = SocketChannel.open();
			serverSocket.connect(address);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendPacket(byte[] packet){
		ByteBuffer buffer = ByteBuffer.wrap(packet);
		
		buffer.flip();
		
		while(buffer.hasRemaining()) {
			try {
				serverSocket.write(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
