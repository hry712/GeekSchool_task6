package com.jikexuyuan.newtask6;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by sparrow on 16-10-7.
 */

public class AlarmReceiver extends BroadcastReceiver {
    public final static String SEND_ACTION = "com.jikexuyuan.newtask6.action.ALARM";
    public final static String DELETE_ACTION = "delete";

    private NotificationManager notificationManager;
    private Notification notification;
    private NotificationCompat.Builder builder;
    private PendingIntent pi;


    public void onReceive(Context context, Intent intent) {
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(context);


//        Toast.makeText(context, "Receive Mess: ", Toast.LENGTH_SHORT).show();
        // 如果是将待办事项发送通知出去
        if (intent.getAction().equals(SEND_ACTION)) {
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle("To Do")
                    .setContentText(intent.getStringExtra("things"));
            notification = builder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(R.id.tv_hour, notification);
//            Toast.makeText(context, "Noti Sent out!", Toast.LENGTH_SHORT).show();
        }

        // 由于 AM 不在 Receiver 中，删除待办事项的工作应该放在 MainAty 中

    }
}
