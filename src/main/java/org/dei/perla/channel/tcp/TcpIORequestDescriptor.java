package org.dei.perla.channel.tcp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.dei.perla.core.descriptor.IORequestDescriptor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "request")
public class TcpIORequestDescriptor extends IORequestDescriptor {

}
