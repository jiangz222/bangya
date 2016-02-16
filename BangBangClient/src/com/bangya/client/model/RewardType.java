package com.bangya.client.model;

/**
 * Created by wisp on 3/29/14.
 */
public enum RewardType {
    RMB(1),
    OBJECT(2),
    ZAN(3),
    OTHERS(4),
    NOREWARD(5);
    private final int value;
    private RewardType(int value){
    	this.value = value;
    }
    public int getValue(){
    	return value;
    }
}
