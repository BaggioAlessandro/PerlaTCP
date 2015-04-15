package org.dei.perla.server;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javassist.bytecode.ByteArray;

import org.apache.http.util.ByteArrayBuffer;
import org.dei.perla.channel.tcp.TcpIORequest;
import org.dei.perla.channel.tcp.TcpIORequest.TypeParameter;
import org.dei.perla.core.channel.IORequest;
import org.dei.perla.core.fpc.Fpc;

public class Demux {

	private Map<Long, Fpc> lookupTable;
	
	public Demux(){
		lookupTable = new HashMap<Long, Fpc>();
	}
	
	public void demux(byte[] request){
		
		int type = getType(request);
		switch(type){
			case TypeParameter.NORMAL:
				break;

			case TypeParameter.CHANGE_IP:
				break;

			case TypeParameter.SHUTDOWN:
				break;

			case TypeParameter.DESC:
				
				break;
		}
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
