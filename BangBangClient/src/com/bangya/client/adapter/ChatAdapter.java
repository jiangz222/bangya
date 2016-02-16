package com.bangya.client.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import com.bangya.client.BBUI.BaseUtil;
import com.bangya.client.model.MessagesDTO;
import com.joeapp.bangya.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 
 * @author jiangzhi
 *
 */
@SuppressLint("SimpleDateFormat")
public class ChatAdapter extends BaseAdapter {
 
	private Context context;
	private List<MessagesDTO> chatMessages;

	public ChatAdapter(Context context, List<MessagesDTO> messages) {
		super();
		this.context = context;
		this.chatMessages = messages;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return chatMessages.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return chatMessages.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		MessagesDTO message = chatMessages.get(position);
		String createTime = null;
		//check messages direction
		if(message.getOwnerUid() == BaseUtil.getSelfUserInfo().getUserId())
		{
			convertView = LayoutInflater.from(context).inflate(
					R.layout.chat_item_sms_my_msg, null);
		}else
		{
			convertView = LayoutInflater.from(context).inflate(
					R.layout.chat_item_sms_rcv_msg, null);
		}
		LinearLayout layoutsms=(LinearLayout)convertView.findViewById(R.id.chatting_item_sms);
		TextView tvCreateTime=(TextView)convertView.findViewById(R.id.chatting_time_tv);

		String content=message.getContent();
		TextView txt=new TextView(context);
		txt.setGravity(Gravity.CENTER);
		txt.setTextColor(Color.BLACK);
		txt.setText(content);
		layoutsms.addView(txt);
		if(message.getCreateTime() !=null)
		{
			if(message.getCreateTime().getYear() != new Date().getYear())
			{
				createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(message.getCreateTime());
			}else
			{
				if(message.getCreateTime().getDay() != new Date().getDay())
				{
					createTime = new SimpleDateFormat("MM-dd HH:mm:ss").format(message.getCreateTime());
				}else{

						createTime = new SimpleDateFormat("HH:mm:ss").format(message.getCreateTime());
					
					}
				}
			}
		if(createTime == null){
			tvCreateTime.setVisibility(View.GONE);
		}else{
			tvCreateTime.setText(createTime);
		}
		return convertView;
	}

}
