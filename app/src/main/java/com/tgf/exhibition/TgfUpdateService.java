package com.tgf.exhibition;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.tgf.exhibition.http.handler.FileDownloader;
import com.tgf.exhibition.http.handler.JsonResponseHandler;
import com.tgf.exhibition.http.handler.PackageVersionRequestHandler;
import com.tgf.exhibition.http.json.PackageInfo;
import com.tgf.exhibition.http.msg.PackageInfoMassage;
import com.tgf.exhibition.util.OSChecker;
import com.tgf.exhibition.widget.LoadingDialog;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TgfUpdateService extends Service {
    private PackageUpdater mPackageUpdater;

    public TgfUpdateService() {
        super();
        mPackageUpdater = new PackageUpdater(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Servic 请求处理：1-请求版本检查，完成检查后的交互类型可以是Notifycation和Dialog
     * 2-请求Download，无交互，请求即开始下载
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || !OSChecker.isNetworkAvailable(this)) {
            return super.onStartCommand(intent, Service.START_REDELIVER_INTENT, startId);
        }

        String startWhat = intent.getStringExtra(PackageUpdater.KEY_START_WHAT);
        if (PackageUpdater.START_WHAT_DOWNLOAD.equals(startWhat)) {
            // TODO： 开始 Download
            mPackageUpdater.requestPackageDownload();
        } else if (PackageUpdater.START_WHAT_VERSION_CHECK.equals(startWhat)) {
            // TODO： 版本检查
            String promptType = intent.getStringExtra(PackageUpdater.KEY_PROMPT_TYPE);
            boolean showLoadingDialog = intent.getBooleanExtra(PackageUpdater.KEY_SHOW_LOADING, false);
            mPackageUpdater.requestPackageVersionInfo(promptType, showLoadingDialog);
        }
        return START_STICKY;
        //return super.onStartCommand(intent, Service.START_REDELIVER_INTENT, startId);
    }

    private static final Handler TIMER_HANDLER = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            PackageUpdater packageUpdater = (PackageUpdater) msg.obj;

            packageUpdater.requestPackageVersionInfo(null, false);

            sendMessageDelayed(Message.obtain(msg), packageUpdater.getNextDelayMillis());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Message message = Message.obtain(TIMER_HANDLER, 0, mPackageUpdater);
        TIMER_HANDLER.sendMessage(message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("test", "-----> onDestroy");
    }
}

/**
 * 执行版本检查以及包更行
 */
final class PackageUpdater {
    private static final int NOTIFY_ID = 1000;

    public static final String KEY_START_WHAT = "start_what";
    public static final String START_WHAT_DOWNLOAD = "start_download";
    public static final String START_WHAT_VERSION_CHECK = "start_version_check";

    public static final String KEY_PROMPT_TYPE = "prompt_type";
    public static final String PROMPT_TYPE_NOTIFICATION = "notification";
    public static final String PROMPT_TYPE_DIALOG = "dialog";

    /**
     * Used by TgfActivity for intercative with User. is Boolean Value
     */
    public static final String KEY_SHOW_LOADING = "show_loading";
    /**
     * SharedPreference KEY with PackageInfo Json-data
     */
    public static final String KEY_NEW_PACKAGE_INFO = "new_package_info";

    private final PackageVersionRequestHandler mPackageVersionRequestHandler;
    private Service mContext;

    private Set<RequestRecord> mRequesetRecords = new HashSet<RequestRecord>(5);

    private static class RequestRecord {
        public final long mRequestTime;
        public final String mPromptType;
        public final boolean mShowLoadingDialog;

        public RequestRecord(String promptType, boolean showLoadingDialog) {
            mPromptType = promptType;
            mShowLoadingDialog = showLoadingDialog;
            mRequestTime = System.currentTimeMillis();
        }

        public static RequestRecord create(String promptType, boolean showLoadingDialog) {
            return new RequestRecord(promptType, showLoadingDialog);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof RequestRecord)) {
                return false;
            }
            RequestRecord other = (RequestRecord) o;
            return mPromptType.equals(other.mPromptType) && mShowLoadingDialog == other.mShowLoadingDialog;
        }

        public boolean elapsedLongTime(RequestRecord requestRecord) {
            return (requestRecord.mRequestTime - mRequestTime) > DateUtils.HOUR_IN_MILLIS;
        }
    }

    PackageUpdater(Service context) {
        mContext = context;
        mPackageVersionRequestHandler = new PackageVersionRequestHandler(context);
    }

    /**
     * 获得当前版本号
     */
    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {

        }
        return verCode;
    }

    /**
     * 获得当前版本名称
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return verName;
    }

    /**
     * 从服务器端获得版本号与版本名称
     *
     * @return
     */
    public synchronized void requestPackageVersionInfo(String promptType, boolean showLoadingDialog) {
        if (!showLoadingDialog) {
            // 非用户行为记录
            RequestRecord requestRecord = RequestRecord.create(promptType, showLoadingDialog);
            if (isInvalideRequest(requestRecord)) {
                return;
            }
        }
        mPackageVersionRequestHandler
                .setPackageName(mContext.getPackageName())
                .doHttpRequest(new PackageVersionResponseHandler(promptType, showLoadingDialog));
    }

    private boolean isInvalideRequest(RequestRecord requestRecord) {
        if (mRequesetRecords.contains(requestRecord)) {
            Iterator<RequestRecord> iterator = mRequesetRecords.iterator();
            RequestRecord preRequest;
            while (iterator.hasNext()) {
                preRequest = iterator.next();
                if (preRequest.equals(requestRecord) && preRequest.elapsedLongTime(requestRecord)) {
                    iterator.remove();
                    mRequesetRecords.add(requestRecord);
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * CallBack方法，当PackageInfo请求返回时，被PackageVersionResponseHandler调用
     *
     * @param packageInfo
     */
    private void onPackageInfoRetreived(PackageInfo packageInfo, String promptType) {
        if(packageInfo == null||TextUtils.isEmpty(packageInfo.downloadUrl)) {
            if (PROMPT_TYPE_DIALOG.equals(promptType)) {
                showNoNewVersionUpdateDialog();
            }
            return;
        }
        int newVersionCode = Integer.parseInt(packageInfo.versionCode);
        int oldVersionCode = getVerCode(mContext);
        if (newVersionCode > oldVersionCode) {
            savePackageInfo(packageInfo);

            if (!TextUtils.isEmpty(promptType)) {
                if (PROMPT_TYPE_NOTIFICATION.equals(promptType)) {
                    postNotification(getVerName(mContext), packageInfo.versionName);
                } else if (PROMPT_TYPE_DIALOG.equals(promptType)) {
                    showNewVersionUpdateDialog(getVerName(mContext), packageInfo.versionName);
                }
            }
        } else if (PROMPT_TYPE_DIALOG.equals(promptType)) {
            showNoNewVersionUpdateDialog();
        }
    }

    private void savePackageInfo(PackageInfo packageInfo) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            preferences.edit().putString(KEY_NEW_PACKAGE_INFO, objectMapper.writeValueAsString(packageInfo)).apply();
        } catch (JsonProcessingException e) {
            Log.e("TgfUpdate", "Json data parse failed", e);
        }
    }

    /**
     * 创建通知栏更新提示
     */
    private void postNotification(String oldVersionName, String newVersionName) {
        Intent intent = new Intent(mContext, TgfUpdateService.class);
        intent.putExtra(KEY_START_WHAT, START_WHAT_DOWNLOAD);
        PendingIntent contentIntent = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        RemoteViews contentView = new RemoteViews(mContext.getPackageName(), R.layout.notification_new_version);
        contentView.setTextViewText(R.id.tv_old_version, mContext.getString(R.string.update_old_version, oldVersionName));
        contentView.setTextViewText(R.id.tv_new_version, mContext.getString(R.string.update_new_version, newVersionName));

        Notification.Builder builder = new Notification.Builder(mContext);
        builder.setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_scene_logo)
                .setContent(contentView)
                .setTicker(mContext.getString(R.string.update_has_new_version))
                .setContentIntent(contentIntent);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationManager.notify(NOTIFY_ID, builder.build());
        } else {
            notificationManager.notify(NOTIFY_ID, builder.getNotification());
        }
    }

    /**
     * 不更新版本
     */
    public void showNoNewVersionUpdateDialog() {
        String verName = getVerName(mContext);
        int verCode = getVerCode(mContext);
        StringBuffer sb = new StringBuffer()
                .append("当前版本：").append(verName)
                .append("\n已是最新版本");
        AlertDialog dialog = new AlertDialog.Builder(mContext, R.style.SystemAlertDialog)
                .setTitle("软件更新")
                .setMessage(sb.toString())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.getWindow().setBackgroundDrawableResource(R.color.panelBackgroud);
        dialog.show();
    }

    private void sendDownloadMsg() {
        Intent intent = new Intent(mContext, TgfUpdateService.class);
        intent.putExtra(KEY_START_WHAT, START_WHAT_DOWNLOAD);
        mContext.startService(intent);
    }

    /**
     * 更新版本
     */
    public void showNewVersionUpdateDialog(String oldVerName, String newVerName) {
        StringBuffer sb = new StringBuffer()
                .append("当前版本：").append(oldVerName)
                .append("\n发现版本：").append(newVerName)
                .append("\n是否更新?");
        AlertDialog alertDialog =  new AlertDialog.Builder(mContext, R.style.SystemAlertDialog)
                .setCancelable(false)
                .setTitle("软件更新")
                .setMessage(sb.toString())
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendDownloadMsg();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.panelBackgroud);
        alertDialog.show();
    }

    /**
     * 下载apk
     */
    public void requestPackageDownload() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String jsonPackageInfo = preferences.getString(KEY_NEW_PACKAGE_INFO, null);
        if (TextUtils.isEmpty(jsonPackageInfo)) {
            return;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            final PackageInfo packageInfo = objectMapper.readValue(jsonPackageInfo, PackageInfo.class);
            if (getVerName(mContext).equals(packageInfo.versionName) && (getVerCode(mContext) >= Integer.parseInt(packageInfo.versionCode))) {
                preferences.edit().remove(KEY_NEW_PACKAGE_INFO).apply();
                requestPackageVersionInfo(PROMPT_TYPE_NOTIFICATION, false);
                return;
            }

//            if (!TextUtils.isEmpty(packageInfo.localePath)) {
//                File apkFile = new File(packageInfo.localePath);
//                if (apkFile.exists()) {
//                    installPackage(apkFile);
//                }
//            }

            final FileDownloader fileDownloader = new FileDownloader(mContext, packageInfo.downloadUrl, createTempFile());
            fileDownloader.doHttpRequest(new FileDownloader.FileDownloadListener() {
                private NotificationManager mNotificationManager = null;
                private boolean mDownloadSuccess = false;
                private File mDownloadedApk = null;

                @Override
                public void onDownloadFinished(File apkFile) {
                    mDownloadSuccess = true;
                    mDownloadedApk = apkFile;
                    installPackage(apkFile);
                    packageInfo.localePath = apkFile.getAbsolutePath();
                    savePackageInfo(packageInfo);
                }

                @Override
                public void onDownloadFailure(File storedFile) {
                    if (storedFile != null && storedFile.exists()) {
                        storedFile.delete();
                    }
                }

                @Override
                public void onDownloadStart() {
                    getNotificationManager().cancel(NOTIFY_ID);
                    NotificationCompat.Builder builder = createDownloadProgressNotifictionBuilder()
                            .setContentTitle("开始下载...")
                            .setOngoing(true)
                            .setProgress(100, 0, false);
                    postNotification(builder);
                }

                @Override
                public void onDownloadProgress(long bytesWritten, long totalSize) {
                    int maxProgress = 100;
                    int progress = (totalSize > 0) ? (int)((bytesWritten * 1.0 / totalSize) * maxProgress) : 0;
                    NotificationCompat.Builder builder = createDownloadProgressNotifictionBuilder()
                            .setContentTitle("正在下载...")
                            .setOngoing(true)
                            .setProgress(maxProgress, progress, false);
                    postNotification(builder);
                }

                @Override
                public void onDownloadFinish() {
                    NotificationCompat.Builder builder = createDownloadProgressNotifictionBuilder()
                            .setOngoing(false)
                            .setAutoCancel(true)
                            .setProgress(0, 0, true);
                    if(mDownloadSuccess) {
                        PendingIntent pi = PendingIntent.getActivity(mContext, 0, getInstallPacakgeIntent(mDownloadedApk), PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentText("下载完成")
                                .setDefaults(Notification.DEFAULT_SOUND)
                                .setContentIntent(pi);
                    } else {
                        builder.setContentText("下载失败...");
                    }
                    postNotification(builder);
                }

                private void postNotification(NotificationCompat.Builder builder) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        getNotificationManager().notify(NOTIFY_ID, builder.build());
                    } else {
                        getNotificationManager().notify(NOTIFY_ID, builder.getNotification());
                    }
                }

                private NotificationManager getNotificationManager() {
                    if(mNotificationManager == null) {
                        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    }
                    return mNotificationManager;
                }

                private NotificationCompat.Builder createDownloadProgressNotifictionBuilder() {
                    return new NotificationCompat.Builder(mContext)
                            .setSmallIcon(R.mipmap.ic_launcher);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private File createTempFile() throws IOException {
        return File.createTempFile("tgf-", new Date().toString() + ".apk", mContext.getExternalCacheDir());
    }

    /**
     * 安装应用
     */
    private void installPackage(File apkFile) {
        if (apkFile != null && apkFile.exists()) {
            mContext.startActivity(getInstallPacakgeIntent(apkFile));
        }
    }

    private Intent getInstallPacakgeIntent(File apkFile) {
        return new Intent(Intent.ACTION_VIEW)
                        .setDataAndType(Uri.fromFile(apkFile),
                                "application/vnd.android.package-archive")
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * 动态计算检查任务延迟时间
     *
     * @return
     */
    public long getNextDelayMillis() {
        return DateUtils.DAY_IN_MILLIS;
    }

    private class PackageVersionResponseHandler extends JsonResponseHandler<PackageInfoMassage> {
        private final String mPromptType;
        private final boolean mShowLoadingDialog;

        public PackageVersionResponseHandler(String promptType, boolean showLoadingDialog) {
            super(PackageInfoMassage.class);
            mPromptType = promptType;
            mShowLoadingDialog = showLoadingDialog;
        }

        @Override
        public void onStart() {
            if (mShowLoadingDialog) {
                LoadingDialog loadingDialog = new LoadingDialog(mContext);
                loadingDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                setLoadingDialog(loadingDialog);
            }
            super.onStart();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (mShowLoadingDialog) {
                dismissLoadingDialog();
            }
        }

        @Override
        public void onSuccess(PackageInfoMassage jsonObj, String rawJsonResponse) {
            if (jsonObj.statusCode == 0) {
                onPackageInfoRetreived(jsonObj.data, mPromptType);
            } else {
                Log.w("Update", jsonObj.statusMessage);
            }
        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {
        }
    }
}
