package org.diylc.components;

import lombok.Getter;

public enum IcPointCount {
	_3("3", 3), _5("5", 5);

	private String title;
	
	@Getter
	private int value;

	private IcPointCount(String title, int value) {
		this.title = title;
		this.value = value;
	}

	@Override
	public String toString() {
		return title;
	}
	
}
