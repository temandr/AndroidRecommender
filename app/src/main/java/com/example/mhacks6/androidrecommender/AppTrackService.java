package com.example.mhacks6.androidrecommender;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

/**
 * Created by temandr on 9/12/15.
 */
public class AppTrackService extends Service
{
    private ExecutorService executorService;
    private ActivityManager am;
    private String curPackage;
    private Location curLocation;
    private Set<String> ignore_packages;
    private int curTime;
    private int curWeek;

    private MobileServiceClient mClient;

    @Override
    public IBinder onBind(Intent intent) { return null;}

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("Banana", "AppTrackService - onCreate()");
        curPackage = "";

        executorService = Executors.newSingleThreadExecutor();
        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        TrackThread tt = new TrackThread();

        ignore_packages = new HashSet<String>();
        populateIgnorePackages();

        try {
            mClient = new MobileServiceClient(
                    "https://lockscreenmhacks6.azure-mobile.net/",
                    "qzAjMcWnZoDxcEaEILrykvIbLQPywO14",
                    this
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        executorService.submit(tt);


    }

    private void populateIgnorePackages()
    {
        ignore_packages.add("com.android.launcher");
    }

    public class TrackThread implements Runnable
    {

        @Override
        public void run()
        {
            Item i;
            while(true)
            {
                String packageName = am.getRunningTasks(1).get(0).topActivity.getPackageName();
                if(!curPackage.equals(packageName) && !ignore_packages.contains(packageName)) {
                    //New Package
                    curPackage = packageName;
                    curLocation = getLocationFunc();
                    curTime = getTime();
                    curWeek = getWeek();
                    i = new Item(curPackage, curLocation.toString(), curTime, curWeek);

                    mClient.getTable(Item.class).insert(i, new TableOperationCallback<Item>() {
                        @Override
                        public void onCompleted(Item entity, Exception exception, ServiceFilterResponse response) {
                            if(exception == null)
                            {
                                Log.i("Banana", "Sweet");
                            } else
                            {
                                Log.i("Banana", "crap.");

                            }
                        }
                    });
                    Log.i("Banana", "AppTrackService - run() - " + packageName + "::" + curLocation + "::" + curTime);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }

    public Location getLocationFunc()
    {
        Location loc = null;
        LocationClass l = new LocationClass(this);

        Log.i("Banana", "getLocationFunc");
        loc = l.getLocation();

        return loc;
    }

    public int getTime()
    {
        Calendar c = new GregorianCalendar(TimeZone.getDefault());
        c.setTime(new Date());

        //dayofweek:hour:AM/PM
        int hour = c.get(Calendar.AM_PM) * 12 +  c.get(Calendar.HOUR) -1;
        return hour;
    }

    public int getWeek()
    {
        Calendar c = new GregorianCalendar(TimeZone.getDefault());
        c.setTime(new Date());
        return c.get(Calendar.DAY_OF_WEEK);
    }


    public class LocationClass implements LocationListener
    {
        private LocationManager locationManager;
        private Context context;

        private boolean gpsOn;
        private boolean networkOn;

        private boolean isGPSOn() {
            gpsOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.i("Banana", "isGPSON - " + gpsOn);
            return gpsOn;
        }
        private boolean isNetOn() {
            Log.i("Banana", "isNetON - " + networkOn);
            networkOn = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            return networkOn;
        }

        public LocationClass(Context context)
        {
            this.context = context;
        }

        public Location getLocation()
        {
            Location location = null;
            try {
                location = getLastKnownLocation();

            } catch (Exception e)
            {
                e.printStackTrace();
            }

            return location;
        }

        private Location getLastKnownLocation() {
            locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
            List<String> providers = locationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                try {
                    Location l = locationManager.getLastKnownLocation(provider);
                    if (l == null) {
                        continue;
                    }
                    if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                        // Found best last known location: %s", l);
                        bestLocation = l;
                    }
                } catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            return bestLocation;
        }

        @Override
        public void onLocationChanged(Location location) {
            //Don't care
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Don't care

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    }


}

