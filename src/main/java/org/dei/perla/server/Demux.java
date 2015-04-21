package org.dei.perla.server;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dei.perla.channel.tcp.TcpChannelFactory;
import org.dei.perla.channel.tcp.TcpIORequest.TypeParameter;
import org.dei.perla.channel.tcp.TcpIORequestBuilderFactory;
import org.dei.perla.core.channel.ByteArrayPayload;
import org.dei.perla.core.channel.ChannelFactory;
import org.dei.perla.core.channel.IORequestBuilderFactory;
import org.dei.perla.core.channel.Payload;
import org.dei.perla.core.descriptor.DeviceDescriptor;
import org.dei.perla.core.descriptor.DeviceDescriptorParseException;
import org.dei.perla.core.descriptor.JaxbDeviceDescriptorParser;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.FpcFactory;
import org.dei.perla.core.fpc.base.BaseFpcFactory;
import org.dei.perla.core.message.MapperFactory;
import org.dei.perla.core.message.json.JsonMapperFactory;

public class Demux {

	private Map<Long, Fpc> lookupTable;
	
	public Demux(){
		lookupTable = new HashMap<Long, Fpc>();
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
				//TODO cosa passare al posto di null? 
				JaxbDeviceDescriptorParser descriptorParser = new JaxbDeviceDescriptorParser(null);
				try {
					DeviceDescriptor deviceDescriptor = descriptorParser.parse(payload.asInputStream());
				} catch (DeviceDescriptorParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
		}
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
	
	public static FpcFactory createFpcFactory() {
		List<MapperFactory> mhfList = new ArrayList<>();
		mhfList.add(new JsonMapperFactory());

		List<ChannelFactory> chfList = new ArrayList<>();
		chfList.add(new TcpChannelFactory());

		List<IORequestBuilderFactory> rbfList = new ArrayList<>();
		rbfList.add(new TcpIORequestBuilderFactory());

		return new BaseFpcFactory(mhfList, chfList, rbfList);
	}
}
