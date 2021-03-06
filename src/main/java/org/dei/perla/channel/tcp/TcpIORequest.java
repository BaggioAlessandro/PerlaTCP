package org.dei.perla.channel.tcp;

import org.dei.perla.core.channel.IORequest;
import org.dei.perla.core.channel.Payload;

/**
 * 
 * @author Luca Baggi (2015)
 * @author Alessandro Baggio (2015)
 *
 */
public class TcpIORequest implements IORequest {
	
	private static final String PAYLOAD = "payload";
	
	private final String id;
	private Payload payload = null;
	private int type;

	protected TcpIORequest(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	public int getType(){
		return type;
	}
	
	public void setType(int type){
		this.type = type;
	}

	@Override
	public void setParameter(String name, Payload payload) {
		if(name.equals(PAYLOAD))
			this.payload = payload;
		else {
			throw new IllegalArgumentException("TCPIORequest have only 'Payload' as name parameter");
		}
	}
	
	public Payload getPayload(){
		return this.payload;
	}
	
	/**
	 * 
	 * Class used to identify various types of message sent by a device
	 * 
	 * @author Luca Baggi (2015)
	 * @author Luca Baggi (2015)
	 *
	 */
	public class TypeParameter{

		public static final int NORMAL = 0;
		public static final int CHANGE_IP = 1;
		public static final int SHUTDOWN = 2;
		public static final int DESC = 3;
		
		public static final int TYPE_LENGHT = Integer.BYTES;
	}
	
}
