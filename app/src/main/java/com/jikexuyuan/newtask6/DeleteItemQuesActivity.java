package com.jikexuyuan.newtask6;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DeleteItemQuesActivity extends AppCompatActivity {

    private Button btnDelete;
    private Button btnCancel;
    private Intent del_item_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_item_ques);

        WidgetsInitialzing();       // 初始化按钮
        SetBtnOnClickListener();    // 设置按钮的点击事件
    }

    // 初始化两个按钮控件
    private void WidgetsInitialzing() {
        btnDelete = (Button)findViewById(R.id.btn_del);
        btnCancel = (Button)findViewById(R.id.btn_cancel);
        del_item_intent = this.getIntent();
    }

    // 设置两个按钮的点击事件监听器
    private void SetBtnOnClickListener() {
        // 删除按钮被按下后，返回一个状态码
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_OK, del_item_intent);
                finish();
            }
        });

        // 取消按钮被按下后，退出这个界面
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }
}
