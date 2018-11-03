package com.github.rafaritter44.redes.t2.model;

import com.github.rafaritter44.redes.t2.constant.Constants;
import com.github.rafaritter44.redes.t2.exception.InvalidPacketException;
import com.github.rafaritter44.redes.t2.server.Server;

public class ClientPacket {
	
	private String input;
	
	public ClientPacket(String input) throws InvalidPacketException {
		validatePacket(input);
	}
	
	public String getInput() { return input; }
	
	public String getContent() {
		return Constants.DATA_PACKET_ID + ";" + Constants.NOT_COPIED + ":" +
				Server.getInstance().getConfig().getNickname() + ":" + input;
	}
	
	private void validatePacket(String input) throws InvalidPacketException {
		if(!validFields(input.split(":")))
			throw new InvalidPacketException("This packet is invalid:\n" + input + "\nCorrect format:\n" + Constants.CLIENT_PACKET_FORMAT);
		this.input = input;
	}
	
	private boolean validFields(String[] fields) {
		return fields.length == 3 && validDataType(fields[1]);
	}
	
	private boolean validDataType(String dataType) {
		return Constants.FILE_ID.equals(dataType) ||
				Constants.MESSAGE_ID.equals(dataType);
	}

}
