package message;

import constant.Constants;
import model.Packet;

public class Messages {
	
	public static String broadcastSource(Packet packet) {
		return yourMessage(packet) + " was broadcasted";
	}

	public static String ok(Packet packet) {
		return "(" + Constants.OK + ") " + yourMessage(packet) +
				" was correctly sent to " + packet.getDestinationNickname();
	}
	
	public static String notCopied(Packet packet) {
		return "(" + Constants.NOT_COPIED + ") " + yourMessage(packet) + " could not be sent because "
				+ packet.getDestinationNickname() + " is not on the network or is off";
	}
	
	public static String error(Packet packet, boolean secondError) {
		return "(" + Constants.ERROR + ") " + yourMessage(packet) + " had an error " + (secondError?
				" twice, so it is being removed from the queue": " and could not be sent to " + packet.getDestinationNickname()
				+ ". It will be resent the next time the token arrives");
	}
	
	private static String yourMessage(Packet packet) {
		return "Your message \"" + packet.getMessage() + "\"";
	}
	
	public static String receive(Packet packet) {
		return "From " + packet.getSourceNickname() + ":\"" + packet.getMessage() + "\"";
	}
	
	public static String receiveBroadcast(Packet packet) {
		return "(Broadcast) " + receive(packet);
	}
	
	public static String notForMe(Packet packet) {
		return "Received the following packet, which is not destined to this computer:\n" +
				packet.getContent() + "\nResending it to the next one...";
	}

}
