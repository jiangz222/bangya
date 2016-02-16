package com.bangbang.webapi.server.model;

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
    public RewardType fromInt(int x) {
        switch(x) {
        case 0:
            return RMB;
        case 1:
            return OBJECT;
        case 2:
            return ZAN;
        case 3:
            return OTHERS;
        }
        return null;
    }
    public static RewardType fromString(String value)
    {
    	switch(Integer.valueOf(value))
    	{
    		case 1:
    			return RewardType.RMB;
    		case 2:
    			return RewardType.OBJECT;
    		case 3:
    			return RewardType.ZAN;
    		case 4:
    			return RewardType.OTHERS;
    		case 5:
    			return RewardType.NOREWARD;
    		default:
    			return RewardType.NOREWARD;

    	}
    }
}
