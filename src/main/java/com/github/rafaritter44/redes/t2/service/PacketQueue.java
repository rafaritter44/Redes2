package com.github.rafaritter44.redes.t2.service;

import java.util.LinkedList;
import java.util.List;

import com.github.rafaritter44.redes.t2.constant.Constants;
import com.github.rafaritter44.redes.t2.exception.FullQueueException;
import com.github.rafaritter44.redes.t2.model.Packet;

/*
 * Classe que representa a fila de pacotes
 */
public class PacketQueue {
	
	/*
	 * Holder do Singleton da fila de pacotes
	 */
	private static class QueueHolder {
		static final PacketQueue INSTANCE = new PacketQueue();
	}
	
	private List<Packet> queue;

	/*
	 * Construtor, que inicializa uma lista encadeada de pacotes
	 */
	private PacketQueue() {
		queue = new LinkedList<>();
	}
	
	/*
	 * Método estático que retorna o Singleton da fila de pacotes
	 */
	public static PacketQueue getInstance() {
		return QueueHolder.INSTANCE;
	}
	
	/*
	 * Método que informa se a fila está ou não vazia
	 */
	public boolean isEmpty() { return queue.isEmpty(); }
	
	/*
	 * Método que adiciona o pacote passador por parâmetro ao final da fila;
	 * Caso a fila tenha atingido o limite de pacotes (10), lança uma exceção
	 */
	public void add(Packet packet) throws FullQueueException {
		if(queue.size() == Constants.QUEUE_LIMIT)
			throw new FullQueueException("Failed to add \"" + packet.getContent() + "\"" +
					"\nCan't add any more packets. Maximum capacity: " + Constants.QUEUE_LIMIT);
		queue.add(packet);
	}
	
	/*
	 * Método que remove e retorna o primeiro pacote da fila
	 */
	public Packet poll() { return queue.remove(0); }
	
	/*
	 * Método que substitui (e retorna) o primeiro da fila pelo pacote passado por parâmetro
	 */
	public Packet replaceFirst(Packet packet) {
		return queue.set(0, packet);
	}

}
