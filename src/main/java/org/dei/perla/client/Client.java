package org.dei.perla.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Client used for tests
 * @author Luca Baggi (2015)
 * @author Alessandro Baggio (2015)
 *
 */
public class Client {
	
	private SocketChannel socket;
	private ServerSocketChannel serverSocketChannel;
	private byte[] lastReceived;
	private InetSocketAddress address;

	public Client(InetSocketAddress address, int port){
		try {
			this.address = address;
			//Creation of serverSocket to allow connection to socket inside the Channel and
			//read the messages from the channel
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(port));
			serverSocketChannel.configureBlocking(false);
			
			//Open the Socket used to communicate with the server
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
	
	public void changeIP() throws IOException{
		String ipAddress = ((InetSocketAddress)socket.getLocalAddress()).getHostString();
		int port = ((InetSocketAddress)socket.getLocalAddress()).getPort();
		try {
			socket.close();
			socket = SocketChannel.open();
			socket.connect(address);
			
			sendChangeIP(ipAddress,port);
		} catch (IOException e) {
			e.printStackTrace();
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
	
	public void sendChangeIP(String strigIP, int port){
		InetAddress ip = null;
		try {
			ip = InetAddress.getByName(strigIP);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		byte[] byteIP = ip.getAddress();
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 2 + byteIP.length).putInt(1);
		buffer.putInt(port);
		buffer.put(byteIP);
		sendPacket(buffer.array());
	}

	public byte[] getLastReceived(){
		return lastReceived;
	}
	
	private class Handler extends Thread{
		
		private SocketChannel socketChannel;
		private ByteBuffer packetLengthBuffer;
		private ByteBuffer packetBuffer;
		
		public Handler(SocketChannel socketChannel){
			packetLengthBuffer = ByteBuffer.allocate(Integer.BYTES);
			this.socketChannel = socketChannel;
		}
		
		@Override
		public void run(){
			while(true){
				int num = 0;
				try {
					num = socketChannel.read(packetLengthBuffer);
					if(num == -1)
						break;
				
				} catch (IOException e) {
					e.printStackTrace();
				}
				byte[] packetLengthByte = packetLengthBuffer.array();
				ByteBuffer buffer = ByteBuffer.wrap(packetLengthByte);
				int packetLength = buffer.getInt();
				packetBuffer = ByteBuffer.allocate(packetLength);
				try {
					socketChannel.read(packetBuffer);
				} catch (IOException e) {
					e.printStackTrace();
				}
				lastReceived = packetBuffer.array(); //used for test
				System.out.println("Client riceve pacchetto " + num);
				packetLengthBuffer.clear();
				packetBuffer.clear();
			}
		}
	}

}
