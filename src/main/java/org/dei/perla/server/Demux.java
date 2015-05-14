package org.dei.perla.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;






import org.dei.perla.channel.tcp.TcpChannel;
import org.dei.perla.channel.tcp.TcpIORequest.TypeParameter;
import org.dei.perla.core.channel.ByteArrayPayload;
import org.dei.perla.core.channel.Payload;

public class Demux {

	private Map<SocketAddress, TcpChannel> lookupTable;

	private Server server;
	
	public Demux(Server server){
		lookupTable = new HashMap<SocketAddress, TcpChannel>();
		this.server = server;
	}
	
	public void demux(byte[] request, SocketAddress sender){
		
		int type = getType(request);
		System.out.println("Il tipo del messaggio è " + type);
		byte[] bytePayload = removeHeader(request);
		
		switch(type){
			case TypeParameter.NORMAL:	
				lookupTable.get(sender).notifyRequestCompleted(new ByteArrayPayload(bytePayload));
				break;

			case TypeParameter.CHANGE_IP:
				InetSocketAddress address = getSocketAddress(bytePayload);
				if(address != null)
					lookupTable.get(sender).changeSocket(address);
				else
					//TODO lancia eccezzione o altro?
				break;

			case TypeParameter.SHUTDOWN:
				break;

			case TypeParameter.DESC:
				Payload payload = new ByteArrayPayload(bytePayload);
				server.notifyDescriptor(payload);	
				break;
		}
	}

	public void addChannel(SocketAddress address, TcpChannel channel){
		lookupTable.put(address, channel);
	}
	
	private InetSocketAddress getSocketAddress(byte[] request) {
		byte[] bytePort = Arrays.copyOfRange(request, 0, Integer.BYTES);
		ByteBuffer wrapped = ByteBuffer.wrap(bytePort); // big-endian by default
		int port = wrapped.getInt();
		
		byte[] byteIP = Arrays.copyOfRange(request, Integer.BYTES, request.length);
		
		InetAddress ip = null;
		try {
			ip = InetAddress.getByAddress(byteIP);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		return new InetSocketAddress(ip, port);

	}
	
	public Map<SocketAddress, TcpChannel> getLookupTable() {
		return lookupTable;
	}
	
	private byte[] removeHeader(byte[] request){
		return Arrays.copyOfRange(request, TypeParameter.TYPE_LENGHT, request.length);
	}
	
	/**
	 * Return the type of a message, converting bytes in input.
	 * @param bytes message received
	 * @return {@code Integer} conversion
	 */
	public static int getType(byte[] bytes){
		int start = 0;
		int offset = TypeParameter.TYPE_LENGHT;
		
		byte[] byteType = Arrays.copyOfRange(bytes, start, start + offset);
		
		
		ByteBuffer wrapped = ByteBuffer.wrap(byteType);
		//wrapped.order(ByteOrder.LITTLE_ENDIAN);
		//wrapped.put(byteType); // big-endian by default
		int num = wrapped.getInt();
		System.out.println(num);
		return num;
	}
	
}
