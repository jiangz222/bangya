package com.bangbang.webapi.server.model;

public class SystemDTO {
	public String  latestServerVersion ;
	public String  latestAndroidClientVersion;
	public String  latestIOSClientVersion;
	public String  latestAndroidClientName;
	public String  latestIOSClientName;
	public static SystemDTO converFromDAO(SystemDAO systemDAO){
		SystemDTO systemDTO = new SystemDTO();
		systemDTO.latestAndroidClientVersion = systemDAO.latestAndroidClientVersion;
		systemDTO.latestServerVersion = systemDAO.latestServerVersion;
		systemDTO.latestIOSClientVersion = systemDAO.latestIOSClientVersion;
		systemDTO.latestAndroidClientName = systemDAO.latestAndroidClientName;
		systemDTO.latestIOSClientName = systemDAO.latestIOSClientName;
		return systemDTO;

	}
}
