package com.tgf.exhibition;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tgf.exhibition.util.ValueHodler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by jeff on 2016/5/19.
 */
public class TgfApplication extends Application {
    private static final ExecutorService sPool = Executors.newFixedThreadPool(10);
    private static final ValueHodler sGlobalValues = new ValueHodler();

    @Override
    public void onCreate() {
        super.onCreate();

        boolean isUIProcess = true;
        int pid = android.os.Process.myPid ();
        ActivityManager mActivityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses())  {
            if(appProcess.pid == pid && appProcess.processName.contains(":updater")){
                isUIProcess = false;
            }
        }

        JPushInterface.setDebugMode(false); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);

        UIProcessInitialize(isUIProcess);
        launcherUpdater();
    }

    private void UIProcessInitialize(boolean isUIProcess) {
        if(isUIProcess) {
            //File cacheDir = StorageUtils.getCacheDirectory(this); // default
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .taskExecutor(sPool)
                    .taskExecutorForCachedImages(sPool)
                    .threadPoolSize(5) // default
                    .diskCacheSize(50 * 1024 * 1024)
                    .diskCacheFileCount(100)
//                    .writeDebugLogs()
                    .build();
            ImageLoader.getInstance().init(config);
        }
    }

    private void launcherUpdater() {
        Intent intent = new Intent(this, TgfUpdateService.class);
        intent.putExtra(PackageUpdater.KEY_START_WHAT, PackageUpdater.START_WHAT_VERSION_CHECK);
        intent.putExtra(PackageUpdater.KEY_PROMPT_TYPE,PackageUpdater.PROMPT_TYPE_NOTIFICATION);
        intent.putExtra(PackageUpdater.KEY_SHOW_LOADING, false);
        startService(intent);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        System.gc();
        super.onLowMemory();
    }

    public static void runInBackground(Runnable runnable) {
        sPool.execute(runnable);
    }

    public static void removeValue(String key) {
        sGlobalValues.remove(key);
    }

    public static <T> void putValue(String key, T value) {
        sGlobalValues.putValue(key, value);
    }

    public static <T> T getValue(String key) {
        return sGlobalValues.getValue(key);
    }
}
