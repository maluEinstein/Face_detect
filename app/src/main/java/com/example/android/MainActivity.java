package com.example.android;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText etname;
    private EditText etpassword;
    private Dbhelper dbhelper;
    private SQLiteDatabase sqLiteDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button1 = (Button) findViewById(R.id.button2);
        Button button2 = (Button) findViewById(R.id.button);
        etname = (EditText) findViewById(R.id.editText1);              //editText1是用户名
        etpassword = (EditText) findViewById(R.id.editText);           //editText是密码
        button1.setOnClickListener(new View.OnClickListener() {
                @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "进入注册界面", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, registered.class);
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbhelper = new Dbhelper(MainActivity.this, "db_work", null, 1);
                sqLiteDatabase = dbhelper.getReadableDatabase();
                Cursor c = sqLiteDatabase.rawQuery("select * from user where name=?", new String[]{etname.getText().toString()});
                try {
                    if (etname.getText().toString().equals("") || etpassword.getText().toString().equals("")) {
                        Toast.makeText(MainActivity.this, "请输入用户名密码", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (c.moveToFirst()) {
                            if (etpassword.getText().toString().equals(c.getString(c.getColumnIndex("password")))) {
                                Toast.makeText(MainActivity.this, "进入用户界面", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, UserWindow.class);
                                 String name=etname.getText().toString();
                                 intent.putExtra("User_id",name);
                                 startActivity(intent);
                            }
                            else {
                                Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(MainActivity.this, "不存在该用户", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }

}
