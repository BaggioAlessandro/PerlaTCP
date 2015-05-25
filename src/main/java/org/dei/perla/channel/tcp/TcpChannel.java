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

/**
 * 
 * @author Luca Baggi (2015)
 * @author Alessandro Baggio (2015)
 *
 */
public class TcpChannel extends AbstractAsyncChannel {
	
	private Logger logger = Logger.getLogger(TcpChannel.class);
	private String ipAddress;
	private int srcPort;
	private int destPort;
	private SocketChannel socket;
	
	private Map<Integer, IOHandler> mapperHandler;
	private Map<Integer, IORequest> mapperRequest;
	private int sequence;
	
	public TcpChannel(String id, String ipAddress, int srcPort, int destPort) {
		super(id);
		this.ipAddress = ipAddress;
		this.srcPort = srcPort;
		this.destPort = destPort;
		this.sequence = 1;
		this.mapperHandler = new HashMap<Integer, IOHandler>();
		this.mapperRequest = new HashMap<Integer, IORequest>();

		try {
			socket = SocketChannel.open();
			socket.connect(new InetSocketAddress(InetAddress.getByName(this.ipAddress), this.destPort));
			System.out.println("Channel connesso con ip " + ipAddress + " e porta " + destPort);
			socket.configureBlocking(false);
		} catch (IOException e) {
			logger.error("an error has occurred while creating socket connection", e);
			e.printStackTrace();
		}	
	}

	@Override
	public synchronized void handleRequest(IORequest request, IOHandler handler) throws ChannelException,
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
		
		byte[] payloadByteArray = tcpRequest.getPayload().asString().getBytes();
		System.out.println(tcpRequest.getPayload().asString());
		
		//add as header of the payload the id of the current sequence
		ByteBuffer bufferToSend = ByteBuffer.allocate(Integer.BYTES + payloadByteArray.length).putInt(sequence);
		bufferToSend.put(payloadByteArray);
		
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
	
	public synchronized void notifyRequestCompleted(Payload payload){
		ByteBuffer buff = payload.asByteBuffer();
		byte[] byteID = new byte[buff.capacity()];
		buff.get(byteID, 0, byteID.length);
		ByteBuffer wrapped = ByteBuffer.wrap(byteID); // big-endian by default
		int requestId = getRequestId(wrapped);
		
		System.out.println("richiesta numero " + requestId);
		
		byte[] effectiveByteArray = removeHeader(wrapped.array());
		
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

	public String getipAddress() {
		return ipAddress;
	}

	public int getSrcPort() {
		return srcPort;
	}

	public int getDestPort() {
		return destPort;
	}

	private int getRequestId(ByteBuffer buff) {
		int start = 0;
		int offset = Integer.BYTES;
		byte[] byteID = new byte[Integer.BYTES];
		buff.get(byteID, start, offset);
		ByteBuffer wrapped = ByteBuffer.wrap(byteID); // big-endian by default
		int num = wrapped.getInt();
		return num;
	}
	
	private byte[] removeHeader(byte[] request){
		return Arrays.copyOfRange(request, Integer.BYTES, request.length);
	}
	
	protected int getSequence(){
		return this.sequence;
	}
	
	protected Map<Integer, IOHandler> getMapperHandler(){
		return this.mapperHandler;
	}
	
	protected Map<Integer, IORequest> getMapperRequest(){
		return this.mapperRequest;
	}
	
	protected SocketChannel getSocket(){
		return this.socket;
	}

	public void closeConnection() {
		try {
			socket.close();
			System.out.println("Socket chiuso lato Channel");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
