package org.dei.perla.channel.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.dei.perla.core.channel.ByteArrayPayload;
import org.dei.perla.core.channel.ChannelException;
import org.dei.perla.core.channel.IOHandler;
import org.dei.perla.core.channel.IORequest;
import org.dei.perla.core.channel.Payload;

public class TcpChannel extends AbstractAsyncChannel {
	
	private Logger logger = Logger.getLogger(TcpChannel.class);
	private String ipAddress;
	private int port;
	private SocketChannel socket;
	
	private Map<Integer, IOHandler> mapperHandler;
	private Map<Integer, IORequest> mapperRequest;
	private int sequence;
	
	public TcpChannel(String id, String ipAddress, int port) {
		super(id);
		this.ipAddress = ipAddress;
		this.port = port;
		this.sequence = 1;
		this.mapperHandler = new HashMap<Integer, IOHandler>();
		this.mapperRequest = new HashMap<Integer, IORequest>();

		try {
			socket = SocketChannel.open();
			socket.connect(new InetSocketAddress(InetAddress.getByName(this.ipAddress), this.port));
			socket.configureBlocking(false);
		} catch (IOException e) {
			logger.error("an error has occurred while creating socket connection", e);
			e.printStackTrace();
		}	
	}

	@Override
	public void handleRequest(IORequest request, IOHandler handler) throws ChannelException,
			InterruptedException {
		TcpIORequest tcpRequest;
		
		if (!(request instanceof TcpIORequest)) {
			String message = "Channel request error: Cannot cast from "
					+ request.getClass().getSimpleName() + " to "
					+ TcpIORequest.class.getSimpleName();
			logger.error(message);
			throw new ClassCastException(message);
		}
		
		tcpRequest = (TcpIORequest) request;

		ByteBuffer payloadBuffer = tcpRequest.getPayload().asByteBuffer();	//need to return the array because the byteBuffer is read
																		//only
		
		//add as header of the payload the id of the current sequence
		ByteBuffer bufferToSend = ByteBuffer.allocate(Integer.BYTES + payloadBuffer.capacity()).putInt(sequence);
		
		//TODO Verificare che i parametri siano giusti
		bufferToSend.put(payloadBuffer.array(), Integer.BYTES, payloadBuffer.capacity());
		
		bufferToSend.flip();

		while(bufferToSend.hasRemaining()) {
			try {
				socket.write(bufferToSend);
			} catch (IOException e) {
				logger.error("an error has occurred while sending data in TCP channel", e);
				e.printStackTrace();
			}
			mapperHandler.put(sequence, handler);
			mapperRequest.put(sequence, request);
			sequence++;
		}
		return;
	}
	
	public void notifyRequestCompleted(Payload payload){
		ByteBuffer buff = payload.asByteBuffer();
		int requestId = getRequestId(buff);
		
		byte[] effectiveByteArray = removeHeader(buff.array());
		
		ByteArrayPayload effectivePayload = new ByteArrayPayload(effectiveByteArray);
		
		if(mapperHandler.containsKey(requestId)){
			mapperHandler.get(requestId).complete(mapperRequest.get(requestId), Optional.ofNullable(effectivePayload));
		}
		else{
			notifyAsyncData(effectivePayload);
		}
	}
	
	public synchronized void changeSocket(InetSocketAddress address){
		try {
			socket.close();
		} catch (IOException e) {
			logger.error("an error has occurred while closing socket for the socket change", e);
			e.printStackTrace();
		}
		
		try {
			socket = SocketChannel.open();			
			socket.connect(address);
		} catch (IOException e) {
			logger.error("an error has occurred while creating new connection during the change of IP", e);
			e.printStackTrace();
		}
	}
	
	public String getIpAddress() {
		return ipAddress;
	}

	public int getPort() {
		return port;
	}

	private int getRequestId(ByteBuffer buff) {
		int start = 0;
		int offset = Integer.BYTES;
		
		byte[] byteID = Arrays.copyOfRange(buff.array(), start, start + offset);
		ByteBuffer wrapped = ByteBuffer.wrap(byteID); // big-endian by default
		int num = wrapped.getInt();
		return num;
	}
	
	private byte[] removeHeader(byte[] request){
		return Arrays.copyOfRange(request, Integer.BYTES, request.length);
	}
}
