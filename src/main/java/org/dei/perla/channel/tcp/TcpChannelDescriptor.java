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
	private String srcIpAddress;
	
	@XmlAttribute(required = true)
	private int srcPort;
	
	@XmlAttribute(required = true)
	private String destIpAddress;
	
	@XmlAttribute(required = true)
	private int destPort;

	
	public String getSrcIpAddress() {
		return srcIpAddress;
	}

	public int getSrcPort() {
		return srcPort;
	}

	public String getDestIpAddress() {
		return destIpAddress;
	}

	public int getDestPort() {
		return destPort;
	}
	
	public void setSrcPort(int srcPort){
		this.srcPort = srcPort;
	}
	
	

}
