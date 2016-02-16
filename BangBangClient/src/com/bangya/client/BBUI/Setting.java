package com.bangya.client.BBUI;

import java.io.File;

import com.bangya.client.Util.FileUtil;
import com.bangya.client.Util.MessageId;
import com.bangya.client.comm.InnerCommAdapter;
import com.bangya.client.comm.InnerCommInterface;
import com.bangya.client.comm.SFtpRequest;
import com.bangya.client.model.SystemDTO;
import com.joeapp.bangya.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

public class Setting extends Activity  implements InnerCommInterface {
	private String TITLE_NAME = "设置";
	public static boolean isNotifyEnable = true;
	public 
	BaseUtil bu =null;
	private String TAG="SETTING";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 BaseUtil.innerComm.addCurrentActivity(this);
		setContentView(R.layout.setting);
		bu= new BaseUtil();
		getActionBar().setTitle(TITLE_NAME);
		initUI();
	}
	@Override
    public void onDestroy() {
		BaseUtil.innerComm.delCurrentActivity(this);
		super.onDestroy();
    }
	@Override
    public void onBackPressed() {
        this.finishSelf();
    }
	private void finishSelf()
	{
		BaseUtil.innerComm.delCurrentActivity(this);
		finish();
	}
	public void initUI(){
		Switch s = (Switch)findViewById(R.id.switch_notify);
		TextView lchangePassword = (TextView)findViewById(R.id.change_password);
		lchangePassword.setOnClickListener(listener);
		LinearLayout checkVerion = (LinearLayout)findViewById(R.id.check_version_update);
		ImageView versionNotification = (ImageView)findViewById(R.id.check_version_notification);
		TextView tvcurrentVersion = (TextView)findViewById(R.id.tv_current_version_input);
		tvcurrentVersion.setText(" "+BaseUtil.getCurrentVersion(this));
		if(BaseUtil.getVersionNeedUpdateFlag() == false){
			versionNotification.setVisibility(View.GONE);
		}
		
		checkVerion.setOnClickListener(listener);
		s.setChecked(isNotifyEnable);
		s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {  
              @Override  
              public void onCheckedChanged(CompoundButton buttonView,  
                      boolean isChecked) {  
            	  if(isChecked){
            		  isNotifyEnable = true;
            		  bu.setSharePreferencesForNotify(true,Setting.this);

            	  }else{
            		  isNotifyEnable = false;
            		  bu.setSharePreferencesForNotify(false,Setting.this);

            	  }
              }
          }); 
	}
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if(view.getId() == R.id.change_password)
			{	
				Intent it=new Intent(Setting.this,ChangePassWord.class);
				startActivity(it);

			}else if(view.getId() == R.id.check_version_update){
				BaseUtil.innerComm.webappclient.getSystemInfo();
			}else
			{
				Log.e(TAG, "impossible");

			}
		}
	};
	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
			case MessageId.CONNECT_SERVER_ERROR:
			{
				bu.toastShow(this,"网络连接失败，请恢复后再试");
				break;
			}

			case MessageId.GET_SYSTEM_INFO_SUCCESS:
			{
				BaseUtil.setSystemInfo((SystemDTO)msg.obj);
				Log.e(TAG, "pkg info:"+BaseUtil.getSystemInfo().getLatestClientName());
				startVersionUpdate(this,msg);
				break;
			}
			case MessageId.GET_SYSTEM_INFO_FAIL:
			{
				bu.toastShow(this, "获取版本信息失败，请稍后再试");

				break;
			}

			default:
			{
				Log.i(TAG, "unknow msg:"+msg.what);
			}
		}
	}
    public void startVersionUpdate(final Context ct,Message msg){
		final SystemDTO systemInfo = (SystemDTO)msg.obj;
		if(BaseUtil.isVersionUpdateNeeded(ct, systemInfo)){
			// new version avaliable, check if user want to update
			new AlertDialog.Builder(ct)
			.setTitle("有新版本，是否开始升级？")
			.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
	            	// do not want update, return
	            	return;
	            }
			})
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
	            	// want to update
    			if(!BaseUtil.isWifiConnected(ct)){
    				// if wifi is not connected, check if user want to update
					new AlertDialog.Builder(ct)
						.setTitle("当前为非wifi连接，是否继续？")
						.setMessage("继续下载可能会消耗一定流量")
						.setPositiveButton("确定",new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int which) {
				            	// yes, update
								BaseUtil.getFileFromSvr(FileUtil.APK_PATH+systemInfo.getLatestClientName(),
										SFtpRequest.apkPathOnSvr,systemInfo.getLatestClientName());        			            }
						})
						.setNegativeButton("取消",new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface dialog, int which) {
				            	// no, return
								return;
							}
						})
						.show();
					}else{
						// wifi is connected, update it
						BaseUtil.getFileFromSvr(FileUtil.APK_PATH+systemInfo.getLatestClientName(),
								SFtpRequest.apkPathOnSvr,systemInfo.getLatestClientName());   
					}			            
	    		}
			})
			.show();
	
		}else{
			BaseUtil bu = new BaseUtil();
			bu.toastShow(ct, "当前已是最新版本");
			BaseUtil.setVersionNeedUpdateFlag(false);

		}
	return;
   }
}


