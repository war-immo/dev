package de.zorgk.drums;

import java.util.*;

public class CommunicationStream {

	protected LinkedList<Integer> stream_info;
	protected LinkedList<Object> stream_object;

	public CommunicationStream() {
		stream_info = new LinkedList<Integer>();
		stream_object = new LinkedList<Object>();
	}

	public synchronized boolean empty() {
		return stream_info.isEmpty();
	}

	public synchronized int info() {
		return stream_info.peekFirst();
	}

	public synchronized Object object() {
		return stream_object.peekFirst();
	}

	public synchronized void pop() {
		stream_info.pop();
		stream_object.pop();
	}

	public synchronized void push(int info, Object object) {
		stream_info.add(new Integer(info));
		stream_object.add(object);
	}

}
