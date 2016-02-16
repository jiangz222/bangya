package com.bangya.client.BBUI;

import java.util.Locale;




import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bangya.client.adapter.UILProvider;
import com.bangya.client.comm.InnerCommAdapter;
import com.bangya.client.comm.PushReceiver;
import com.bangya.client.location.LocationInfo;
import com.bangya.client.location.LocationResult;
import com.bangya.client.model.TabIdx;
import com.bangya.client.widget.PullToRefreshListView;
import com.igexin.sdk.PushManager;
import com.joeapp.bangya.R;
public class HomeActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	public SectionsPagerAdapter mSectionsPagerAdapter;
	 public static  LocationInfo locationinfo = null;
	public static Location lt = null;
	private String TAG = "HomeActivity";
	public  Toast gToast = null;
	private static int backPressedCount = 0;
	public static boolean bisLogin = false;
	BaseUtil bu=null;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager = null;
	public static boolean pushManagerInited = false;
	static ActionBar actionBar = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	   // setTheme(android.R.style.Theme_Holo_Light);

		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.activity_main);
		new UILProvider(this);
		 bu = new BaseUtil();
		 InnerCommAdapter.ct=this;
		// locationinfo = new LocationInfo(this);
		Intent intent=getIntent();
		bisLogin = (boolean)intent.getBooleanExtra("IsLogin",false);
		initPushManger(bisLogin);

		// Set up the action bar.
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		
		//make swiping tab possible
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						Log.i(TAG, "Listener position selected:"+position);
						if(position == TabIdx.MY_TRENDS.getValue() && PushReceiver.PCCInfo.IsChanged()){
							// if push recived, and switch to myTrends,refresh myTrends to make ui notification of RCV OR PUB works 
							mSectionsPagerAdapter.notifyDataSetChanged();

						}
						// only set tab as selected
						actionBar.setSelectedNavigationItem(position);
						//call getItemPosition which return POSITION_NONE make fragment refresh itself
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
			
		}
		
		//initial mytrends UI
		if(bisLogin == true && PushReceiver.PCCInfo.IsChanged()){
		this.setTabIcon(10);//input tab index impossible means not update UI in MyTreands in home activity
		}
  	  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
   //     getMenuInflater().inflate(R.menu.homemain, menu );

		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	        	Log.e(TAG,"click settting in action bar in homeActivity");
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		Log.i(TAG, "onTabSelected(tabid):"+tab.getPosition());

		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {

	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
//	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Log.i(TAG, "go to item(tab):"+position);
	        Fragment fragment = null;
	        if (position == TabIdx.FIND.getValue()) {
	            fragment = new FindJob();
	        }
	        if (position == TabIdx.MY_TRENDS.getValue()) {
	            fragment = new MyTrends();
	        }
	        if (position == TabIdx.NEW.getValue()) {
	            fragment = new NewBB();
	        }
	        if (position == TabIdx.ABOUT_ME.getValue()) {
	            fragment = new AboutMe();
	        }
	        return fragment;
	        /*
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;*/
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			if(bisLogin == true)
			return 4;
			else
			return 1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.find).toUpperCase(l);
			case 1:
				return getString(R.string.activity).toUpperCase(l);
			case 2:
				return getString(R.string.create).toUpperCase(l);
			case 3:
				return getString(R.string.me).toUpperCase(l);
			}
			return null;
		}
		@Override
		public int getItemPosition(Object object) {
			// suppose fragment A, B, C are one next one,after mSectionsPagerAdapter.notifyDataSetChanged();
			// switch from A to B, getItemPosition() will be called separately by parameter object is  A 、B、C
			// switch to fragment B, the fragment A and C which besides B can be controled refresh or not here, by:
			// if return none, refresh fragment by input parameter object  
			// if return getItemPosition, do not refresh current fragment(object)
			// when we say refresh, we mean call onDestroyView/onCreateView
			
			// here we can only force refresh B when switch from A to B,
			// but when A to B, cannot avoid create C here 
			// also can not avoid when A works as current fragment,B will be create
			Log.i(TAG, "getItemPosition:"+object);

			if(object instanceof MyTrends 
					&& TabIdx.MY_TRENDS.getValue() == actionBar.getSelectedTab().getPosition()){
				return POSITION_NONE;// only refresh myTrends
			}else
			    return super.getItemPosition(object);
			/*
			else if(object instanceof FindJob 
					&& TabIdx.FIND.getValue() == actionBar.getSelectedTab().getPosition()){
				Log.i(TAG, "getItemPosition:"+object);
				return super.getItemPosition(object);// do not  refresh findjob
			}
			else if(object instanceof NewBB 
					&& TabIdx.NEW.getValue() == actionBar.getSelectedTab().getPosition()){
				Log.i(TAG, "getItemPosition:"+object);
				return POSITION_NONE;//  refresh NewBB
			}
			else if(object instanceof AboutMe 
					&& TabIdx.ABOUT_ME.getValue() == actionBar.getSelectedTab().getPosition()){
				Log.i(TAG, "getItemPosition:"+object);
				return POSITION_NONE;//  refresh AboutMe
			}*/

		}
	}
	/*
	public  void toastShow(String toastString)
	{
		if(gToast == null)
		{
			gToast  = Toast.makeText(this,toastString,Toast.LENGTH_LONG);
		}else
		{
			gToast.setText(toastString);
		}
		gToast.show();
	}*/
	@Override
    public void onBackPressed() {
		backPressedCount++;
		if(1 == backPressedCount)
		{ 
			bu.toastShow(this,"再按一次 退出程序");
		}else if(backPressedCount > 1)
		{
			finish();
		}
		new Handler().postDelayed(new Runnable(){   
		    public void run() {   
		    	backPressedCount = 0;
		    }   
		 }, 2500);
		
	}
	public void initPushManger(boolean bisLogin)
	{
		if(false == pushManagerInited && true == bisLogin){ 
			Log.i(TAG, "init getuiPush");
			PushManager.getInstance().initialize(this.getApplicationContext());
			pushManagerInited = true;
		}
	}
	public void setTabIcon(int tabId)
	{
		Log.e(TAG, "setTabIcon:"+tabId);

		if(actionBar == null || bisLogin == false){
			Log.e(TAG, "action bar not initialed");
			return;
		}
		//set ui notify on trends
		actionBar.getTabAt(TabIdx.MY_TRENDS.getValue()).setIcon(R.drawable.ui_notification);

		Log.i(TAG, "POSTION:"+tabId+actionBar.getSelectedTab().getPosition());
		if(tabId == actionBar.getSelectedTab().getPosition()){
			//set ui notification in publish or pick in MyTrends, if current fragment is MyTrends 

			Log.i(TAG, "POSTION-1:"+tabId+actionBar.getSelectedTab().getPosition());
			mOnRefreshListener.onRefresh();
		}
	}
	public void clearTabIcon(int tabId)
	{
		Log.e(TAG, "clearTabIcon:"+tabId);

		if(actionBar == null){
			Log.e(TAG, "action bar not initialed");
			return;
		}
		actionBar.getTabAt(tabId).setIcon(null);
	}
    /**
     * Register a callback to be invoked when this list should be refreshed.
     * 
     * @param onRefreshListener The callback to run.
     */
	public static OnRefreshListener mOnRefreshListener = null;
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.mOnRefreshListener = onRefreshListener;
    }
    /**
     * Interface definition for a callback to be invoked when list should be
     * refreshed.
     */
    public interface OnRefreshListener {
        /**
         * Called when the list should be refreshed.
         * <p>
         * A call to {@link PullToRefreshListView #onRefreshComplete()} is
         * expected to indicate that the refresh has completed.
         */
        public void onRefresh();
    }



}
