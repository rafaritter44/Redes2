package server;

import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import client.Client;
import constant.Constants;
import exception.FullQueueException;
import exception.InvalidPacketException;
import message.Messages;
import model.Configuration;
import model.Packet;
import service.PacketQueue;

public class Server implements Runnable {
	
	private static class ServerHolder {
		static final Server INSTANCE = new Server();
	}
	
	private Configuration config;
	private PacketQueue queue;
	private Random random;
	private int fileCount;
	
	private Server() {
		queue = PacketQueue.getInstance();
		random = new Random();
		fileCount = 1;
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
			if(config.isTokenGenerator())
				socket.send(datagramTokenPacket());
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
					try {
						queue.add(packet);
					} catch(FullQueueException exception) {
						System.out.println(exception.getMessage());
					}
					continue;
				}
				if(packet.isToken()) {
					datagramPacket = createDatagramPacket(queue.isEmpty()? packet: queue.replaceFirst(packet));
				} else if(packet.getSourceNickname().get().equals(config.getNickname())) {
					switch(packet.getErrorControl().get()) {
					case Constants.OK:
						System.out.println(Messages.ok(packet));
						datagramPacket = createDatagramPacket(queue.poll());
						break;
					case Constants.NOT_COPIED:
						if(packet.getDestinationNickname().get().equals(Constants.BROADCAST_ID))
							System.out.println(Messages.broadcastSource(packet));
						else
							System.out.println(Messages.notCopied(packet));
						datagramPacket = createDatagramPacket(queue.poll());
						break;
					case Constants.ERROR:
						Client client = Client.getInstance();
						boolean secondError = client.isMessageWithError(packet.getContent());
						System.out.println(Messages.error(packet, secondError));
						if(secondError)
							datagramPacket = createDatagramPacket(queue.poll());
						else {
							datagramPacket = createDatagramPacket(queue.replaceFirst(packet));
							client.updateMessageWithError(packet.getContent());
						}
					}
				} else if(packet.getDestinationNickname().get().equals(config.getNickname())) {
					System.out.println(Messages.receive(packet));
					if(error())
						datagramPacket = createDatagramPacket(packet.introduceError());
					else
						datagramPacket = createDatagramPacket(packet.readMessage());
					if(packet.getDataType().get().equals(Constants.FILE_ID))
						saveFile(packet);
				} else if(packet.getDestinationNickname().get().equals(Constants.BROADCAST_ID)) {
					System.out.println(Messages.receiveBroadcast(packet));
					if(packet.getDataType().get().equals(Constants.FILE_ID))
						saveFile(packet);
				} else {
					System.out.println(Messages.notForMe(packet));
				}
				socket.send(datagramPacket);
			}
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}
	
	private DatagramPacket createDatagramPacket(Packet packet) throws UnknownHostException {
		byte[] data = packet.getContent().getBytes();
		return new DatagramPacket(data, data.length, InetAddress.getByName(config.getNextIP()), config.getNextPort());
	}
	
	private boolean error() {
		return random.nextInt(100) < Constants.ERROR_PROBABILITY;
	}
	
	private void saveFile(Packet packet) {
		try(PrintWriter writer = new PrintWriter(Constants.DEFAULT_FILE_NAME + fileCount++ + Constants.DEFAULT_FILE_EXTENSION)) {
			writer.println(packet.getContent());
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}
	
	private DatagramPacket datagramTokenPacket() throws Exception {
		return createDatagramPacket(Packet.generateToken());
	}
	
}
