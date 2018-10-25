package model;

import static java.util.stream.Collectors.toList;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;

import constant.Constants;
import exception.InvalidConfigurationException;

public class Configuration {
	
	private String nextIP;
	private String nextPort;
	private String nickname;
	private long sleepDuration;
	private boolean tokenGenerator;
	private InetAddressValidator validator;
	
	public Configuration(String path) throws InvalidConfigurationException {
		validator = InetAddressValidator.getInstance();
		validateConfiguration(path);
	}
	
	private void validateConfiguration(String path) throws InvalidConfigurationException {
		List<String> lines;
		try(Stream<String> stream = Files.lines(Paths.get(path))) {
			lines = stream.collect(toList());
		} catch(Exception exception) {
			exception.printStackTrace();
			throw new InvalidConfigurationException(exception);
		}
		if(!validFormat(lines))
			throw new InvalidConfigurationException("Invalid format! Should be:\n" + Constants.CONFIGURATION_FORMAT);
		initializeConfiguration(lines);
	}
	
	private void initializeConfiguration(List<String> lines) {
		String[] address = lines.get(0).split(":");
		nextIP = address[0];
		nextPort = address[1];
		nickname = lines.get(1);
		sleepDuration = Long.parseLong(lines.get(2));
		tokenGenerator = Boolean.parseBoolean(lines.get(3));
	}
	
	private boolean validFormat(List<String> lines) {
		if(lines.size() != Constants.CONFIGURATION_LINES)
			return false;
		String[] address = lines.get(0).split(":");
		return address.length == 2 && validator.isValid(address[0]) && StringUtils.isNumeric(address[0]) &&
				!Constants.BROADCAST_ID.equals(lines.get(1)) && StringUtils.isNumeric(lines.get(2)) && isBoolean(lines.get(3));
	}
	
	private boolean isBoolean(String value) {
		return "true".equals(value) || "false".equals(value);
	}
	
	public String getNextIP() { return nextIP; }
	public String getNextPort() { return nextPort; }
	public String getNickname() { return nickname; }
	public long getSleepDuration() { return sleepDuration; }
	public boolean isTokenGenerator() { return tokenGenerator; }
	
}
