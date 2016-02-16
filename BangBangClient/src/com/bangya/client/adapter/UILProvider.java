package com.bangya.client.adapter;

import java.io.File;

import android.content.Context;

import com.bangya.client.BBUI.BaseUtil;
import com.bangya.client.Util.FileUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class UILProvider {

		public  UILProvider(Context context)
		{
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
			.threadPriority(3)
			.denyCacheImageMultipleSizesInMemory()
			.diskCacheFileNameGenerator(new Md5FileNameGenerator())
			.diskCacheSize(10 * 1024 * 1024) // 10 Mb
			.tasksProcessingOrder(QueueProcessingType.LIFO)
			.writeDebugLogs() // Remove for release app
			.build();
	// Initialize ImageLoader with configuration.
			ImageLoader.getInstance().init(config);
			
		}
		public static String getLocalPhotowithPath(int uid)
		{
			//this format is required by UIL
			return "file://"+FileUtil.HEAD_PATH + File.separator  + uid+ ".jpg";
		}
}
