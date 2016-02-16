package com.bangya.client.BBUI;

import java.io.File;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.bangya.client.Util.Constants;
import com.bangya.client.Util.FileUtil;
import com.bangya.client.Util.MessageId;
import com.bangya.client.auth.MyRSA;
import com.bangya.client.comm.InnerCommInterface;
import com.bangya.client.comm.WebAppClient;
import com.bangya.client.model.User;
import com.bangya.client.model.UserType;
import com.joeapp.bangya.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Register extends Activity implements InnerCommInterface  {
	String TAG="BBReg";
	private String TITLE_NAME = "注册";   
	Button registerBtn;
	Button editHeadBtn;
	ImageView headImg;
	EditText nickNameEdit;
	EditText pwdEdit;
	EditText pwdReEdit;
	EditText emailAddrs;
//	RadioGroup group;
//	RadioButton male;
//	RadioButton female;
	String gender=Constants.FEMALE;  //keep same as xml setting
	String nickName;
	String pwd;
	String repwd;
	String email;
	String headPath="//default"; //just temp,need to update to local path image which release with APK
	private static Uri fileUri;
	MyRSA rsa;
	private CircleProgressDiaglog cpd = null;
	BaseUtil bu = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "on create");
		super.onCreate(savedInstanceState);
		rsa= new MyRSA(this);
		bu = new BaseUtil();
		BaseUtil.innerComm.addCurrentActivity(this);
		setContentView(R.layout.register);
		this.initUI();
		getActionBar().setTitle(TITLE_NAME);   

	}
	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
		BaseUtil.innerComm.delCurrentActivity(this);

	}
	
	private void initUI()
	{
		registerBtn=(Button)findViewById(R.id.register);
	//	headImg=(ImageView)findViewById(R.id.reg_head);
		nickNameEdit=(EditText)findViewById(R.id.reg_nickname);
		pwdEdit=(EditText)findViewById(R.id.reg_pwd);
		pwdReEdit=(EditText)findViewById(R.id.reg_re_pwd);
		emailAddrs = (EditText)findViewById(R.id.reg_email);
	//	group = (RadioGroup) findViewById(R.id.reg_radioGroup);
	//	male=(RadioButton)findViewById(R.id.reg_male);
	//	female=(RadioButton)findViewById(R.id.reg_female);

	}
	public void BangBangSendReg(View view)//for button regiter
	{
		nickName=nickNameEdit.getText().toString().trim();
		pwd=pwdEdit.getText().toString().trim();
		repwd=pwdReEdit.getText().toString().trim();
		email= emailAddrs.getText().toString().trim();
		if(Constants.BB_FALSE == registerInputCheck(nickName,pwd,repwd,email))
		{
			return;
		}
			Log.e(TAG,"here come to register");
			cpd = new CircleProgressDiaglog();
			cpd.CreateProgressDiaglog(this,null);
			try {
				BaseUtil.innerComm.webappclient.register(nickName,rsa.encrypt(pwd.getBytes()),email,UserType.BANGBANG,"1",gender);
				return;
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bu.toastShow(this, "注册失败，请重试!");
			cpd.CancleProgressDialog();
			
	}
	//����ͷ��
	public void selectHeadImage(View view)
	{
		Log.i(TAG, "select head image");		
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setTitle("设置头像").setItems(new String[]{"拍照","相册"}, new DialogInterface.OnClickListener()
		{
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which==0){
							Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
							File file=FileUtil.createHeadImage("temp");
							fileUri=Uri.fromFile(file); 
							Log.i(TAG, "create file: "+file+"fileUri: "+fileUri);
							//���趨ʹͼ�񱣴浽�ƶ�λ��
							it.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
							startActivityForResult(it, Activity.DEFAULT_KEYS_DIALER);
						}
						else
						{
							//��ת��ͼƬ�������Ӧ�ã�ѡȡҪ���͵�ͼƬ
							Intent i = new Intent();
							i.setType("image/*");
							i.putExtra("return-data", true);
							i.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(i, Activity.DEFAULT_KEYS_SHORTCUT);
						}
					}
				}).create().show();
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{//���ڴ����ͼƬ��ȡ���༭����
		Log.i(TAG, "back to BB register");
		Log.i(TAG, "resultCode=="+resultCode+" , RESULT_OK=="+RESULT_OK);
		if(requestCode==Activity.DEFAULT_KEYS_DIALER){
			//дsd���������Ѿ���MediaStore���
			Log.i(TAG, "data2="+data);//uri�Ѿ�����,��data����
			headPath=fileUri.getPath();
			Bitmap bitmap=BitmapFactory.decodeFile(headPath);
			if(bitmap!=null){
				headImg.setImageBitmap(bitmap);
			}
			Log.i(TAG, "filePath2="+fileUri.getEncodedPath());
		}
		else if(requestCode==Activity.DEFAULT_KEYS_SHORTCUT){  
			Log.i(TAG, "data1="+data);
			Uri uri=data.getData();
			Log.i(TAG, "uri:"+uri.toString());
			File file=FileUtil.createHeadImage("temp");
			headPath=file.getAbsolutePath();
			boolean result=FileUtil.writeFile(getContentResolver(), file, uri);
			Bitmap bitmap=BitmapFactory.decodeFile(file.getAbsolutePath());
			if(bitmap!=null){
				headImg.setImageBitmap(bitmap);
			}
			Log.i(TAG, "wirte file result"+result);
		}
	}
	private boolean registerInputCheck(String nickname,String pwd, String repwd,String email)
	{
		if(nickname.length() == 0)
		{
			Log.e(TAG, "empty input username is not allowed when register");
			bu.toastShow(this,"姓名不能为空");
        	return Constants.BB_FALSE;
		}
	    Log.e(TAG, "register sex:"+gender);
		if(Constants.BB_FALSE == bu.checkEmailVaild(email, this)){
			return Constants.BB_FALSE;
		}
		if(Constants.BB_FALSE == bu.checkPasswordValid(pwd, this)){
			return Constants.BB_FALSE;
		}
		if(pwd.compareTo(repwd) != 0)
		{
			Log.e(TAG, "pwd an repwd is not the same");
			bu.toastShow(this,"两次输入的密码不同，请重新输入");
			return Constants.BB_FALSE;
		}
		return Constants.BB_TRUE;
	}
	public void setFelmale(View view)//for button regiter
	{
		gender=Constants.FEMALE;
	}
	public void setMale(View view)//for button regiter
	{
		gender = Constants.MALE;
	}
	@Override
    public void onBackPressed() {
	    //���Activityջ�еı�Activity
       BaseUtil.innerComm.delCurrentActivity(this);
		Intent it= new Intent(this,HomeActivity.class);	
    	it.putExtra("IsLogin", false);
		startActivity(it);
		finish();
    }
	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what)
		{
			case MessageId.REGISTER_SUCCESS_UI:
			{
        		Log.e(TAG,"register sucess");
    	    //    User myUserInfo = (User)msg.obj;
    	        /*
    	        String newPath="";
    	        if(headPath!=null && headPath.length()>0){
    	            newPath=FileUtil.HEAD_PATH+"/"+myUserInfo.getUserId()+".jpg";
    	            File file=new File(headPath);
    	            file.renameTo(new File(newPath));
                }
    	        else
    	        {
    	        	Log.e(TAG, "register succ ,but no input headfile,so user default headimage");
    	        }*/
      //      	//toastShow(this,"欢迎您！"+nickNameEdit.getText().toString().trim()+"!请重新登录！");
    			//logon directly
            	try {
					BaseUtil.innerComm.webappclient.Logon(email,rsa.encrypt(pwd.getBytes()));
					return;
            	} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	bu.toastShow(this, "注册失败，请重新登录");
            	cpd.CancleProgressDialog();
			}
			break;
			case MessageId.REGISTER_FAIL_UI:
			{
				if(null != cpd){
					cpd.CancleProgressDialog();
					cpd = null;
				}
        		Log.e(TAG,"register fail for unkonw reason");
        		bu.toastShow(this,"注册失败，请稍后再试");
			}
				break;
			case MessageId.REGISTER_FAIL_EMAIL_CONFLICT_UI:
			{
				if(null != cpd){
					cpd.CancleProgressDialog();
					cpd = null;
				}
        		Log.e(TAG,"register fail for email conflict");
        		bu.toastShow(this,"邮箱已注册，请更换邮箱地址再试");
        //    	emailAddrs.setText("");
			}
				break;
			case MessageId.LOGIN_FAIL_UI:
			{
				if(null != cpd){
					cpd.CancleProgressDialog();
					cpd = null;
				}
				bu.toastShow(this,"用户名密码错误，请重新登录");
				Log.e(TAG, "log on failed");
				BaseUtil.innerComm.delCurrentActivity(this);
            	Intent it= new Intent(Register.this,Login.class);
            	it.putExtra("emailAddr", email);
            	it.putExtra("password", pwd);
            	startActivity(it);
            	finish();
				break;
			}
			case MessageId.LOGIN_SUCCESS_UI:
			{
				bu.setSharePreferencesForToken(email,WebAppClient.MY_AUTH,this);
        		BaseUtil.innerComm.webappclient.getUserInfoByUserName(email);
				break;
			}
			case MessageId.GET_USER_BY_NAME_SUCCESS_UI:
			{
				if(null != cpd){
					cpd.CancleProgressDialog();
					cpd = null;
				}
			   User user = new User();
			   user = (User)msg.obj;
			   BaseUtil.setSelfUserInfo(user);
	    	   Setting.isNotifyEnable = bu.getNotifyPreference(this);	
       		Intent it= new Intent(this,HomeActivity.class);	
        	it.putExtra("IsLogin", true);
       	startActivity(it);
    		BaseUtil.innerComm.delCurrentActivity(this);
    		finish();

        		// go to activity of homepage
			}
			break;
			case MessageId.GET_USER_BY_NAME_FAIL_UI:
			{
				if(null != cpd){
					cpd.CancleProgressDialog();
					cpd = null;
				}
				bu.toastShow(this,"获取用户信息失败，请重新登录");
            	Intent it= new Intent(Register.this,Login.class);
            	it.putExtra("emailAddr", email);
            	it.putExtra("password", pwd);
            	startActivity(it);
            	finish();
			}
			break;
			case MessageId.CONNECT_SERVER_ERROR:
			{
				if(null != cpd){
					cpd.CancleProgressDialog();
					cpd = null;
				}
			//	Log.e(TAG, "connect server error");
				bu.toastShow(this,"网络连接失败，请恢复后再试");
			}
			break;
			default:
				if(null != cpd){
					cpd.CancleProgressDialog();
					cpd = null;
				}
        		Log.e(TAG,"unexpected msg"+msg.what);
				break;
		}
	
	}
	
}
