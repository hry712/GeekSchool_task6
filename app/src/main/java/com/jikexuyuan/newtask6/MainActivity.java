package com.jikexuyuan.newtask6;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnItemAddedListenerIF, OnItemDeletedListenerIF {

    private List<Map<String, Object>> items_list;
    private SimpleAdapter list_adapter;
    private ToDoListFragment toDoListFragment;
    private NoItemsFragment noItemsFragment;
    private AlarmManager alarmManager;
    private FragmentTransaction fragmentTransaction;
    private SQLiteDatabase todolist_db;
    private ToDoListDBOpenHelper todolist_db_helper;
    private Cursor todolist_db_cursor;
    private FragmentManager fragmentManager;
    private String[] item_columns = new String[]{
            ToDoListDBOpenHelper.KEY_ID,
            ToDoListDBOpenHelper.KEY_TODO_TIME_COLUMN,
            ToDoListDBOpenHelper.KEY_TODO_THINGS_COLUMN
    };
    public static final int ADD_REQUESTCODE = 888;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 读取数据库，获得所有待办事件
        todolist_db_helper = new ToDoListDBOpenHelper(this, null, null, 1);

//        InsertTestItems();

        todolist_db = todolist_db_helper.getReadableDatabase();
        todolist_db_cursor = todolist_db.query(ToDoListDBOpenHelper.DATABASE_TABLE,
                item_columns, null, null, null, null, ToDoListDBOpenHelper.KEY_TODO_TIME_COLUMN);
        items_list = new ArrayList<>();
        GetToDoItems(todolist_db_cursor, items_list);

        // 如果数据库中的事项不为空
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if (!items_list.isEmpty()) {
            // 加载 to do fragment
//            toDoListFragment = (ToDoListFragment) fragmentManager.findFragmentById(R.id.todolistFragment);
            Toast.makeText(this, items_list.get(0).toString(), Toast.LENGTH_SHORT).show();
            toDoListFragment = new ToDoListFragment();
            list_adapter = new SimpleAdapter(MainActivity.this, items_list, R.layout.fragment_todo_item,
                    new String[]{"tvHour", "tvThings"},
                    new int[]{R.id.tv_hour, R.id.tv_things});
            toDoListFragment.setListAdapter(list_adapter);
//
            fragmentTransaction.add(R.id.todolistFragment, toDoListFragment);
            fragmentTransaction.commit();

            // 初始化 AM，准备开启线程
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            // 发送广播
            Runnable myRunnable = new InitialAlarmBroadcast(MainActivity.this, items_list, alarmManager);
            Thread myThread = new Thread(myRunnable);

            // 启动线程，将到点待办事项发送出去
            myThread.start();
        } else {
            // 加载 no items fragment
            noItemsFragment = new NoItemsFragment();
            fragmentTransaction.add(R.id.noitemsFragment, noItemsFragment);
            fragmentTransaction.commit();
        }
    }

    // 从数据库中读取待办事项列表并初始化 item_list 成员变量
    private void GetToDoItems(Cursor ToDoListDBCursor, List<Map<String, Object>> ItemsList) {
        if (ToDoListDBCursor.moveToFirst()) {
            String item_hour;
            String item_things;
            HashMap<String, Object> todo_item;
            do {
                todo_item = new HashMap<>();
                item_hour = ToDoListDBCursor.getString(ToDoListDBCursor.getColumnIndex(ToDoListDBOpenHelper.KEY_TODO_TIME_COLUMN));
                item_things = ToDoListDBCursor.getString(ToDoListDBCursor.getColumnIndex(ToDoListDBOpenHelper.KEY_TODO_THINGS_COLUMN));
                todo_item.put("tvHour", item_hour);
                todo_item.put("tvThings", item_things);
                ItemsList.add(todo_item);
            } while (ToDoListDBCursor.moveToNext());
        }
        ToDoListDBCursor.close();
    }

    // 测试时用的方法，向数据库中插入一条初始数据
    private void InsertTestItems() {
        ContentValues testValues = new ContentValues();
        testValues.put(todolist_db_helper.KEY_TODO_TIME_COLUMN, 10);
        testValues.put(todolist_db_helper.KEY_TODO_THINGS_COLUMN, "Heiheihei");
        SQLiteDatabase DB = todolist_db_helper.getWritableDatabase();
        DB.insert(todolist_db_helper.DATABASE_TABLE, null, testValues);
        DB.close();
    }

    // 给右上角菜单添加选项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_item_menu, menu);
        return true;
    }

    // 给右上角菜单的选项添加点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itm_addItem:
                Intent add_item_intent = new Intent(MainActivity.this, GetItemActivity.class);
                startActivityForResult(add_item_intent, ADD_REQUESTCODE);
                break;
            default:
                Toast.makeText(this, "Unkown Menu Item Selected!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    // 获取到新加的 Item 后，得到结果，并在列表中更新
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_REQUESTCODE &&
                resultCode == Activity.RESULT_OK) {
            int hour = data.getIntExtra("tvHour", 0);
            String things = data.getStringExtra("tvThings");
            // 调用接口方法，更新列表并向数据库插入数据
            onItemAdded(hour, things);
        }
    }

    // 更新列表内容
    private void ItemsListUpdating(Map<String, Object> NewItem) {
        items_list.add(NewItem);
        //TODO:利用时间值进行比较，找到合适的位置，再将待办事项插入到列表中并更新
        Collections.sort(items_list, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> stringObjectMap, Map<String, Object> t1) {
                return (stringObjectMap.get("tvHour")+"").compareTo(t1.get("tvHour")+"");
            }
        });
    }

    // 检查待办事项列表，如果有重复就返回 false
    private boolean CheckReduItemExists(HashMap<String, Object> NewItem) {
        for (Map<String, Object> item : items_list) {
            if (item.get("tvHour").equals(NewItem.get("tvHour"))) {
                Toast.makeText(MainActivity.this,
                        "List hour:" + item.get("tvHour") + "New hour:" + NewItem.get("tvHour"),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onItemAdded(int hour, String things) {
        // 添加一项待办事项后，要对整个列表重新排序并将这个待办事项定点发送出去
        HashMap<String, Object> new_item = new HashMap<>();
        new_item.put("tvHour", hour);
        new_item.put("tvThings", things);
        //TODO:先检查输入的时间值是否已经存在列表中，如果存在则发出告警，不存在才将其插入 list 中
        if (!CheckReduItemExists(new_item)) {
            // 更新列表
            ItemsListUpdating(new_item);
            list_adapter.notifyDataSetChanged();
            // 向数据库中插入数据
            ContentValues cv_item = new ContentValues();
            cv_item.put(ToDoListDBOpenHelper.KEY_TODO_TIME_COLUMN, hour);
            cv_item.put(ToDoListDBOpenHelper.KEY_TODO_THINGS_COLUMN, things);
            todolist_db.insert(ToDoListDBOpenHelper.DATABASE_TABLE, null, cv_item);
        } else {
            Toast.makeText(MainActivity.this,
                    "The input hour has already exist in todo list.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemDeleted(HashMap<String, Object> DeletedItem) {
        //TODO:删除列表中的待办事项
        //TODO:将列表中的事项从计时器中取消掉
        //TODO:从数据库中删除待办事项的记录
        items_list.remove(DeletedItem);
        list_adapter.notifyDataSetChanged();
        todolist_db.delete(ToDoListDBOpenHelper.DATABASE_TABLE,
                ToDoListDBOpenHelper.KEY_TODO_TIME_COLUMN + "=?",
                new String[]{(String)DeletedItem.get("tvHour")});
        // 撤销定时事件
        int hour = Integer.parseInt((String) DeletedItem.get("tvHour"));
        Intent cancel_intent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent del_pending_intent = PendingIntent.getBroadcast(MainActivity.this, hour, cancel_intent, PendingIntent.FLAG_NO_CREATE);
        alarmManager.cancel(del_pending_intent);
    }
}
