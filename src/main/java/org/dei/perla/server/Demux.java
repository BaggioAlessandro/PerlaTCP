package org.dei.perla.server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


import org.dei.perla.channel.tcp.TcpChannel;
import org.dei.perla.channel.tcp.TcpIORequest.TypeParameter;
import org.dei.perla.core.channel.ByteArrayPayload;
import org.dei.perla.core.channel.Payload;

public class Demux {

	private Map<InetSocketAddress, TcpChannel> lookupTable;
	private Server server;
	
	public Demux(Server server){
		lookupTable = new HashMap<InetSocketAddress, TcpChannel>();
		this.server = server;
	}
	
	public void demux(byte[] request){
		
		int type = getType(request);
		System.out.println("Il tipo del messaggio è " + type);
		byte[] bytePayload = removeHeader(request);
		
		switch(type){
			case TypeParameter.NORMAL:		
				//TODO Lookup della table e invio del payload all'fpc corretto
				break;

			case TypeParameter.CHANGE_IP:
				//TODO 
				break;

			case TypeParameter.SHUTDOWN:
				break;

			case TypeParameter.DESC:
				Payload payload = new ByteArrayPayload(bytePayload);
				server.notifyDescriptor(payload);	
				break;
		}
	}
	
	public void addCannel(InetSocketAddress address, TcpChannel channel){
		lookupTable.put(address, channel);
	}
	
	private byte[] removeHeader(byte[] request){
		return Arrays.copyOfRange(request, Long.BYTES + TypeParameter.TYPE_LENGHT, request.length);
	}
	
	/**
	 * Return the type of a message, converting bytes in input.
	 * @param bytes message received
	 * @return {@code Integer} conversion
	 */
	public static int getType(byte[] bytes){
		int start = Long.BYTES;
		int offset = TypeParameter.TYPE_LENGHT;
		
		byte[] byteType = Arrays.copyOfRange(bytes, start, start + offset);
		ByteBuffer wrapped = ByteBuffer.wrap(byteType); // big-endian by default
		int num = wrapped.getInt();
		return num;
	}
	
}
