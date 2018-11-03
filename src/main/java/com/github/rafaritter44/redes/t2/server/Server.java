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

/*
 * Classe que representa o servidor
 */
public class Server implements Runnable {
	
	/*
	 * Holder do Singleton de Server
	 */
	private static class ServerHolder {
		static final Server INSTANCE = new Server();
	}
	
	private Configuration config;
	private PacketQueue queue;
	private Random random;
	private int fileCount;
	
	/*
	 * Construtor, que inicializa a instância do Singleton da lista de pacotes,
	 * inicializa o gerador de números aleatórios (usado para a probabilidade
	 * de introduzir erros nos pacotes recebidos), e inicializa o contador de
	 * arquivos (para salvar os arquivos recebidos com diferentes nomes) com 1
	 */
	private Server() {
		queue = PacketQueue.getInstance();
		random = new Random();
		fileCount = 1;
	}
	
	/*
	 * Método estático que retorna o Singleton de Server
	 */
	public static Server getInstance() {
		return ServerHolder.INSTANCE;
	}
	
	/*
	 * Método que configura e retorna o servidor com a configuração especificada
	 */
	public Server configure(Configuration config) {
		this.config = config;
		return this;
	}
	
	/*
	 * Método que retorna a configuração do servidor
	 */
	public Configuration getConfig() { return config; }

	/*
	 * Trecho assíncrono do servidor, o qual cria um Servidor UDP e continuamente
	 * recebe, trata e envia pacotes dentro da rede em anel, imprimindo tudo que ocorre;
	 * Inicialmente, caso seja o gerador do token (true no arquivo de configuração),
	 * aguarda o tempo de espera (também informado na configuração) e envia o token
	 * para a próxima máquina do anel;
	 * Em seguida, fica continuamente recebendo pacotes (do cliente ou da máquina anterior
	 * na rede em anel), validando os pacotes recebidos, adicionando-os na fila caso sejam
	 * do cliente, e caso sejam da máquina anterior entra em tempo de espera e, depois disso,
	 * verifica se o pacote é um token, ou se foi enviado por este mesmo servidor, ou se foi
	 * destinado a esta máquina, ou se é o pacote foi enviado em broadcast;
	 * Caso seja o token, verifica se há pacotes na fila; Se tiver, envia o primeiro e coloca
	 * o token em primeiro da fila, senão envia o token para a próxima máquina da rede;
	 * Caso o pacote tenha sido enviado por este servidor, verifica o controle de erro:
	 * se estiver marcado como "OK" ou "naocopiado", informa o que ocorreu e envia o token
	 * para a próxima máquina; se estiver marcado como "erro" pela primeira vez, coloca o
	 * pacote de volta como primeiro da fila e envia o token para o próximo, e se tiver
	 * ocorrido um erro pela segunda vez não coloca o pacote na fila de novo, apenas envia o
	 * token;
	 * Caso o pacote tenha sido destinado a esta máquina, imprime o conteúdo e o apelido da
	 * origem na tela, salvando se for arquivo, e marcando o controle de erro como "OK", ou
	 * como "erro" em 25% das vezes, e retransmitindo o pacote para a próxima máquina
	 * Caso tenha sido enviado em broadcast, exibe na tela o conteúdo e o apelido da origem,
	 * salva se for um arquivo, e retransmite o pacote para a próxima máquina da rede
	 * Se o pacote não for destinado a esta máquina, simplesmente o retransmite para a próxima
	 * máquina da rede
	 */
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
	
	/*
	 * Método que cria um "DatagramPacket" com o conteúdo do pacote passado por parâmetro,
	 * e para o endereço especificado no arquivo de configuração
	 */
	private DatagramPacket createDatagramPacket(Packet packet) throws UnknownHostException {
		byte[] data = packet.getContent().getBytes();
		return new DatagramPacket(data, data.length, InetAddress.getByName(config.getNextIP()), config.getNextPort());
	}
	
	/*
	 * Método que decide se será introduzido um erro no pacote, retornando true 25% das vezes
	 */
	private boolean error() {
		return random.nextInt(100) < Constants.ERROR_PROBABILITY;
	}
	
	/*
	 * Método que salva o conteúdo do pacote informado em um novo arquivo
	 */
	private void saveFile(Packet packet) {
		final String file = Constants.DEFAULT_FILE_NAME + fileCount++ + Constants.DEFAULT_FILE_EXTENSION;
		try(PrintWriter writer = new PrintWriter(file)) {
			writer.println(packet.getContent());
			System.out.println(Messages.saveFile(file, packet));
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}
	
	/*
	 * Método que cria um "DatagramPacket" do token
	 */
	private DatagramPacket datagramTokenPacket() throws UnknownHostException {
		return createDatagramPacket(Packet.generateToken());
	}
	
	/*
	 * Método que deixa o servidor em tempo de espera pelo tempo informado na configuração,
	 * contando segundo a segundo até o final desse tempo
	 */
	private void sleep() throws InterruptedException {
		System.out.println(Messages.sleep(config.getSleepDuration()));
		for(long i=config.getSleepDuration(); i>0L; i--) {
			System.out.print(i + "...");
			TimeUnit.SECONDS.sleep(1L);
		}
		System.out.println(0);
	}
	
	/*
	 * Método que verifica se o "DatagramPacket" passado por parâmetro foi enviado pelo cliente ou não
	 */
	public boolean isFromClient(DatagramPacket datagramPacket) throws UnknownHostException {
		return InetAddress.getLocalHost().equals(datagramPacket.getAddress())
				&& Constants.CLIENT_PORT == datagramPacket.getPort();
	}
	
	/*
	 * Método que remove a sujeira restante no final dos dados contidos no "DatagramPacket" passado por parâmetro
	 */
	private void trimDatagramPacket(DatagramPacket datagramPacket) {
		datagramPacket.setData(new String(datagramPacket.getData(), 0, datagramPacket.getLength()).trim().getBytes());
	}
	
}
