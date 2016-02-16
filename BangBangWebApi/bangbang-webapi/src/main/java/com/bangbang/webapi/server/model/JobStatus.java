package com.bangbang.webapi.server.model;

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
    /*
    public static JobStatus fromInt(int x) {
        switch(x) {
        case 1:
            return PUBLISHED;
        case 2:
            return PICKED;
        case 3:
            return COMPLETED;
        case 4:
            return CLOSED;
        case 5:
            return EXPIRED;
        case 6:
        	  return ALL;
        }
        return null;
    }*/
}
