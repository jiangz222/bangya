package com.bangya.client.model;

public enum TabIdx {
	FIND(0),
	MY_TRENDS(1),
	NEW(2),
	ABOUT_ME(3);
    private final int value;
    private TabIdx(int value){
    	this.value = value;
    }
    public int getValue(){
    	return value;
    }
}
