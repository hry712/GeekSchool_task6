package com.jikexuyuan.newtask6;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GetItemActivity extends AppCompatActivity {

    private EditText etHour;
    private EditText etThings;
    private Button btnAddItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_item);

        etHour = (EditText)findViewById(R.id.et_hour);
        etThings = (EditText)findViewById(R.id.et_things);
        btnAddItem = (Button)findViewById(R.id.btn_add_item);
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = 0;
                String things = null;

                // 检查两个输入框中是否有输入
                if (!etHour.getText().toString().isEmpty() &&
                        !etThings.getText().toString().isEmpty()) {
                    try {
                        hour = Integer.parseInt(etHour.getText().toString());
                        things = etThings.getText().toString();
                    } catch (ClassCastException e) {
                        Toast.makeText(GetItemActivity.this, "Illegel hour num!", Toast.LENGTH_SHORT).show();
                    }
                    if (hour < 0 || hour > 24) {
                        Toast.makeText(GetItemActivity.this, "Your Hour is out of range!", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent return_intent = new Intent();
                        return_intent.putExtra("tvHour", hour);
                        return_intent.putExtra("tvThings", things);
                        setResult(Activity.RESULT_OK, return_intent);
                        finish();
                    }
                } else {
                    Toast.makeText(GetItemActivity.this, "Please fill the Item!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
