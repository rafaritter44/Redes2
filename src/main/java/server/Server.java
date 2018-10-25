package server;

import model.Configuration;

public class Server implements Runnable {
	
	private static class ServerHolder {
		static final Server INSTANCE = new Server();
	}
	
	private Configuration config;
	
	private Server() {}
	
	public static Server getInstance() {
		return ServerHolder.INSTANCE;
	}
	
	public Server configure(Configuration config) {
		this.config = config;
		return this;
	}

	@Override
	public void run() {
		//TODO
	}
	
}
