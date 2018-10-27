package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import constant.Constants;
import exception.InvalidPacketException;
import model.Configuration;
import model.Packet;
import service.PacketQueue;

public class Server implements Runnable {
	
	private static class ServerHolder {
		static final Server INSTANCE = new Server();
	}
	
	private Configuration config;
	private PacketQueue queue;
	
	private Server() {
		queue = PacketQueue.getInstance();
	}
	
	public static Server getInstance() {
		return ServerHolder.INSTANCE;
	}
	
	public Server configure(Configuration config) {
		this.config = config;
		return this;
	}
	
	public Configuration getConfig() { return config; }

	@Override
	public void run() {
		try(DatagramSocket socket = new DatagramSocket(Constants.PORT)) {
			byte[] data = new byte[Constants.PACKET_SIZE];
			while(true) {
				DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
				socket.receive(datagramPacket);
				Packet packet;
				try {
					packet = new Packet(new String(datagramPacket.getData()));
				} catch(InvalidPacketException exception) {
					System.out.println(exception.getMessage());
					continue;
				}
				if(InetAddress.getLocalHost().equals(datagramPacket.getAddress())) {
					queue.add(packet);
					continue;
				}
				if(packet.isToken()) {
					datagramPacket = new DatagramPacket(
							queue.isEmpty()? packet.getContent().getBytes(): queue.replaceFirst(packet).getContent().getBytes(),
									data.length, InetAddress.getByName(config.getNextIP()), config.getNextPort());
				} else if(packet.getSourceNickname().get().equals(config.getNickname())) {
					
				} else if(packet.getDestinationNickname().get().equals(config.getNickname())) {
					
				} else if(packet.getDestinationNickname().get().equals(Constants.BROADCAST_ID)) {
					
				}
				socket.send(datagramPacket);
			}
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}
	
}
