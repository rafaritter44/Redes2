package com.github.rafaritter44.redes.t2.server;

import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.github.rafaritter44.redes.t2.client.Client;
import com.github.rafaritter44.redes.t2.constant.Constants;
import com.github.rafaritter44.redes.t2.exception.FullQueueException;
import com.github.rafaritter44.redes.t2.exception.InvalidPacketException;
import com.github.rafaritter44.redes.t2.message.Messages;
import com.github.rafaritter44.redes.t2.model.Configuration;
import com.github.rafaritter44.redes.t2.model.Packet;
import com.github.rafaritter44.redes.t2.service.PacketQueue;

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
		try(DatagramSocket socket = new DatagramSocket(Constants.SERVER_PORT)) {
			if(config.isTokenGenerator()) {
				sleep();
				System.out.println(Messages.generateToken(config));
				socket.send(datagramTokenPacket());
			}
			byte[] data = new byte[Constants.PACKET_SIZE];
			while(true) {
				DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
				socket.receive(datagramPacket);
				trimDatagramPacket(datagramPacket);
				System.out.println(Messages.receiveData(datagramPacket));
				Packet packet;
				try {
					packet = new Packet(new String(datagramPacket.getData()));
				} catch(InvalidPacketException exception) {
					System.out.println(exception.getMessage());
					continue;
				}
				if(isFromClient(datagramPacket)) {
					try {
						queue.add(packet);
						System.out.println(Messages.addToQueue(packet));
					} catch(FullQueueException exception) {
						System.out.println(exception.getMessage());
					}
					continue;
				}
				sleep();
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
						boolean secondError = client.isMessageWithError(packet.getClientInput());
						System.out.println(Messages.error(packet, secondError));
						if(secondError)
							datagramPacket = createDatagramPacket(queue.poll());
						else {
							datagramPacket = createDatagramPacket(queue.replaceFirst(packet.renew()));
							client.updateMessageWithError(packet.getClientInput());
						}
					}
				} else if(packet.getDestinationNickname().get().equals(config.getNickname())) {
					System.out.println(Messages.receive(packet));
					if(error()) {
						System.out.println(Messages.introduceError(packet));
						datagramPacket = createDatagramPacket(packet.introduceError());
					}
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
				System.out.println(Messages.send(datagramPacket, config));
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
		final String file = Constants.DEFAULT_FILE_NAME + fileCount++ + Constants.DEFAULT_FILE_EXTENSION;
		try(PrintWriter writer = new PrintWriter(file)) {
			writer.println(packet.getContent());
			System.out.println(Messages.saveFile(file, packet));
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}
	
	private DatagramPacket datagramTokenPacket() throws Exception {
		return createDatagramPacket(Packet.generateToken());
	}
	
	private void sleep() throws InterruptedException {
		System.out.println(Messages.sleep(config.getSleepDuration()));
		for(long i=config.getSleepDuration(); i>0L; i--) {
			System.out.print(i + "...");
			TimeUnit.SECONDS.sleep(1L);
		}
		System.out.println(0);
	}
	
	public boolean isFromClient(DatagramPacket datagramPacket) throws UnknownHostException {
		return InetAddress.getLocalHost().equals(datagramPacket.getAddress())
				&& Constants.CLIENT_PORT == datagramPacket.getPort();
	}
	
	private void trimDatagramPacket(DatagramPacket datagramPacket) {
		datagramPacket.setData(new String(datagramPacket.getData(), 0, datagramPacket.getLength()).trim().getBytes());
	}
	
}
