package com.example.android;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class registered extends AppCompatActivity {
    private EditText name;
    private EditText password;
    private EditText email;
    private EditText phone;
    private Dbhelper dbhelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        dbhelper = new Dbhelper(this, "db_work", null, 1);
        name = (EditText) findViewById(R.id.editText2);
        password = (EditText) findViewById(R.id.editText3);
        email = (EditText) findViewById(R.id.editText4);
        phone = (EditText) findViewById(R.id.editText5);
        Button adddata = (Button) findViewById(R.id.button_userRegistered);
        adddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = dbhelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("name", name.getText().toString());
                values.put("password", password.getText().toString());
                values.put("email", email.getText().toString());
                values.put("phone", phone.getText().toString());
                db.insert("user", null, values);
                Intent intent = new Intent(registered.this, MainActivity.class);
                Toast.makeText(registered.this, "用户信息注册成功", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
    }
}
