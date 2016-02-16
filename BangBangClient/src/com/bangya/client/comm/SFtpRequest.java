package com.bangya.client.comm;


import java.io.File;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.util.Log;

import com.bangya.client.BBUI.BaseUtil;
import com.bangya.client.Util.FileUtil;
import com.bangya.client.Util.MessageId;
import com.bangya.client.model.JobDTO;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SFtpRequest implements Runnable {
	// root path is /home/dev/bangya/
	 public final static String imagePathOnSvr = "image/head";
	 public final static String apkPathOnSvr = "client";
	 public final static int GET=1;
	 public final static int PUT=2;

		private String ftpUser = "ftpclient1";
		private String ftpPwd = "ftp.client1";
		private String dstPath;
		private String srcPath = null;
		private String fileNameOnServer;
		private List<JobDTO> joblist= null;
		private Context ct;
		private int getAvatarDirection;
		public static final int getAvatarForOwner = 1;
		public static final int getAvatarForPicker = 2;
		private String TAG="SFTPREQ";
		private int getOrPut;
	public SFtpRequest(){
		
	}
	public SFtpRequest(String srcPath,String dstPath,String fileNameOnServer,int getOrPut){
		this.srcPath = srcPath;
		this.dstPath = dstPath;
		this.fileNameOnServer = fileNameOnServer;
		this.getOrPut = getOrPut;
	}
	public SFtpRequest(Context ct,String dstPath,List<JobDTO> joblist,int getAvatarDirection){
		
		this.dstPath = dstPath;
		this.ct = ct;
		this.joblist = joblist;
		this.getAvatarDirection = getAvatarDirection;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(this.getOrPut == SFtpRequest.PUT){
			putFile();
		}else if(this.getOrPut == SFtpRequest.GET)
		{
			getFile();
		}
		else if(ct != null){
			getImageFromSvr();
		}
	}
	private void putFile(){
	       JSch jsch = new JSch();
	        Session session = null;
	        try {
	            session = jsch.getSession(ftpUser, WebAppClient.serverIP, 22);
	            session.setConfig("StrictHostKeyChecking", "no");
	            session.setPassword(ftpPwd);
	            session.connect();
	            Channel channel = session.openChannel("sftp");
	            channel.connect();
	            ChannelSftp sftpChannel = (ChannelSftp) channel;
	            sftpChannel.cd(dstPath);
	            sftpChannel.put(srcPath, fileNameOnServer);
	            sftpChannel.exit();
	            session.disconnect();
	        } catch (JSchException e) {
	            e.printStackTrace();  
	        } catch (SftpException e) {
	            e.printStackTrace();
	        }
	}
	private void getFile(){
		   JSch jsch = new JSch();
		   Session session = null;
			try {
	            session = jsch.getSession(ftpUser, WebAppClient.serverIP, 22);
	            session.setConfig("StrictHostKeyChecking", "no");
	            session.setPassword(ftpPwd);
	            session.connect();
	            Channel channel = session.openChannel("sftp");
	            channel.connect();
	            ChannelSftp sftpChannel = (ChannelSftp) channel;
	            sftpChannel.cd(srcPath);
		         sftpChannel.get(this.fileNameOnServer,dstPath);
		         sftpChannel.exit();
	            }catch (SftpException e) {
	                e.printStackTrace();
	            }catch (JSchException e) {
	                e.printStackTrace();  
	        }
			if(null != session){
		         session.disconnect();
			}
			if(dstPath.equals(FileUtil.APK_PATH+BaseUtil.getSystemInfo().getLatestClientName())){
				InnerCommAdapter.sendEmptyMessage(MessageId.APK_DOWN_LOAD_DONE);
			}
	}
	private void getImageFromSvr()
	{
	   JSch jsch = new JSch();
	   Session session = null;
	   BaseUtil bt = new BaseUtil();
		boolean bisUpdated = false;
		try {
            session = jsch.getSession(ftpUser, WebAppClient.serverIP, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(ftpPwd);
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            sftpChannel.cd(dstPath);
			for(JobDTO jt :  joblist)
			{
				if(this.getAvatarDirection == getAvatarForOwner){
				if(jt.getOwnerheadPhoto() !=null && jt.getOwnerheadPhoto().length()>0)
				{// if no photo at server, do not need to get image from svr
					Date dt = bt.getHeadPhotoModifyTimeByUid(ct,jt.getOwnerUid());
					File avatar = new File(FileUtil.HEAD_PATH+File.separator+jt.getOwnerUid()+".jpg");
					if((jt.getOwnerHeadPhotoMTime()!=null) // if owner head modify time is null, no need to get image
						&&(	dt == null   // if local photo modify time is null, get image
						|| jt.getOwnerHeadPhotoMTime().after(dt)// or if local is not null ,and image at svr is newer than local, get image
						|| (!avatar.exists()))) // or if local Avatar is not exist, download it now
						{
							try {
								Log.i(TAG, "download owner avatar for uid:"+jt.getOwnerUid());
					          sftpChannel.get(jt.getOwnerUid()+".jpg",FileUtil.HEAD_PATH+File.separator+jt.getOwnerUid()+".jpg");

								bt.storeHeadPhotoModifyTime(ct, jt.getOwnerHeadPhotoMTime(), jt.getOwnerUid());
								bisUpdated = true;
							} catch (SftpException e) {
					            e.printStackTrace();
					        }
						}
					}
				}
				else if(this.getAvatarDirection == getAvatarForPicker){
					if(jt.getPickerheadPhoto() !=null && jt.getPickerheadPhoto().length()>0)
					{// if no photo at server, do not need to get image from svr
						Date dt = bt.getHeadPhotoModifyTimeByUid(ct,jt.getPickerUid());
						File avatar = new File(FileUtil.HEAD_PATH+File.separator+jt.getPickerUid()+".jpg");
						if((jt.getPikcerHeadPhotoMTime()!=null) // if head modify time is null, no need to get image
						&&(	dt == null   // if local photo modify time is null, get image
						|| jt.getPikcerHeadPhotoMTime().after(dt)// or if local is not null ,and image at svr is newer than local, get image
						||(!avatar.exists())))  // or if local Avatar is not exist, download it now
							{
							try {
								Log.i(TAG, "download picker avatar for uid:"+jt.getPickerUid());
						       sftpChannel.get(jt.getPickerUid()+".jpg",FileUtil.HEAD_PATH+File.separator+jt.getPickerUid()+".jpg");
								bt.storeHeadPhotoModifyTime(ct, jt.getPikcerHeadPhotoMTime(), jt.getPickerUid());
								bisUpdated = true;
							}  catch (SftpException e) {
					            e.printStackTrace();
					        }
						}
					}
				}
			}
			// check if need downloading of my avatar
			File myAvatar = new File(FileUtil.HEAD_PATH+File.separator+BaseUtil.getSelfUserInfo().getUserId()+".jpg");
			Date dt = bt.getHeadPhotoModifyTimeByUid(ct,BaseUtil.getSelfUserInfo().getUserId());
			if((BaseUtil.getSelfUserInfo().getHeadPhotoMTime()!=null) // if owner head modify time is null, no need to get image
					&&(	dt == null   // if local photo modify time is null, get image
					|| BaseUtil.getSelfUserInfo().getHeadPhotoMTime().after(dt)// or if local is not null ,and image at svr is newer than local, get image
					|| (!myAvatar.exists()))) // or if local Avatar is not exist, download it now
					{
				try {
					Log.i(TAG, "download my avatar");
			       sftpChannel.get(BaseUtil.getSelfUserInfo().getUserId()+".jpg",FileUtil.HEAD_PATH+File.separator+BaseUtil.getSelfUserInfo().getUserId()+".jpg");
					bt.storeHeadPhotoModifyTime(ct, BaseUtil.getSelfUserInfo().getHeadPhotoMTime(), BaseUtil.getSelfUserInfo().getUserId());
				} catch (SftpException e) {
		            e.printStackTrace();
		        }

			}
			
			if(true == bisUpdated)
			{
	            // if ftp new photo to local, clear cache so MainlistAdapter use new photo
				ImageLoader.getInstance().clearDiskCache();
	          ImageLoader.getInstance().clearMemoryCache();
			}
			
            sftpChannel.exit();
            session.disconnect();
		}  catch (SftpException e) {
            e.printStackTrace();
        }catch (JSchException e) {
            e.printStackTrace();  
        } 

	}
}
