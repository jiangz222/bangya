package com.bangya.client.model;

public class SystemDTO {
	public String  latestServerVersion ;
	public String  latestAndroidClientVersion;
	public String  latestIOSClientVersion;
	public String  latestAndroidClientName;
	public String  latestIOSClientName;
	
	public String getLatestClientVersion(){
		return this.latestAndroidClientVersion;
	}
	public String getLatestClientName(){
		return this.latestAndroidClientName;
	}
}
