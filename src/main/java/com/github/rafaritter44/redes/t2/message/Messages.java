package com.github.rafaritter44.redes.t2.message;

import java.net.DatagramPacket;
import java.net.UnknownHostException;

import com.github.rafaritter44.redes.t2.constant.Constants;
import com.github.rafaritter44.redes.t2.model.Configuration;
import com.github.rafaritter44.redes.t2.model.Packet;
import com.github.rafaritter44.redes.t2.server.Server;

/*
 * Classe que retorna mensagens padrão com base nas entradas fornecidas
 */
public class Messages {
	
	/*
	 * Informa que o pacote foi enviado em broadcast
	 */
	public static String broadcastSource(Packet packet) {
		return yourMessage(packet) + " was broadcasted";
	}

	/*
	 * Informa que o pacote foi devidamente recebido pelo destino
	 */
	public static String ok(Packet packet) {
		return "(" + Constants.OK + ") " + yourMessage(packet) +
				" was correctly sent to " + packet.getDestinationNickname().get();
	}
	
	/*
	 * Informa que o destino do pacote não o recebeu
	 */
	public static String notCopied(Packet packet) {
		return "(" + Constants.NOT_COPIED + ") " + yourMessage(packet) + " could not be sent because "
				+ packet.getDestinationNickname().get() + " is not on the network or is off";
	}
	
	/*
	 * Informa que houve um erro na transmissão do pacote e, caso tenha sido a segunda vez,
	 * informa que o pacote vai ser retirado da fila; senão informa que ele será reenviado
	 */
	public static String error(Packet packet, boolean secondError) {
		return "(" + Constants.ERROR + ") " + yourMessage(packet) + " had an error " + (secondError?
				" twice, so it is being removed from the queue": " and could not be sent to " +
				packet.getDestinationNickname().get() + ". It will be resent the next time the token arrives");
	}
	
	/*
	 * Informa que foi introduzido um erro no pacote informado
	 */
	public static String introduceError(Packet packet) {
		return "Introduced an error into packet \"" + packet.getMessage().get() + "\" from " + packet.getSourceNickname().get();
	}
	
	/*
	 * Retorna texto padrão contendo a mensagem do pacote
	 */
	private static String yourMessage(Packet packet) {
		return "Your message \"" + packet.getMessage().get() + "\"";
	}
	
	/*
	 * Exibe a mensagem recebida e o apelido de quem a enviou
	 */
	public static String receive(Packet packet) {
		return "From " + packet.getSourceNickname().get() + ": \"" + packet.getMessage().get() + "\"";
	}
	
	/*
	 * Exibe a mensagem recebida em broadcast, bem como quem a enviou
	 */
	public static String receiveBroadcast(Packet packet) {
		return "(Broadcast) " + receive(packet);
	}
	
	/*
	 * Informa que o pacote enviado não é destinado a esta máquina, e que será retransmitido
	 */
	public static String notForMe(Packet packet) {
		return "Received the following packet, which is not destined to this computer:\n" +
				packet.getContent() + "\nResending it to the next one...";
	}
	
	/*
	 * Informa que o token foi gerado e enviado ao endereço especificado nas configurações
	 */
	public static String generateToken(Configuration config) {
		return "Generated token and sent it to " + config.getNextIP() + ":" + config.getNextPort();
	}
	
	/*
	 * Informa que o pacote especificado foi adicionado à fila
	 */
	public static String addToQueue(Packet packet) {
		return "Added this packet \"" + packet.getContent() + "\" to queue";
	}
	
	/*
	 * Informa que o pacote passado por parâmetro foi enviado à próxima máquina do anel
	 * (cujo endereço é informado nas configurações)
	 */
	public static String send(DatagramPacket datagramPacket, Configuration config) {
		String data = new String(datagramPacket.getData());
		return "Sent " + (Constants.TOKEN.equals(data)? "token": "\"" + data + "\"") +
				" to " + config.getNextIP() + ":" + config.getNextPort();
	}
	
	/*
	 * Informa que a thread em questão entrará em estado de espera por X segundos
	 * (X passado por parâmetro)
	 */
	public static String sleep(long sleepDuration) {
		return "Sleeping for " + sleepDuration + " seconds";
	}
	
	/*
	 * Informa que o pacote passado por parâmetro foi recebido pelo endereço indicado
	 */
	public static String receiveData(DatagramPacket datagramPacket) throws UnknownHostException {
		String data = new String(datagramPacket.getData());
		return "Received " + (Constants.TOKEN.equals(data)? "token": "\"" + data + "\"") +
				" from " + (Server.getInstance().isFromClient(datagramPacket)? "client":
						datagramPacket.getAddress().getHostAddress() + ":" + datagramPacket.getPort());
	}
	
	/*
	 * Informa que o pacote passado por parâmetro foi salvo no arquivo informado
	 */
	public static String saveFile(String file, Packet packet) {
		return "Saved \"" + packet.getContent() + "\" into " + file;
	}

}
