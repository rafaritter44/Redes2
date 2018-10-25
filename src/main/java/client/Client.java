package client;

public class Client implements Runnable {

	private static class ClientHolder {
		static final Client INSTANCE = new Client();
	}
	
	private Client() {}
	
	public static Client getInstance() {
		return ClientHolder.INSTANCE;
	}
	
	@Override
	public void run() {
		//TODO
	}
	
}
