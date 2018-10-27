package model;

import constant.Constants;
import exception.InvalidPacketException;
import server.Server;

public class ClientPacket {
	
	private String input;
	
	public ClientPacket(String input) throws InvalidPacketException {
		validatePacket(input);
	}
	
	public String getContent() {
		return Constants.DATA_PACKET_ID + ";" + Constants.NOT_COPIED + ":" +
				Server.getInstance().getConfig().getNickname() + ":" + input.replace(' ', ':');
	}
	
	private void validatePacket(String input) throws InvalidPacketException {
		if(!validFields(input.split(" ")))
			throw new InvalidPacketException("This packet is invalid:\n" + input + "\nCorrect format:\n" + Constants.CLIENT_PACKET_FORMAT);
		this.input = input;
	}
	
	private boolean validFields(String[] fields) {
		return fields.length == 3 && validDataType(fields[0]);
	}
	
	private boolean validDataType(String dataType) {
		return Constants.FILE_ID.equals(dataType) ||
				Constants.MESSAGE_ID.equals(dataType);
	}

}
