package org.dei.perla.channel.tcp;

import org.dei.perla.core.channel.IORequest;
import org.dei.perla.core.channel.Payload;

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
	
	public class TypeParameter{

		public static final int NORMAL = 0;
		public static final int CHANGE_IP = 1;
		public static final int SHUTDOWN = 2;
		public static final int DESC = 3;
		
		public static final int TYPE_LENGHT = 1;
	}
	
	/*
	public enum TypeParameter{
		NORMAL(0),
		CHANGE_IP(1),
		SHUTDOWN(2),
		DESC(3);
		
		private final int type;
		public static final int TYPE_LENGHT = 4;
		
		private TypeParameter(int type) {	
			this.type = type;
		}

		public int getType(){
			return type;
		}

	}
	*/
}
