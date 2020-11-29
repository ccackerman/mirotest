package com.miro.widget.repo;

import java.util.concurrent.atomic.AtomicLong;

public class WidgetIdGenerator {
	
	private final AtomicLong idGen = new AtomicLong(1);
	
    /**
     * Generates a unique id for a new Widget
     */
	long genId() {
		return idGen.getAndIncrement();
	}
	
	/**
	 * Tests if a given value would be a valid Widget id.
	 */
	static boolean isValidId(Long id) {
		if ((null == id) || (id < 1l))
			return false;
		return true;					
	}
}
