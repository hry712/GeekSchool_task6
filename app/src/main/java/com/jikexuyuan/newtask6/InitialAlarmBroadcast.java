package com.jikexuyuan.newtask6;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by sparrow on 16-10-7.
 */

public class InitialAlarmBroadcast implements Runnable {
    private List<Map<String, Object>> todo_items;
    private AlarmManager alarmManager;
    private PendingIntent sender;
    private Intent intent;
    private Context mContext;
    private int hour;
    private String things;
    private Object temp;
    private Calendar calendar;

    public InitialAlarmBroadcast(Context context, final List<Map<String, Object>> ToDoItems, AlarmManager AM) {
        todo_items = ToDoItems;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mContext = context;
        System.out.println("We have entered the thread!");
    }

    @Override
    public void run() {
        // 遍历读取列表中所获得的时间和事项，并依次发送出去
        for (Map<String, Object> item:
             todo_items) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            // 获得时刻
            temp = item.get("tvHour");
            if (temp instanceof String) {
                try {
                    hour = Integer.parseInt((String)temp);
                } catch (ClassCastException e) {
                    throw new ClassCastException("Fail to parse Hour to Int data!");
                }
//                System.out.println("the hour is: "+hour);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            } else {
//                System.out.print("Hour Type Error");
            }

            // 获得待办事项
            temp = item.get("tvThings");
            if (temp instanceof String) {
                things = (String) temp;
//                System.out.println("the things are: " + things);
            } else {
//                System.out.print("Things Type Error");
            }

            intent = new Intent(mContext, AlarmReceiver.class);
            intent.setAction(AlarmReceiver.SEND_ACTION);
            intent.putExtra("things", things);

            sender = PendingIntent.getBroadcast(mContext, hour, intent, PendingIntent.FLAG_NO_CREATE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            // 测试：立即将事件发送出去，观察能否在 Receiver 那端收到消息
//            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), sender);
//            temp = null;
//            intent = null;
        }
        System.out.println("The thread has finished!");
    }
}
