package model;

import java.util.Optional;

import constant.Constants;
import exception.InvalidPacketException;

public class Packet {
	
	private String content;
	private Optional<String> errorControl;
	private Optional<String> sourceNickname;
	private Optional<String> destinationNickname;
	private Optional<String> dataType;
	private Optional<String> message;
	
	public Packet(String content) throws InvalidPacketException {
		validatePacket(content);
	}
	
	private void validatePacket(String content) throws InvalidPacketException {
		if(content == null)
			throw new InvalidPacketException("The content of the packet cannot be null");
		if(!validPacket(content))
			throw new InvalidPacketException("This packet is invalid:\n" + content + "\nCorrect format:\n" + Constants.DATA_PACKET_FORMAT);
		this.content = content;
		if(isToken())
			initializeToken();
		else
			initializeDataPacket();
	}
	
	private void initializeDataPacket() {
		String[] fields = fields(content);
		errorControl = Optional.ofNullable(fields[0]);
		sourceNickname = Optional.ofNullable(fields[1]);
		destinationNickname = Optional.ofNullable(fields[2]);
		dataType = Optional.ofNullable(fields[3]);
		message = Optional.ofNullable(fields[4]);
	}
	
	private void initializeToken() {
		errorControl = Optional.empty();
		sourceNickname = Optional.empty();
		destinationNickname = Optional.empty();
		dataType = Optional.empty();
		message = Optional.empty();
	}
	
	private String dataPacketBeginning() {
		return Constants.DATA_PACKET_ID + ";";
	}
	
	private String[] fields(String content) {
		return content.substring(dataPacketBeginning().length()).split(":");
	}
	
	private boolean validPacket(String content) {
		if(content == null)
			return false;
		if(isToken(content))
			return true;
		if(!content.startsWith(dataPacketBeginning()) || !validFields(fields(content)))
			return false;
		return true;
	}
	
	private boolean validFields(String[] fields) {
		return fields.length == 5 && validErrorControl(fields[0]) &&
				!Constants.BROADCAST_ID.equals(fields[1]) && validDataType(fields[3]);
	}
	
	private boolean validErrorControl(String errorControl) {
		return Constants.NOT_COPIED.equals(errorControl) ||
				Constants.ERROR.equals(errorControl) ||
				Constants.OK.equals(errorControl);
	}
	
	private boolean validDataType(String dataType) {
		return Constants.FILE_ID.equals(dataType) ||
				Constants.MESSAGE_ID.equals(dataType);
	}
	
	private boolean isToken(String content) {
		return Constants.TOKEN.equals(content);
	}
	
	public boolean isToken() {
		return Constants.TOKEN.equals(content);
	}
	
	public String getContent() { return content; }
	public Optional<String> getErrorControl() { return errorControl; }
	public Optional<String> getSourceNickname() { return sourceNickname; }
	public Optional<String> getDestinationNickname() { return destinationNickname; }
	public Optional<String> getDataType() { return dataType; }
	public Optional<String> getMessage() { return message; }
	
	public Packet readMessage() {
		errorControl = Optional.ofNullable(Constants.OK);
		content = content.replaceFirst(Constants.NOT_COPIED, Constants.OK);
		return this;
	}
	
	public Packet introduceError() {
		errorControl = Optional.ofNullable(Constants.ERROR);
		content = content.replaceFirst(Constants.NOT_COPIED, Constants.ERROR);
		return this;
	}
	
	public static Packet generateToken() throws InvalidPacketException {
		return new Packet(Constants.TOKEN);
	}
	
}
