package org.diylc.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Very simple LRU file list implementation
 * 
 * @author Nikolaj Brinch
 */
public class LRU<T> {

	private int limit = 15;
	
	private LinkedList<T> lru = new LinkedList<>();

	public LRU(int limit) {
		this.limit = limit;
	}
	
	/**
	 * Adds an item to the LRU list, taking care of limiting the list, 
	 * and ordering so the last added file is first
	 * 
	 * @param file
	 */
	public void addItem(T item) {
		if (lru.contains(item)) {
			lru.remove(item);
		}
		
		lru.addFirst(item);
		if (lru.size() > limit) {
			lru.removeLast();
		}
	}

	public List<T> getItems() {
		return Collections.unmodifiableList(lru);
	}

}
