package com.github.rafaritter44.redes.t2;

import com.github.rafaritter44.redes.t2.client.Client;
import com.github.rafaritter44.redes.t2.exception.InvalidConfigurationException;
import com.github.rafaritter44.redes.t2.model.Configuration;
import com.github.rafaritter44.redes.t2.server.Server;

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
