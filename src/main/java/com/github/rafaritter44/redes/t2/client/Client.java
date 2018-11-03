package com.github.rafaritter44.redes.t2.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.rafaritter44.redes.t2.constant.Constants;
import com.github.rafaritter44.redes.t2.exception.InvalidPacketException;
import com.github.rafaritter44.redes.t2.model.ClientPacket;

public class Client implements Runnable {

	private static class ClientHolder {
		static final Client INSTANCE = new Client();
	}
	
	private Map<String, Boolean> messagesWithError;
	private BufferedReader reader;

	private Client() {
		messagesWithError = Collections.synchronizedMap(new HashMap<>());
		reader = new BufferedReader(new InputStreamReader(System.in));
	}

	public static Client getInstance() {
		return ClientHolder.INSTANCE;
	}
	
	public void updateMessageWithError(String input) {
		messagesWithError.put(input, true);
	}
	
	public boolean isMessageWithError(String input) {
		return messagesWithError.get(input);
	}

	@Override
	public void run() {
		try(DatagramSocket socket = new DatagramSocket(Constants.CLIENT_PORT)) {
			while(true) {
				ClientPacket packet;
				try {
					packet = new ClientPacket(reader.readLine());
				} catch(InvalidPacketException exception) {
					System.out.println(exception.getMessage());
					continue;
				}
				messagesWithError.put(packet.getInput(), false);
				byte[] data = packet.getContent().getBytes();
				DatagramPacket datagramPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), Constants.SERVER_PORT);
				socket.send(datagramPacket);
			}
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

}
