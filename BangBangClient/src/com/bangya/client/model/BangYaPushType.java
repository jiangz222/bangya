package com.bangya.client.model;

public enum BangYaPushType {
	CHAT_MSG(1),
	JOB_STATUS(2),
	VERSION_UPDATE(3),
	OTHER(3);
    private final int value;
    private BangYaPushType(int value){
    	this.value = value;
    }
    public int getValue(){
    	return value;
    }
}
