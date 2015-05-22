package org.dei.perla.channel.tcp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.dei.perla.core.descriptor.ChannelDescriptor;

@XmlRootElement(name = "channel")
@XmlAccessorType(XmlAccessType.FIELD)
public class TcpChannelDescriptor extends ChannelDescriptor {
	
	@XmlAttribute(required = true)
	private String ipAddress;
	
	@XmlAttribute(required = true)
	private int srcPort;
	
	@XmlAttribute(required = true)
	private int destPort;

	
	public String getipAddress() {
		return ipAddress;
	}

	public int getSrcPort() {
		return srcPort;
	}

	public int getDestPort() {
		return destPort;
	}
	
	public void setSrcPort(int srcPort){
		this.srcPort = srcPort;
	}
	
	

}
