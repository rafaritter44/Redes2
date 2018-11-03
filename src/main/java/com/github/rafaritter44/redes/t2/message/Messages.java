package com.github.rafaritter44.redes.t2.message;

import java.net.DatagramPacket;
import java.net.UnknownHostException;

import com.github.rafaritter44.redes.t2.constant.Constants;
import com.github.rafaritter44.redes.t2.model.Configuration;
import com.github.rafaritter44.redes.t2.model.Packet;
import com.github.rafaritter44.redes.t2.server.Server;

public class Messages {
	
	public static String broadcastSource(Packet packet) {
		return yourMessage(packet) + " was broadcasted";
	}

	public static String ok(Packet packet) {
		return "(" + Constants.OK + ") " + yourMessage(packet) +
				" was correctly sent to " + packet.getDestinationNickname().get();
	}
	
	public static String notCopied(Packet packet) {
		return "(" + Constants.NOT_COPIED + ") " + yourMessage(packet) + " could not be sent because "
				+ packet.getDestinationNickname().get() + " is not on the network or is off";
	}
	
	public static String error(Packet packet, boolean secondError) {
		return "(" + Constants.ERROR + ") " + yourMessage(packet) + " had an error " + (secondError?
				" twice, so it is being removed from the queue": " and could not be sent to " +
				packet.getDestinationNickname().get() + ". It will be resent the next time the token arrives");
	}
	
	public static String introduceError(Packet packet) {
		return "Introduced an error into packet \"" + packet.getMessage().get() + "\" from " + packet.getSourceNickname().get();
	}
	
	private static String yourMessage(Packet packet) {
		return "Your message \"" + packet.getMessage().get() + "\"";
	}
	
	public static String receive(Packet packet) {
		return "From " + packet.getSourceNickname().get() + ": \"" + packet.getMessage().get() + "\"";
	}
	
	public static String receiveBroadcast(Packet packet) {
		return "(Broadcast) " + receive(packet);
	}
	
	public static String notForMe(Packet packet) {
		return "Received the following packet, which is not destined to this computer:\n" +
				packet.getContent() + "\nResending it to the next one...";
	}
	
	public static String generateToken(Configuration config) {
		return "Generated token and sent it to " + config.getNextIP() + ":" + config.getNextPort();
	}
	
	public static String addToQueue(Packet packet) {
		return "Added this packet \"" + packet.getContent() + "\" to queue";
	}
	
	public static String send(DatagramPacket datagramPacket, Configuration config) {
		String data = new String(datagramPacket.getData());
		return "Sent " + (Constants.TOKEN.equals(data)? "token": "\"" + data + "\"") +
				" to " + config.getNextIP() + ":" + config.getNextPort();
	}
	
	public static String sleep(long sleepDuration) {
		return "Sleeping for " + sleepDuration + " seconds";
	}
	
	public static String receiveData(DatagramPacket datagramPacket) throws UnknownHostException {
		String data = new String(datagramPacket.getData());
		return "Received " + (Constants.TOKEN.equals(data)? "token": "\"" + data + "\"") +
				" from " + (Server.getInstance().isFromClient(datagramPacket)? "client":
						datagramPacket.getAddress().getHostAddress() + ":" + datagramPacket.getPort());
	}
	
	public static String saveFile(String file, Packet packet) {
		return "Saved \"" + packet.getContent() + "\" into " + file;
	}

}
