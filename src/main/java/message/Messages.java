package message;

import java.net.DatagramPacket;

import constant.Constants;
import model.Configuration;
import model.Packet;

public class Messages {
	
	public static final String RECEIVE_TOKEN = "Received token";
	
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
	
	private static String yourMessage(Packet packet) {
		return "Your message \"" + packet.getMessage().get() + "\"";
	}
	
	public static String receive(Packet packet) {
		return "From " + packet.getSourceNickname().get() + ":\"" + packet.getMessage().get() + "\"";
	}
	
	public static String receiveBroadcast(Packet packet) {
		return "(Broadcast) " + receive(packet);
	}
	
	public static String notForMe(Packet packet) {
		return "Received the following packet, which is not destined to this computer:\n" +
				packet.getContent() + "\nResending it to the next one...";
	}
	
	public static String generateToken(Configuration config) {
		return "Generated token and sent it to " + config.getNextIP();
	}
	
	public static String addToQueue(Packet packet) {
		return "Added this packet " + packet.getContent() + " to queue";
	}
	
	public static String send(DatagramPacket datagramPacket, Configuration config) {
		return "Sending " + new String(datagramPacket.getData()).trim() +
				" to " + config.getNextIP() + ":" + config.getNextPort();
	}

}
