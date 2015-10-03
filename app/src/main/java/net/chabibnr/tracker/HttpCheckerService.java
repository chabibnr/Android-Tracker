package net.chabibnr.tracker;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

/**
 * Created by admin on 19/08/2015.
 */
public class HttpCheckerService extends Service {
    boolean IS_RUNNING = false;
    MainModel mainModel;
    private static final String TAG = "Kates";
    public static NotificationManager myNotificationManager;
    private int notificationIdOne = 111;
    private int mInterval = 10000;
    private Handler mHandler;
    private Conf config;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("-------", "SErvice onCreate");
        mainModel = new MainModel(this);
        mHandler = new Handler();
        config = new Conf(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("-------", "SErvice on Start");
        IS_RUNNING = true;

        if (config.autoCheck()) {
            mHandler.removeCallbacks(mStatusChecker);
            mStatusChecker.run();
        }

        mServiceIcon.run();
        if ((intent != null) && (intent.getBooleanExtra("ALARM_RESTART_SERVICE_DIED", false))) {
            Log.d(TAG, "onStartCommand after ALARM_RESTART_SERVICE_DIED");
            if (IS_RUNNING) {
                Log.d(TAG, "Service already running - return immediately...");
                ensureServiceStaysRunning();
                return START_STICKY;
            }
        }
        return START_STICKY;
    }

    private void ensureServiceStaysRunning() {
        // KitKat appears to have (in some cases) forgotten how to honor START_STICKY
        // and if the service is killed, it doesn't restart.  On an emulator & AOSP device, it restarts...
        // on my CM device, it does not - WTF?  So, we'll make sure it gets back
        // up and running in a minimum of 20 minutes.  We reset our timer on a handler every
        // 2 minutes...but since the handler runs on uptime vs. the alarm which is on realtime,
        // it is entirely possible that the alarm doesn't get reset.  So - we make it a noop,
        // but this will still count against the app as a wakelock when it triggers.  Oh well,
        // it should never cause a device wakeup.  We're also at SDK 19 preferred, so the alarm
        // mgr set algorithm is better on memory consumption which is good.
        if (Build.VERSION.SDK_INT >= 19) {
            /* A restart intent - this never changes... */
            final int restartAlarmInterval = 20 * 60 * 1000;
            final int resetAlarmTimer = 2 * 60 * 1000;
            final Intent restartIntent = new Intent(this, HttpCheckerService.class);
            restartIntent.putExtra("ALARM_RESTART_SERVICE_DIED", true);
            final AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Handler restartServiceHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // Create a pending intent
                    PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, restartIntent, 0);
                    alarmMgr.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + restartAlarmInterval, pintent);
                    sendEmptyMessageDelayed(0, resetAlarmTimer);
                }
            };
            restartServiceHandler.sendEmptyMessageDelayed(0, 0);
        }
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            if (config.autoCheck()) {
                try {
                    mainModel.getDataFromUrl(Info.INFO_URL_SERVICE);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mInterval = config.autoCheckDelay() * 1000;
                Log.d("------", "interval " + mInterval);
                mHandler.postDelayed(mStatusChecker, mInterval);
            }

        }
    };

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        myNotificationManager.cancel(notificationIdOne);
        mHandler.removeCallbacks(mStatusChecker);
    }

    private boolean isCurrentAutoCheckStatus = false;
    Runnable mServiceIcon = new Runnable() {
        @Override
        public void run() {
            Integer integer = mainModel.numberOfRows(DataDBHelper.COLUMN_UI_STATUS + " IN ('1', '2')");
            if (integer > 0) {
                if (config.notification())
                    notificationCreate();

                mainModel.disableNotificaton();

            }

            if (config.autoCheck()) {
                if (mInterval != (config.autoCheckDelay() * 1000)) {
                    mHandler.removeCallbacks(mStatusChecker);
                    mStatusChecker.run();
                }
            } else {
                isCurrentAutoCheckStatus = false;
            }
            mHandler.postDelayed(mServiceIcon, 2000);
        }
    };

    public void notificationCreate() {

        // Invoking the default notification service
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setContentTitle("Update Tracking");
        //mBuilder.setContentText("");
        mBuilder.setTicker("Terdapat Perubahan pada tracking Anda");
        mBuilder.setSmallIcon(R.drawable.ic_search_white_24dp);

        if (config.notificationLED())
            mBuilder.setLights(Color.BLUE, 500, 500);

        if (config.notificationVibrate()) {
            long[] patternVibrate = {0, 300, 300};
            mBuilder.setVibrate(patternVibrate);
        }
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("notificationId", notificationIdOne);

        //This ensures that navigating backward from the Activity leads out of the app to Home page
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_ONE_SHOT //can only be used once
                );

        mBuilder.setContentIntent(resultPendingIntent);
        myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // pass the Notification object to the system
        myNotificationManager.notify(notificationIdOne, mBuilder.build());

        Info.notificationPlay(getApplicationContext());
    }

}
