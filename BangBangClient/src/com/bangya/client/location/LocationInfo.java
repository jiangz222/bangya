package com.bangya.client.location;

import java.util.Timer;
import java.util.TimerTask;

import com.bangya.client.BBUI.BaseUtil;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


public class LocationInfo {
	private Timer timer1;
	private LocationResult locationResult;
	private LocationManager lm;
	private boolean gps_enabled=false,network_enabled=false;
	private String TAG="LOC";
	BaseUtil bu = new BaseUtil();
	private Context ct;
	SimpleLocationInfo slocation = new SimpleLocationInfo();
	public boolean getLocation(Context context, LocationResult result)
	{
	        //I use LocationResult callback class to pass location value from MyLocation to user code.
	        locationResult=result;
	        ct = context;
	        if(lm==null)
	            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

	        //exceptions will be thrown if provider is not permitted.
	        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
	        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

	        //don't start listeners if no provider is enabled
	        if(!gps_enabled && !network_enabled){
	            // use default location? 
	        	return false;
	        }
	        Log.e(TAG, "set listener");
	        
	       lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
	       lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
	        
	        timer1=new Timer();
	        timer1.schedule(new GetLastLocation(), 8000);// 8s
	        return true;
	}
	LocationListener locationListenerGps = new LocationListener() {
	        public void onLocationChanged(Location location) {
		        Log.i(TAG, "GPS listener, location changed");
	            timer1.cancel();
	            slocation.setLatitude(location.getLatitude());
	            slocation.setLongitude(location.getLongitude());
		        bu.setLocationToPreference(ct, slocation);
	            locationResult.gotLocation(slocation);
	            lm.removeUpdates(this);
	            lm.removeUpdates(locationListenerNetwork);
	        }
	        public void onProviderDisabled(String provider) {}
	        public void onProviderEnabled(String provider) {
	        	Log.e(TAG, "on GPS provider enable");
	          Location gps_loc = lm.getLastKnownLocation(provider);
	          if(gps_loc != null){
		        	Log.i(TAG, "on GPS provider enable-1");

	            timer1.cancel();
	            slocation.setLatitude(gps_loc.getLatitude());
	            slocation.setLongitude(gps_loc.getLongitude());
		        bu.setLocationToPreference(ct, slocation);
	            locationResult.gotLocation(slocation);
	            lm.removeUpdates(this);
	            lm.removeUpdates(locationListenerNetwork);
	          }
	        }
	        public void onStatusChanged(String provider, int status, Bundle extras) {}

	    };
	    LocationListener locationListenerNetwork = new LocationListener() {
	        public void onLocationChanged(Location location) {
		        Log.i(TAG, "NetWork listener, location changed");
	            timer1.cancel();
	            slocation.setLatitude(location.getLatitude());
	            slocation.setLongitude(location.getLongitude());
		        bu.setLocationToPreference(ct, slocation);
	            locationResult.gotLocation(slocation);
	            lm.removeUpdates(this);
	            lm.removeUpdates(locationListenerGps);
	        }
	        public void onProviderDisabled(String provider) {}
	        public void onProviderEnabled(String provider) {
	        	Log.e(TAG, "on network provider enable");
		          Location network_loc = lm.getLastKnownLocation(provider);
		          if(network_loc != null){
		            timer1.cancel();
		        	Log.i(TAG, "on network provider enable-1");

		            slocation.setLatitude(network_loc.getLatitude());
		            slocation.setLongitude(network_loc.getLongitude());
			        bu.setLocationToPreference(ct, slocation);
		            locationResult.gotLocation(slocation);
		            lm.removeUpdates(this);
		            lm.removeUpdates(locationListenerNetwork);
		            }
	        }
	        public void onStatusChanged(String provider, int status, Bundle extras) {}
	    };
	    class GetLastLocation extends TimerTask {
	        @Override
	        public void run() {
		        Log.i(TAG, "listener time out");

	             lm.removeUpdates(locationListenerGps);
	             lm.removeUpdates(locationListenerNetwork);

	             Location net_loc=null, gps_loc=null;
	             if(gps_enabled)
	                 gps_loc=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	             if(network_enabled)
	                 net_loc=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	             //if there are both values use the latest one
	             if(gps_loc!=null && net_loc!=null){
	                 if(gps_loc.getTime()>net_loc.getTime()){
					        Log.i(TAG, "listener time out,return gps last location-1");
				            slocation.setLatitude(gps_loc.getLatitude());
				            slocation.setLongitude(gps_loc.getLongitude());
					        bu.setLocationToPreference(ct, slocation);
		                  locationResult.gotLocation(slocation); 	 
	                 }
	                 else{
					        Log.i(TAG, "listener time out,return net last location-1");
				            slocation.setLatitude(net_loc.getLatitude());
				            slocation.setLongitude(net_loc.getLongitude());
					        bu.setLocationToPreference(ct, slocation);
					        locationResult.gotLocation(slocation);
	                 }
	                 return;
	             }

	             if(gps_loc!=null){
				        Log.i(TAG, "listener time out,return gps last location");
			            slocation.setLatitude(gps_loc.getLatitude());
			            slocation.setLongitude(gps_loc.getLongitude());
				        bu.setLocationToPreference(ct, slocation);
	                 locationResult.gotLocation(slocation);
	                 return;
	             }
	             if(net_loc!=null){
				        Log.i(TAG, "listener time out,return net last location");
			            slocation.setLatitude(net_loc.getLatitude());
			            slocation.setLongitude(net_loc.getLongitude());
				        bu.setLocationToPreference(ct, slocation);
	                 locationResult.gotLocation(slocation);
	                 return;
	             }
			    Log.e(TAG, "listener time out,return location null");
			    slocation = bu.getLocationFromPreference(ct);
	           locationResult.gotLocation(slocation);
	        }
	    }

	    public void cancelListener()
	    {
	    	if(lm != null){
	    		if(locationListenerGps != null)
	            lm.removeUpdates(locationListenerGps);
	    		if(locationListenerNetwork != null)
	            lm.removeUpdates(locationListenerNetwork);
	    		if(timer1 !=null)
	            timer1.cancel();

	    	}
	    }

}
