package com.example.mhacks6.androidrecommender;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by temandr on 9/12/15.
 */
public class AppTrackService extends Service
{
    private ExecutorService executorService;
    private ActivityManager am;
    private String curPackage;
    private Set<String> ignore_packages;

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
            while(true) {

                String packageName = am.getRunningTasks(1).get(0).topActivity.getPackageName();
                if(!curPackage.equals(packageName) && !ignore_packages.contains(packageName)) {
                    //New Package
                    curPackage = packageName;
                    Log.i("Banana", "AppTrackService - run() - " + packageName);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }

    }
}
