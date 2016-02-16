package com.bangya.client.model;

/**
 * Created by wisp on 3/29/14.
 */
public enum JobStatus {
    PUBLISHED(1),
    PICKED(2),
    COMPLETED(3),
    CLOSED(4),
    EXPIRED(5),
    ALL(6);
    private final int value;
    private JobStatus(int value){
    	this.value = value;
    }
    public int getValue(){
    	return value;
    }
}
