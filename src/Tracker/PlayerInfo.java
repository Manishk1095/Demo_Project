package Tracker;

import java.io.Serializable;

public class PlayerInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String ipAddress;
	
	private int portNumber;
	
	public PlayerInfo(String id, String ipAdress, int portNumber) {
		this.setId(id);
		this.setIpAddress(ipAdress);
		this.setPortNumber(portNumber);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	@Override
	public String toString() {
		return "PlayerInfo [id=" + id + ", ipAddress=" + ipAddress + ", portNumber=" + portNumber + "]";
	}

}
