package com.homecloud.entity.enums;


public enum PageSize {
	SIZE10(10), SIZE20(20), SIZE30(30);
	int size;

	private PageSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return this.size;
	}
}
