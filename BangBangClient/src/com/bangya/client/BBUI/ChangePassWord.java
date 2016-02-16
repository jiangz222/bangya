package com.bangya.client.BBUI;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bangya.client.Util.Constants;
import com.bangya.client.Util.MessageId;
import com.bangya.client.auth.MyRSA;
import com.bangya.client.comm.InnerCommInterface;
import com.joeapp.bangya.R;

public class ChangePassWord extends Activity implements InnerCommInterface {
	private BaseUtil bu;
	private String TAG = "ChangePassWord";
	private String TITLE_NAME = "更改密码";
	private EditText pwdNewEdit;
	private EditText pwdOldEdit;
	private EditText pwdReNewEdit;
	private String pwdNew;
	private String pwdOld;
	private String pwdReNew;
	private CircleProgressDiaglog cpd = new CircleProgressDiaglog() ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "on create");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_password);
		 bu = new BaseUtil();
	    BaseUtil.innerComm.addCurrentActivity(this);
		this.initUI();
		getActionBar().setTitle(TITLE_NAME);   
	}
	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
		BaseUtil.innerComm.delCurrentActivity(this);
	}
	@Override
    public void onBackPressed() {
		// only way start this activity is from Login  
		BaseUtil.innerComm.delCurrentActivity(this);
		finish();
    }
	private void initUI(){
		pwdOldEdit = (EditText)findViewById(R.id.changepwd_old);
		pwdNewEdit = (EditText)findViewById(R.id.changepwd_new);
		pwdReNewEdit = (EditText)findViewById(R.id.changepwd_re_new);
		Button btConfirm = (Button)findViewById(R.id.change_pwd_confirm);
		btConfirm.setOnClickListener(listener);
	}
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			pwdOld = pwdOldEdit.getText().toString().trim();
			pwdNew = pwdNewEdit.getText().toString().trim();
			pwdReNew = pwdReNewEdit.getText().toString().trim();
			
			if(view.getId() == R.id.change_pwd_confirm)
			{
				handleChangePasswordReq();	
			}else 
			{
				Log.e(TAG, "impossible");

			}
		}
	};
	private void handleChangePasswordReq(){
		if(Constants.BB_FALSE == bu.checkPasswordValid(pwdOld, this)){
			return;
		}
		if(Constants.BB_FALSE == bu.checkPasswordValid(pwdNew, this)){
			return;
		}
		if(pwdNew.compareTo(pwdReNew) != 0)
		{
			Log.e(TAG, "pwd an repwd is not the same");
			bu.toastShow(this,"两次输入的新密码不同，请重新输入");
        	return;
		}
		cpd.CreateProgressDiaglog(this, "密码重置中，请稍后...");
		MyRSA rsa= new MyRSA(this);

		try {
			BaseUtil.innerComm.webappclient.ChangePassword(BaseUtil.getSelfUserInfo().getUserId(),rsa.encrypt(pwdOld.getBytes()),rsa.encrypt(pwdNew.getBytes()));
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
		bu.toastShow(this, "更新密码失败！请重试");
		cpd.CancleProgressDialog();
	}
	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub
		cpd.CancleProgressDialog();
		switch(msg.what){
		case MessageId.CHANGE_PASSWORD_FAIL:
		{
			bu.toastShow(this, "更新密码失败，如有疑问，请联系我们:service@bangya365.com");
			break;
		}
		case MessageId.CHANGE_PASSWORD_SUCCESS:
		{
			bu.toastShow(this, "更新密码成功，下次登录请使用新密码");
			BaseUtil.innerComm.delCurrentActivity(this);
			finish();
			break;	
		}
		case MessageId.EMAIL_NOT_EXIST:
		{
			bu.toastShow(this, "用户不存在，更新密码失败，如有疑问，请联系我们:service@bangya365.com");
			break;	
		}
		case MessageId.CHANGE_PASSWORD_OLD_MISMATCH:
		{
			bu.toastShow(this, "您输入的旧密码错误，更新密码失败");
			break;	
		}
		default:
		{
			Log.i(TAG, "unexpected msg:"+msg.what);
		}
		}
	}

}
