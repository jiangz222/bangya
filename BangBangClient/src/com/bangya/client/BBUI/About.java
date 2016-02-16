package com.bangya.client.BBUI;

import  com.joeapp.bangya.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class About extends Activity {
	private String TITLE_NAME = "关于帮呀";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		getActionBar().setTitle(TITLE_NAME);   

	}


}
