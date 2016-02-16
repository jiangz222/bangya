package com.bangya.client.comm;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.bangya.client.BBUI.BaseUtil;
import com.bangya.client.Util.FileUtil;
import com.bangya.client.model.JobDTO;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * ftp request to server by ftp4j
 * @author joe
 *
 */

public class FtpRequest_replacedbySFTP implements Runnable {
	// followed 3 lines should never used
//	private String ftpUser = "root"; 
//	private String ftpPwd = "123456";
//	public final static String imagePathOnSvr = "/home/dev/bangya/image/head";
	
// NOTICE: the home directory of ftpclient1 is /home/dev/bangya
 public final static String imagePathOnSvr = "image/head";
	private String ftpUser = "ftpclient1";
	private String ftpPwd = "ftp.client1";
	private int ftpPort = 21;
	private String dstPath;
	private File transferFile = null;
	private List<JobDTO> joblist= null;
	private Context ct;
	private int getAvatarDirection;
	public static final int getAvatarForOwner = 1;
	public static final int getAvatarForPicker = 2;
	private String TAG="FTPREQ";
	public FtpRequest_replacedbySFTP(String filePath,String dstPath){
		this.transferFile = new File(filePath);
		this.dstPath = dstPath;
	}

	public FtpRequest_replacedbySFTP(Context ct,String dstPath,List<JobDTO> joblist,int getAvatarDirection){
		
		this.dstPath = dstPath;
		this.ct = ct;
		this.joblist = joblist;
		this.getAvatarDirection = getAvatarDirection;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(this.transferFile != null){
			FtpFileToSvr();
		}
		else if(ct != null){
			getImageFromSvr();
		}
	}
	public void FtpFileToSvr()
	{
		FTPClient client = new FTPClient();
		try {
			client.connect(WebAppClient.serverIP,ftpPort);
			client.login(ftpUser, ftpPwd);
			client.setPassive(true);
			client.setType(FTPClient.TYPE_BINARY);
			client.changeDirectory(dstPath);
			client.upload(transferFile);
			client.disconnect(true);
			System.out.println("ftp done");

		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPIllegalReplyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPDataTransferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPAbortedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void getImageFromSvr()
	{
		FTPClient client = new FTPClient();
		BaseUtil bt = new BaseUtil();
		boolean bisUpdated = false;
		try {
			client.connect(WebAppClient.serverIP,ftpPort);
			client.login(ftpUser, ftpPwd);
			client.setPassive(true);
			client.setType(FTPClient.TYPE_BINARY);
			client.changeDirectory(dstPath);
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
								client.download(jt.getOwnerUid()+".jpg", avatar);
								bt.storeHeadPhotoModifyTime(ct, jt.getOwnerHeadPhotoMTime(), jt.getOwnerUid());
								bisUpdated = true;
							} catch (FTPDataTransferException e) {
							// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (FTPAbortedException e) {
							// TODO Auto-generated catch block
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
								client.download(jt.getPickerUid()+".jpg", avatar);
								bt.storeHeadPhotoModifyTime(ct, jt.getPikcerHeadPhotoMTime(), jt.getPickerUid());
								bisUpdated = true;
							} catch (FTPDataTransferException e) {
							// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (FTPAbortedException e) {
							// TODO Auto-generated catch block
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
					client.download(BaseUtil.getSelfUserInfo().getUserId()+".jpg", myAvatar);
					bt.storeHeadPhotoModifyTime(ct, BaseUtil.getSelfUserInfo().getHeadPhotoMTime(), BaseUtil.getSelfUserInfo().getUserId());
				} catch (FTPDataTransferException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPAbortedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
			if(true == bisUpdated)
			{
	            // if ftp new photo to local, clear cache so MainlistAdapter use new photo
				ImageLoader.getInstance().clearDiskCache();
	          ImageLoader.getInstance().clearMemoryCache();
			}
			
			client.disconnect(true);

		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPIllegalReplyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
