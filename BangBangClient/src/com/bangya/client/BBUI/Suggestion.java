package com.bangya.client.BBUI;

import com.bangya.client.Util.MessageId;
import com.bangya.client.comm.InnerCommInterface;
import com.joeapp.bangya.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class Suggestion extends Activity implements InnerCommInterface {
	private String TAG = "Suggest";
	private String TITLE_NAME = "吐嘈";
	EditText edSuggestion;
	BaseUtil bu = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suggestion);
		bu = new BaseUtil();
		BaseUtil.innerComm.addCurrentActivity(this);
		edSuggestion = (EditText) findViewById(R.id.suggest_content);
		getActionBar().setTitle(TITLE_NAME);   

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
      getMenuInflater().inflate(R.menu.suggestion, menu );

		return true;
	}
	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_suggestion:
	        	Suggestion.this.sendSuggestion();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	public void sendSuggestion()
	{
		String input = edSuggestion.getText().toString().trim();
		if(input.length() == 0)
		{
			bu.toastShow(this,"请输入有效的内容");
			return;
		}
		if(input.length()>240)
		{
			bu.toastShow(this,"请将字数控制在240以内，谢谢");
			return;
		}
		BaseUtil.innerComm.webappclient.sendSuggestion(BaseUtil.getSelfUserInfo().getUserId(),input);
}
	@Override
    public void onBackPressed() {
        this.backToHomePage();
    }
	private void backToHomePage()
	{
		//销毁Activity栈中的本Activity
		BaseUtil.innerComm.delCurrentActivity(this);
		finish();
	}
	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what)
		{
			case MessageId.SEND_SUGGESTION_SUCCESS_UI:
			{
				bu.toastShow(this,"发送成功，我们会尽快处理您的建议");
				backToHomePage();
				break;
			}
			case MessageId.SEND_SUGGESTION_FAIL_UI:
			{
				bu.toastShow(this,"发送失败，请稍后再试");
				break;
			}
			/*
			case MessageId.CONNECT_SERVER_ERROR:
			{
				toastShow(this,"网络连接失败，请恢复后再试");

			}
			break;*/
			default:
				Log.e(TAG, "unexpected msg"+msg.what);
		}
	}

}
