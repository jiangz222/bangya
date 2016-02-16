package com.bangya.client.BBUI;

import com.bangya.client.adapter.UILProvider;
import com.joeapp.bangya.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class ShowImageInFullScreen extends Activity {
	private String TAG="SIIFS";
	private DisplayImageOptions options;
	private int uid;
	private BaseUtil bu = new BaseUtil();

	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "oncreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_image_full_screen);
		Intent intent=getIntent();
		uid = (int)intent.getIntExtra("uid",0);
		if(uid == 0){
			bu.toastShow(this, "图像显示失败");
			finish();
		}
	//	getActionBar().setTitle(TITLE_NAME); 
		this.initUIL();
		this.showImage();
	}
	private void initUIL()
	{
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.default_head)
		.showImageForEmptyUri(R.drawable.default_head)
		.showImageOnFail(R.drawable.default_head)
		.cacheInMemory(false)
		.cacheOnDisk(false)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}
	private void showImage()
	{
		ImageView ivImage = (ImageView) findViewById(R.id.show_image_full_screen);
    	ImageLoader.getInstance().displayImage(UILProvider.getLocalPhotowithPath(uid), ivImage, options);
	}
}
