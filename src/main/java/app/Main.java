package app;

import client.Client;
import exception.InvalidConfigurationException;
import model.Configuration;
import server.Server;

public class Main {

	public static void main(String args[]) {
		Configuration config;
		try {
			config = new Configuration(args[0]);
		} catch(InvalidConfigurationException exception) {
			exception.printStackTrace();
			return;
		}
		new Thread(Server.getInstance().configure(config)).start();
		new Thread(Client.getInstance()).start();
	}

}
