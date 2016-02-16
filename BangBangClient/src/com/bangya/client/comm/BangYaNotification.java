package com.bangya.client.comm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.bangya.client.BBUI.BangYaWelcome;
import com.bangya.client.BBUI.HomeActivity;
import com.bangya.client.BBUI.Setting;
import com.joeapp.bangya.R;

public class BangYaNotification {
	public static void sendNotification(Context context,String content){
		if(Setting.isNotifyEnable == false){
			return;
		}
		NotificationManager notifier = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent myintent;	
		if(false == HomeActivity.bisLogin){
			myintent = new Intent(context, BangYaWelcome.class);
			myintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		}
		else {
			myintent = new Intent(context, HomeActivity.class);
			myintent.putExtra("IsLogin", true);
			myintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		}

		PendingIntent pi = PendingIntent.getActivity(context, 0, myintent, 0);
		Notification notify = new Notification.Builder(context) 
		.setAutoCancel(true)
		.setTicker("帮呀有更新")
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle(content)
		.setWhen(System.currentTimeMillis())
		.setContentIntent(pi) // app退出的情况下再启动有问题
		.setDefaults(Notification.FLAG_SHOW_LIGHTS) //Notification.DEFAULT_SOUND )
		.setLights(0xff00ff00, 0, 100)
	//do not like sound	.setSound(null)
		.build();

		notifier.notify(R.drawable.ic_launcher,notify);
	}
	public static void clearNotifycation(Context context){
		NotificationManager notifier = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		notifier.cancel(R.drawable.ic_launcher);

	}
}
