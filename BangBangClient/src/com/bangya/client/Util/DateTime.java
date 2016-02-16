package com.bangya.client.Util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.util.Log;

public class DateTime {
	public static int dateToBit(int year,int month,int dayOfMonth)
	{
		return ((year<<9)+(month<<5)+dayOfMonth);
	}
	public static int timeToBit(int hr,int min)
	{
		return ((hr<<6)+min);
	}
	public static double dateTimeToBit(int year,int month,int dayOfMonth,int hr,int min)
	{	
		return (double)((year<<20)+(month<<16)+(dayOfMonth<<11)+(hr<<6)+min);
	}
	public static long dateTimeToMillsfrom1970(int year,int month,int dayOfMonth,int hr,int min)
	{
		Calendar calendar = Calendar.getInstance();
		TimeZone tz;
		long timeMills;
		calendar.clear(); 
		tz = TimeZone.getDefault();
		calendar.setTimeZone(tz);
		//set接口不设时区默认为当前时区
		calendar.set(year, month, dayOfMonth, hr, min);
		//getTimeInMillis返回为set时间经过时区计算为UTC后的mills
		timeMills = calendar.getTimeInMillis();
		System.out.println("ms and date:"+timeMills);
		return timeMills;
	}
	public static String MSfrom1970TodateTime(long mills)
	{
		String datetime;
		Calendar calendar = Calendar.getInstance();
        calendar.clear(); 

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		//setTimeInMillis 默认传入参数即相对于GMT  epoch 
		calendar.setTimeInMillis(mills);
		//gettime 得到的date是本地时区下的信息，即将UTC的millis进行了时区转换后的date
		Date date = calendar.getTime();
		datetime = formatter.format(date);
		return datetime;	
	}
	public static void BitTodateTime(long dateTimeBit,
			int year,int month,int dayOfMonth,int hr,int min)
	{	
		min =(int)((dateTimeBit)&(0x3f));
		hr =(int)( (dateTimeBit>>6)&0x1f);
		dayOfMonth =(int) (dateTimeBit>>11)&0x1f;
		month =(int) (dateTimeBit>>16)&0xf;
		year =(int) (dateTimeBit>>20);
	}
}
