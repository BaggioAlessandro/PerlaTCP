package org.dei.perla.channel.tcp;

import org.dei.perla.core.channel.IORequest;
import org.dei.perla.core.channel.Payload;

public class TcpIORequest implements IORequest {
	
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

	@Override
	public void setParameter(String name, Payload payload) {
		//TODO controllare		
	}
	
}
