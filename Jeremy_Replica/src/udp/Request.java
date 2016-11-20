package udp;

import java.io.Serializable;

import enums.UdpRequestType;

public class Request implements Serializable {
	private static final long serialVersionUID = 1L;
	private UdpRequestType requestType;

	public Request(UdpRequestType requestType) {
		super();
		this.requestType = requestType;
	}

	public UdpRequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(UdpRequestType requestType) {
		this.requestType = requestType;
	}
}
