package model;

import java.util.Optional;

import constant.Constants;
import exception.InvalidPackageException;

public class Package {
	
	private String content;
	private Optional<String> errorControl;
	private Optional<String> sourceNickname;
	private Optional<String> destinationNickname;
	private Optional<String> dataType;
	private Optional<String> message;
	
	public Package(String content) throws InvalidPackageException {
		validatePackage(content);
	}
	
	private void validatePackage(String content) throws InvalidPackageException {
		if(content == null)
			throw new InvalidPackageException("The content of the package cannot be null");
		if(!validPackage(content))
			throw new InvalidPackageException("Invalid format! Should be: " + Constants.DATA_PACKAGE_FORMAT);
		this.content = content;
		if(isToken())
			initializeToken();
		else
			initializeDataPackage();
	}
	
	private void initializeDataPackage() {
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
	
	private String dataPackageBeginning() {
		return Constants.DATA_PACKAGE_ID + ";";
	}
	
	private String[] fields(String content) {
		return content.substring(dataPackageBeginning().length()).split(":");
	}
	
	private boolean validPackage(String content) {
		if(content == null)
			return false;
		if(isToken(content))
			return true;
		if(!content.startsWith(dataPackageBeginning()))
			return false;
		String[] fields = fields(content);
		if(fields.length != 5 || !validErrorControl(fields[0]) || !validDataType(fields[3]))
			return false;
		return true;
	}
	
	private boolean validErrorControl(String errorControl) {
		return Constants.NOT_COPIED.equals(errorControl) ||
				Constants.NOT_COPIED_2.equals(errorControl) ||
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
	
}
