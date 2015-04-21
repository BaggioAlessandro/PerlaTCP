package org.dei.perla.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {

	public static void main(String[] args) throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.connect(new InetSocketAddress("127.0.0.1", 9999));
		
		byte[] packet = new byte[48];
		
		long id = 120;
		byte[] id_to_byte = longToBytes(id);
		
		for(int i = 0; i < Long.BYTES; i++){
			packet[i] = id_to_byte[i];
		}
		
		ByteBuffer buff = ByteBuffer.wrap(packet);
		
		buff.flip();
		
		socketChannel.write(buff);
	}

	private static byte[] longToBytes(long x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(x);
	    return buffer.array();
	}
}
