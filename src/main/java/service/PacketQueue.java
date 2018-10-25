package service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import constant.Constants;
import exception.FullQueueException;
import model.Packet;

public class PacketQueue {
	
	private List<Packet> queue;

	public PacketQueue() {
		queue = Collections.synchronizedList(new LinkedList<>());
	}
	
	public boolean isEmpty() { return queue.isEmpty(); }
	
	public synchronized void add(Packet packet) throws FullQueueException {
		if(queue.size() == Constants.QUEUE_LIMIT)
			throw new FullQueueException("Can't add any more packets. Maximum capacity: " + Constants.QUEUE_LIMIT);
		queue.add(packet);
	}
	
	public Packet replaceFirst(Packet packet) {
		return queue.set(0, packet);
	}

}
