package com.bangya.client.BBUI;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.bangya.client.Util.Constants;
import com.bangya.client.Util.MessageId;
import com.bangya.client.auth.MyRSA;
import com.bangya.client.comm.InnerCommInterface;
import com.bangya.client.comm.WebAppClient;
import com.bangya.client.model.User;
import com.joeapp.bangya.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity implements InnerCommInterface {
	private final String TAG = "BBLogin";
	private String TITLE_NAME = "登录";
	private EditText usernameEdit;
	private EditText pwdEdit;
	private Button forgetPwdBtn;
	private String gusername;
	private String gpwd;
	private CircleProgressDiaglog cpd =null;
	BaseUtil bu = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "on create");
		super.onCreate(savedInstanceState);
		 bu = new BaseUtil();
	    BaseUtil.innerComm.addCurrentActivity(this);
		this.initSys();
		getActionBar().setTitle(TITLE_NAME);   
	}
	@Override
	protected void onResume(){
		Log.i(TAG, "onresume");
		forgetPwdBtn.setVisibility(View.GONE);
		super.onResume();
	}
	@Override
	protected void onStart(){
		Log.i(TAG, "onStart");
		super.onResume();
	}
	private void initSys()
	{	
		Intent intent=getIntent();
		String username=intent.getStringExtra("emailAddr");
		Log.i(TAG, "onCreate() username="+username);
		setContentView(R.layout.login);
		usernameEdit=(EditText)findViewById(R.id.username);
		pwdEdit=(EditText)findViewById(R.id.pwd);
		forgetPwdBtn = (Button)findViewById(R.id.forget_pwd);
		forgetPwdBtn.setVisibility(View.GONE);
		if(username != null){
		    // 这是从注册界面注册成功后返回到登陆界面来的
			Log.i(TAG,"return login from register successful");
			usernameEdit.setText(username);
		    pwdEdit.setText("");
		}
		else 
		{
			//没有获取到,重新登录
			Log.i(TAG,"never login,login pls!");
			return;
		}

	}
	@Override
    public void onBackPressed() {
	    //���Activityջ�еı�Activity
        BaseUtil.innerComm.delCurrentActivity(this);
        finish();
		// go to activity of homepage
		Intent it= new Intent(Login.this,HomeActivity.class);	
    	it.putExtra("IsLogin", false);
		startActivity(it);
    }
	/*
	public void BangBangReg(View view)//for button
	{
		Intent it= new Intent(BBLogin.this,BBRegister.class);	
		startActivity(it);
		finish();
	}*/
	public void BangBangLogin(View view)//for button
	{		
		usernameEdit=(EditText)findViewById(R.id.username);
		pwdEdit=(EditText)findViewById(R.id.pwd);
		gusername = usernameEdit.getText().toString().trim();
		/* pls encrypt  pwd here */
		gpwd = pwdEdit.getText().toString().trim();
		
		if(Constants.BB_FALSE == bu.checkEmailVaild(gusername, this)){
			return;
		}
		if(Constants.BB_FALSE == bu.checkPasswordValid(gpwd, this)){
			return;
		}
		logintoserver();
		
	}
	public void BangBangLoginWeixin(View view) // for button
	{

	}
	private void logintoserver()
	{
		cpd = new CircleProgressDiaglog();
		cpd.CreateProgressDiaglog(this,null);
		MyRSA rsa= new MyRSA(this);
		try {
			BaseUtil.innerComm.webappclient.Logon(gusername,rsa.encrypt(gpwd.getBytes()));
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
		bu.toastShow(this, "密码发送失败，请重试！");
	}
	/*
	 *  listener for forget password
	 */
	public void forgetPassword(View view){
		Intent it= new Intent(Login.this,ResetPassWord.class);	
		startActivity(it);
	}
	/*
	private boolean getUserInfo()
	{
		User loginUserInfo	= new User();
		loginUserInfo = getPreference();
		if(loginUserInfo.getUserName().equals("")|| loginUserInfo.getPassWord().equals(""))
		{
			Log.e(TAG, "empty username "+loginUserInfo.getUserName()+"or pwd"+loginUserInfo.getPassWord());
	        return Constants.BB_FALSE;
		}
		gusername = loginUserInfo.getUserName();
		gpwd = loginUserInfo.getPassWord();
        return Constants.BB_TRUE;
	}*/
	public void processMessage(Message msg)
	{
		switch(msg.what)
		{
			case MessageId.LOGIN_SUCCESS_UI:
			{
        		/* 登录成功, 保存用户名 token以备下次直接登录 */
				bu.setSharePreferencesForToken(gusername,WebAppClient.MY_AUTH,this);
        		/* 存入内存,长久保存  */
        		Log.e(TAG,"login sucess from server,username "+gusername+" passwd "+gpwd);
        		BaseUtil.getSelfUserInfo().setUserName(gusername);
        		BaseUtil.getSelfUserInfo().setPassWord(gpwd);
        	    //销毁Activity栈中的本Activity
        		
        		//添加获取userinfo的流程，通过auth和username（email即可）
        		BaseUtil.innerComm.webappclient.getUserInfoByUserName(gusername);

			}
				break;
			case MessageId.LOGIN_FAIL_UI:
			{
				if(null != cpd){
					cpd.CancleProgressDialog();
					cpd = null;
				}
        		Log.e(TAG,"login failed from server,username "+gusername+"passwd "+gpwd);
        		bu.toastShow(this,"登录失败,请检查用户名密码是否正确");
        		forgetPwdBtn.setVisibility(View.VISIBLE);
    		//	setContentView(R.layout.login);
			}
			break;
			case MessageId.GET_USER_BY_NAME_SUCCESS_UI:
			{
			   User user = new User();
			   user = (User)msg.obj;
			   BaseUtil.setSelfUserInfo(user);
				if(null != cpd){
					cpd.CancleProgressDialog();
					cpd = null;
				}
        		BaseUtil.innerComm.delCurrentActivity(this);
        		finish();
	    		Setting.isNotifyEnable = bu.getNotifyPreference(this);	
	    		// should get system info(e.g. check if update version needed)
	    		//  but never mind, leave it to next time bangyawelcome start
        		// go to activity of homepage
        		Intent it= new Intent(Login.this,HomeActivity.class);	
            	it.putExtra("IsLogin", true);
        		startActivity(it);
			}
			break;
			case MessageId.GET_USER_BY_NAME_FAIL_UI:
			{
				if(null != cpd){
					cpd.CancleProgressDialog();
					cpd = null;
				}
        		Log.e(TAG,"login failed from server,username "+gusername+"passwd "+gpwd);
        		bu.toastShow(this,"获取用户信息失败，请稍后再试");
    		//	setContentView(R.layout.login);
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
