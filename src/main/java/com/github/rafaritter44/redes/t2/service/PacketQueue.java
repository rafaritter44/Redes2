package com.github.rafaritter44.redes.t2.service;

import java.util.LinkedList;
import java.util.List;

import com.github.rafaritter44.redes.t2.constant.Constants;
import com.github.rafaritter44.redes.t2.exception.FullQueueException;
import com.github.rafaritter44.redes.t2.model.Packet;

public class PacketQueue {
	
	private static class QueueHolder {
		static final PacketQueue INSTANCE = new PacketQueue();
	}
	
	private List<Packet> queue;

	private PacketQueue() {
		queue = new LinkedList<>();
	}
	
	public static PacketQueue getInstance() {
		return QueueHolder.INSTANCE;
	}
	
	public boolean isEmpty() { return queue.isEmpty(); }
	
	public void add(Packet packet) throws FullQueueException {
		if(queue.size() == Constants.QUEUE_LIMIT)
			throw new FullQueueException("Failed to add \"" + packet.getContent() + "\"" +
					"\nCan't add any more packets. Maximum capacity: " + Constants.QUEUE_LIMIT);
		queue.add(packet);
	}
	
	public Packet poll() { return queue.remove(0); }
	
	public Packet replaceFirst(Packet packet) {
		return queue.set(0, packet);
	}

}
