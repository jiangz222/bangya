package com.bangya.client.BBUI;

import com.joeapp.bangya.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class BYNotification extends Activity {
	NotificationManager manager = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	@SuppressLint("NewApi")
	public void addNotification(String content)
	{
		manager = (NotificationManager) this  
		        .getSystemService(Context.NOTIFICATION_SERVICE); 
		Notification notify = new Notification.Builder(this) 
									.setAutoCancel(true)
									.setTicker("有新的消息")
									.setSmallIcon(R.drawable.ic_launcher)
									.setContentTitle(content)
									.setWhen(System.currentTimeMillis())
									.build();
		manager.notify(R.drawable.ic_launcher,notify);
	}
}
