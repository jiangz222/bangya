package com.bangya.client.BBUI;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class CircleProgressDiaglog extends Activity{
	private  ProgressDialog progressDialog = null;  
    private String TAG="CPD";
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
      }
   /**
    * 
    * @param context
    * @param showText  "default "refreshing hardly" if showText is null"
    */
    
    public void CreateProgressDiaglog(Context context,String showText)
    {
    	if(null == showText)
    	{
    		showText = "努力加载中......";
    	}
    	Log.i(TAG,"create progress DIA");
    	CancleProgressDialog();
    	//创建ProgressDialog对象  
        progressDialog = new ProgressDialog(context);  
        // 设置进度条风格，风格为圆形，旋转的  
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
        // 设置ProgressDialog 标题  
       // progressDialog.setTitle("请稍等");  
        // 设置ProgressDialog 提示信息  
        
        progressDialog.setMessage(showText);  
        // 设置ProgressDialog 标题图标  
       // progressDialog.setIcon(R.drawable.ic_launcher);  
        // 设置ProgressDialog 的进度条是否不明确  
        progressDialog.setIndeterminate(false);           
        // 设置ProgressDialog 是否可以按退回按键取消  
        progressDialog.setCancelable(true);           
        //设置ProgressDialog 的一个Button  
  //      progressDialog.setButton("确定", new SureButtonListener());  
        // 让ProgressDialog显示  
        
        progressDialog.show(); 
    }
    public void CancleProgressDialog()
    {
    	if(null != this.progressDialog){
        	Log.i(TAG,"create progress DIA");

        	progressDialog.cancel();
        	
        	progressDialog=null;
    	}
    }
	//@Override
   // public void onBackPressed() {
//		this.CancleProgressDialog();
  //  }
}
