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
/**
 * 1. check if email is empty and exist in server DB
 *	2. if yes,check if new password and re new password is match
 * 3. if yes, check if pass_code is matched with server DB
 *	4. if yes, update new password to  server DB
 *  5. DONE
 *  ps. everytime user request reset password code, update server DB
 * @author root
 *
 */
public class ResetPassWord extends Activity implements InnerCommInterface {
	private String TAG = "ResetPWD";
	private String TITLE_NAME = "重置密码";
	private BaseUtil bu;
	private EditText indeitfyCodeEdit;
	private EditText pwdEdit;
	private EditText pwdReEdit;
	private EditText emailAddrsEdit;
	private Button btnConfirm;
	private Button reqIdentifyCodeBtn;
	private CircleProgressDiaglog cpd = new CircleProgressDiaglog() ;

	private String newPwd;
	private String reNewPwd;
	private String email;
	private String identifyCode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "on create");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reset_password);

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
		emailAddrsEdit = (EditText)findViewById(R.id.reset_email);
		pwdEdit = (EditText)findViewById(R.id.reset_pwd);
		pwdReEdit = (EditText)findViewById(R.id.reset_re_pwd);
		indeitfyCodeEdit = (EditText)findViewById(R.id.reset_passwd_identify_code);
		btnConfirm = (Button)findViewById(R.id.reset_pwd_confirm);
	   reqIdentifyCodeBtn = (Button)findViewById(R.id.req_reset_pass_code);
	   btnConfirm.setOnClickListener(listener);
	   reqIdentifyCodeBtn.setOnClickListener(listener);

	}

	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			email = emailAddrsEdit.getText().toString().trim();
			newPwd = pwdEdit.getText().toString().trim();
			reNewPwd = pwdReEdit.getText().toString().trim();
			identifyCode = indeitfyCodeEdit.getText().toString().trim();
			if(view.getId() == R.id.reset_pwd_confirm)
			{
				handleResetPWDReq();	
			}else if(view.getId() == R.id.req_reset_pass_code)
			{	
				handleReqIdentifyCode();
			}else
			{
				Log.e(TAG, "no chance");

			}
		}
	};
	private void handleResetPWDReq()
	{
		
		if(Constants.BB_FALSE == bu.checkEmailVaild(email, ResetPassWord.this)){
			return;
		}
		if(Constants.BB_FALSE == bu.checkPasswordValid(newPwd, ResetPassWord.this)){
			return;
		}
		if(newPwd.compareTo(reNewPwd) != 0)
		{
			Log.e(TAG, "pwd an repwd is not the same");
			bu.toastShow(ResetPassWord.this,"两次输入的密码不同，请重新输入");
        	return;
		}
		if(identifyCode.length() == 0)
		{
			Log.e(TAG, "empty input identifyCode is not allowed when register");
			bu.toastShow(ResetPassWord.this,"请输入重置的验证码");
        	return;
		}
		// we do not have token here, server should do not care the token
		cpd.CreateProgressDiaglog(this, "密码重置中，请稍后...");
		MyRSA rsa= new MyRSA(this);

		try {
			BaseUtil.innerComm.webappclient.ResetPassword(email,rsa.encrypt(newPwd.getBytes()),identifyCode);
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
		bu.toastShow(this, "密码重置失败，请重试!");
		cpd.CancleProgressDialog();
	}
	private void handleReqIdentifyCode(){
		if(email.length() == 0)
		{
			Log.e(TAG, "empty input email is not allowed ");
			bu.toastShow(this,"请输入邮箱地址");
			return;
		}
		if(false == email.contains("@"))
		{
			Log.e(TAG, "invalide email"+email);
			bu.toastShow(this,"请输入有效的邮箱地址");
        	return;
		}
		cpd.CreateProgressDiaglog(this, "重置验证码发送中，请稍后...");
		BaseUtil.innerComm.webappclient.reqIdentifyCode(email);
	}
	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub
		cpd.CancleProgressDialog();
		switch(msg.what){
		case MessageId.RESET_PASSWORD_FAIL:
		{
			bu.toastShow(this, "重置密码失败，请联系我们:service@bangya365.com");
			break;
		}
		case MessageId.RESET_PASSWORD_SUCCESS:
		{
			bu.toastShow(this, "重置密码成功，请用新密码重新登录");
			BaseUtil.innerComm.delCurrentActivity(this);
			finish();
			break;	
		}
		case MessageId.IDENTIFY_CODE_MISMATCH:
		{
			bu.toastShow(this, "您输入的重置验证码不正确，请重新输入");
			break;	
		}
		case MessageId.EMAIL_NOT_EXIST:
		{
			bu.toastShow(this, "您输入的邮箱账户未注册，请重新输入");
			break;	
		}
		 case MessageId.REQ_IDENTIFY_CODE_SUCCESS:
		{
			bu.toastShow(this, "验证码已经发送到您的邮箱，请查收后输入验证码");
			break;
		}
		case MessageId.REQ_IDENTIFY_CODE_FAIL:
		{
			bu.toastShow(this, "重置验证码发送失败，请重试");
			break;
		}
		default:
			Log.i(TAG, "unknown msg:"+msg.what);
			break;
		}

	}

}
