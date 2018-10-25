package exception;

@SuppressWarnings("serial")
public class InvalidConfigurationException extends Exception {
	
	public InvalidConfigurationException(String message) {
		super(message);
	}
	
	public InvalidConfigurationException(Exception exception) {
		super(exception);
	}
	
}
