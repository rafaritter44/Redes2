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

/*
 * Classe que representa o cliente
 */
public class Client implements Runnable {

	/*
	 * Holder do Singleton de Client
	 */
	private static class ClientHolder {
		static final Client INSTANCE = new Client();
	}
	
	private Map<String, Boolean> messagesWithError;
	private BufferedReader reader;

	/*
	 * Construtor, que inicializa um dicionário que mapeia cada mensagem enviada pelo
	 * cliente para um booleano que informa se o pacote foi recebido com erro ou não;
	 * Também inicializa um BufferedReader para ler as mensagens escritas pelo teclado
	 */
	private Client() {
		messagesWithError = Collections.synchronizedMap(new HashMap<>());
		reader = new BufferedReader(new InputStreamReader(System.in));
	}

	/*
	 * Método estático que retorna o Singleton de Client
	 */
	public static Client getInstance() {
		return ClientHolder.INSTANCE;
	}
	
	/*
	 * Método que atualiza a mensagem especificada, informando que ela foi recebida com erro
	 */
	public void updateMessageWithError(String input) {
		messagesWithError.put(input, true);
	}
	
	/*
	 * Método que informa se a mensagem especificada foi recebida com erro ou não
	 */
	public boolean isMessageWithError(String input) {
		return messagesWithError.get(input);
	}

	/*
	 * Trecho assíncrono do cliente, que cria um Cliente UDP e recebe continuamente as entradas do
	 * teclado, validando-as, adicionando-as ao dicionário de controle de erro, e enviando-as ao servidor
	 */
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
