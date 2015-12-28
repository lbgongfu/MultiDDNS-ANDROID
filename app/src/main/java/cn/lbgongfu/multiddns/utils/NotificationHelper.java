package cn.lbgongfu.multiddns.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import cn.lbgongfu.multiddns.R;

/**
 * Created by gf on 2015/12/26.
 */
public class NotificationHelper {
    private static final int NOTIFY_ID_IP_CHANGED = 0;

    public static void showIPChanged(Context context, String newIP, PendingIntent intent)
    {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.msg_current_ip) + ": " + newIP)
                .setContentIntent(intent)
                .setTicker(context.getString(R.string.msg_ip_changed) + ": " + newIP)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        manager.notify(NOTIFY_ID_IP_CHANGED, notification);
    }
}
