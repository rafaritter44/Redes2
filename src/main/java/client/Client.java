package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import constant.Constants;
import exception.InvalidPacketException;
import model.ClientPacket;

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
	
	public void updateMessageWithError(String content) {
		messagesWithError.put(content, true);
	}
	
	public boolean isMessageWithError(String content) {
		return messagesWithError.get(content);
	}

	@Override
	public void run() {
		try(DatagramSocket socket = new DatagramSocket()) {
			while(true) {
				ClientPacket packet;
				try {
					packet = new ClientPacket(reader.readLine());
				} catch(InvalidPacketException exception) {
					System.out.println(exception.getMessage());
					continue;
				}
				String content = packet.getContent();
				messagesWithError.put(content, false);
				byte[] data = content.getBytes();
				DatagramPacket datagramPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), Constants.PORT);
				socket.send(datagramPacket);
			}
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

}
