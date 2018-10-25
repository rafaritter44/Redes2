package constant;

public class Constants {
	
	public static final String TOKEN = "1234";
	public static final String DATA_PACKET_ID = "2345";
	
	public static final String FILE_ID = "A";
	public static final String MESSAGE_ID = "M";
	
	public static final String NOT_COPIED = "naocopiado";
	public static final String ERROR = "erro";
	public static final String OK = "OK";
	
	public static final String BROADCAST_ID = "TODOS";
	
	public static final String DATA_PACKET_FORMAT = DATA_PACKET_ID +
			";<error_control>:<source_nickname>:<destination_nickname>:<data_type>:<message_or_file_data>";
	
	public static final long CONFIGURATION_LINES = 4L;
	
	public static final String CONFIGURATION_FORMAT =
			"<next_ip>:<next_port>\n"
			+ "<nickname> (of current machine)\n"
			+ "<sleep_duration>\n"
			+ "<token_generator> (true or false)";
	
	public static final int QUEUE_LIMIT = 10;
	
}
