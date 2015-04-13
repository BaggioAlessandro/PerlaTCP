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
	private int port;
	
	/**
	 * 
	 * @return the Ip Address of the device 
	 */
	public String getIpAddress(){
		return this.ipAddress;
	}
	
	/**
	 * 
	 * @return the port where the device is listening for tcp/ip requests
	 */
	public int getPort(){
		return this.port;
	}
	

}
