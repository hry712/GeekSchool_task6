package com.jikexuyuan.newtask6;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class ToDoListFragment extends ListFragment {
    private OnItemAddedListenerIF onItemAddedListener;
    private OnItemDeletedListenerIF onItemDeletedListener;
    public static final int DELETE_REQUESTCODE = 666;

    public ToDoListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //TODO: 有两个接口，分别在 Item 被添加或被删除时回调启动
        try {
            onItemAddedListener = (OnItemAddedListenerIF) activity;
            onItemDeletedListener = (OnItemDeletedListenerIF) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implment OnNewItemAddedListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 给每个列表项添加一个长按事件监听器，用来弹出删除菜单
        AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                HashMap hashMap = (HashMap)getListView().getItemAtPosition(i);
//                Toast.makeText(getActivity().getBaseContext(), "Long Clicked", Toast.LENGTH_SHORT).show();
                // 编写一个弹出删除对话框的 Activity 类并用在这里
                HashMap hashMap = (HashMap)getListView().getItemAtPosition(i);
//                onItemDeletedListener.onItemDeleted(hashMap);
                String item_hour = (String)hashMap.get("tvHour");
                String item_things = (String)hashMap.get("tvThings");
                Intent del_item_intent = new Intent(getContext(), DeleteItemQuesActivity.class);
                del_item_intent.putExtra("hour", item_hour);
                del_item_intent.putExtra("things", item_things);
                startActivityForResult(del_item_intent, DELETE_REQUESTCODE);
                return true;
            }
        };
        getListView().setOnItemLongClickListener(onItemLongClickListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DELETE_REQUESTCODE &&
                resultCode == Activity.RESULT_OK) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("tvHour", data.getStringExtra("hour"));
            hashMap.put("tvThings", data.getStringExtra("things"));
            onItemDeletedListener.onItemDeleted(hashMap);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_todo_item, container, false);
    }
}
