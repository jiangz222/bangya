package com.bangya.client.BBUI;

import java.util.ArrayList;
import java.util.List;



import com.bangya.client.Util.Constants;
import com.bangya.client.Util.MessageId;
import com.bangya.client.adapter.ChatAdapter;
import com.bangya.client.comm.InnerCommInterface;
import com.bangya.client.comm.PushReceiver;
import com.bangya.client.model.BangYaPushType;
import com.bangya.client.model.MessagesDTO;
import com.bangya.client.model.PushMsgDTO;
import com.joeapp.bangya.R;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends Activity implements InnerCommInterface {
	private String TAG = "CHAT";
	private String TITLE_NAME="聊天";
	private ListView chatListView; 
	private ChatAdapter chatAdapter = null;
	private List<MessagesDTO> chatMsgs = new ArrayList<MessagesDTO>();
	private EditText contentEdit = null;
	private String sendStr = null;
	private CircleProgressDiaglog cpd = null;
	private int listItemNumber = 0;
	private int jobId = 0;
	//private int ownerMsgsCounter = 0;
	//private int pickerMsgsCounter = 0;
	BaseUtil bu=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "oncreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity);
		getActionBar().setTitle(TITLE_NAME);   
		 bu = new BaseUtil();

		BaseUtil.innerComm.addCurrentActivity(this);
		
		Intent intent=getIntent();
		jobId = (int)intent.getIntExtra("jobid",0);

		this.initUI();
		this.initAdapter();
		//need jog id, usrid
		 
		//limit msgs number in current version,if open this limit
		//need db to store msgs in client,instead of request from server
		BaseUtil.innerComm.webappclient.reqMessages(jobId);
	}

	@Override
    public void onBackPressed() {
		finishSelf();
    }
	private void finishSelf()
	{
		BaseUtil.innerComm.delCurrentActivity(this);
		finish();
	}
	public void initUI()
	{
		Button sendBT = (Button)findViewById(R.id.chat_send);
		contentEdit = (EditText)findViewById(R.id.chat_input);
		chatListView = (ListView) findViewById(R.id.chat_lv);

		sendBT.setOnClickListener(listener);
	}
	public void initAdapter()
	{
		chatAdapter = new ChatAdapter(this, chatMsgs);
		chatListView.setAdapter(chatAdapter);	
		chatListView.setSelection(listItemNumber);
	}
	/**
	 * call when get messages from server
	 */
	public void UpdateAdapter(List<MessagesDTO> messages)
	{
		chatMsgs.addAll(messages);
		this.listItemNumber = messages.size();
		chatAdapter.notifyDataSetChanged();
		chatListView.setSelection(listItemNumber);
    	PushReceiver.PCCInfo.clearClientInfo(BangYaPushType.CHAT_MSG,jobId,this);
	}
	public void UpdateAdapterForOneMsg(MessagesDTO message)
	{
	//	if(0 == messages.size())
	//	{
		//	chatAdapter.notifyDataSetChanged();
		//	return;
		//}else
		//{
			chatMsgs.add(message);
			this.listItemNumber++;
		//}
		chatAdapter.notifyDataSetChanged();
		chatListView.setSelection(listItemNumber);
		contentEdit.setText("");
    	PushReceiver.PCCInfo.clearClientInfo(BangYaPushType.CHAT_MSG,message.jobId,this);

	}
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view.getId() == R.id.chat_send) {    
			//	if(ChatActivity.this.ownerMsgsCounter+1 >= Constants.MAX_NUMBER_MSGS_PER_USER){
		///			bu.toastShow(ChatActivity.this,"每人最多可发送8条留言");
		//			return;
		//		}
				String str = contentEdit.getText().toString().trim();
				if (str != null
						&& (sendStr = str.replaceAll("\r", "").replaceAll("\t", "").replaceAll("\n", "")
								.replaceAll("\f", "")) != "") {
					BaseUtil.innerComm.webappclient.sendMessage(jobId,BaseUtil.getSelfUserInfo().getUserId(),1,sendStr);
					cpd = new CircleProgressDiaglog();
					cpd.CreateProgressDiaglog(ChatActivity.this,null);
				}
			}
		}
	};
	private void updateMsgCounter(List<MessagesDTO> messages)
	{
		for(MessagesDTO iterator:messages){
			if(iterator.getOwnerUid() == BaseUtil.getSelfUserInfo().getUserId()){
			//	this.ownerMsgsCounter++;
			}else
			{
		//		this.pickerMsgsCounter++;
			}
		}
		
	}
	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub
		if(cpd != null)
		{
			cpd.CancleProgressDialog();
		}
		switch(msg.what)
		{
		    case MessageId.GET_MSGS_SUCCESS_UI:
		    {
		    	List<MessagesDTO> msgs = (List<MessagesDTO>)msg.obj;
				
		    	
		    	UpdateAdapter(msgs);
		    	updateMsgCounter(msgs);
		    	break;
		    }
		    case MessageId.GET_MSGS_FAIL_UI:
		    {
				bu.toastShow(this,"获取消息失败，请稍后再试");
		    	break;
		    }
		    case MessageId.SEND_MSG_SUCCESS_UI:
		    {
		    	MessagesDTO msgDTO = (MessagesDTO)msg.obj;
		    	UpdateAdapterForOneMsg(msgDTO);
			//	this.ownerMsgsCounter++;
				contentEdit.setText("");
		    	break;
		    }
		    case MessageId.SEND_MSG_FAIL_UI:
		    {
				bu.toastShow(this,"发送消息失败，请稍后再试");
		    	break;
		    }
		    case MessageId.PUSH_CHAT_MSG:
		    {
		    	
		    	PushMsgDTO pushMsg = (PushMsgDTO)msg.obj;
		    	if(pushMsg.jobId != this.jobId){
		    		return;
		    	}
		    	MessagesDTO msglocal  = pushMsg.msgDTO;
		    	UpdateAdapterForOneMsg(msglocal);
		    	//HomeActivity.PCCInfo.clearClientInfo(pushMsg.pushType,pushMsg.jobId);
			    break;
		    }
		    /*
		    case MessageId.CONNECT_SERVER_ERROR:
		    {
				Log.e(TAG, "connect server error");
				toastShow(this,"网络连接失败，请恢复后再试");
		    	break;
		    }*/
		}
	}

}
