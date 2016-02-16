package com.bangya.client.BBUI;

import java.util.ArrayList;

import com.joeapp.bangya.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

public class IntroductionActivity extends Activity {
    /** viewpager组件 */
    private ViewPager mViewPager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Holo_Light_NoActionBar);
		setContentView(R.layout.introduction_main);
		initUI();
	}
	private void initUI(){
        mViewPager = (ViewPager) findViewById(R.id.intro_viewpager);
        // onPageChangeListener, can do something when page changed, e.g. indicator, but we do not need it here
       // mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        LayoutInflater layoutInflater = LayoutInflater.from(this);// views
        View view1 = layoutInflater.inflate(R.layout.introduction_1, null);
        View view2 = layoutInflater.inflate(R.layout.introduction_2, null);
        View view3 = layoutInflater.inflate(R.layout.introduction_3, null);
        View view4 = layoutInflater.inflate(R.layout.introduction_4, null);
        
        final ArrayList<View> views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);

        // viewpager的内容adapter
        PagerAdapter mPagerAdapter = new PagerAdapter() {

             @Override
             public boolean isViewFromObject(View arg0, Object arg1) {
                  return arg0 == arg1;
             }

             @Override
             public int getCount() {
                  return views.size();
             }

             @Override
             public void destroyItem(View container, int position, Object object) {
                  ((ViewPager) container).removeView(views.get(position));
             }

             @Override
             public Object instantiateItem(View container, int position) {
                  ((ViewPager) container).addView(views.get(position));
                  return views.get(position);
             }
        };

        mViewPager.setAdapter(mPagerAdapter);
	}
	public void btn_start(View view)
	{
		Intent it	= new Intent(this,HomeActivity.class);	
    	
		it.putExtra("IsLogin", false);
    		// we do not have UID here, so can not get correct notifyFlag from preference
    		// change the correct value when login or register
    	Setting.isNotifyEnable = true;
		finish();
		startActivity(it);
	}
}
