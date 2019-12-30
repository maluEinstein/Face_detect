package com.example.android;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class UserWindow extends AppCompatActivity {
    private ListView listView;
    private Dbhelper dbhelper;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_window);
        Intent intent1 =getIntent();
       String User =intent1.getStringExtra("User_id");
        listView = (ListView) findViewById(R.id.user_list);
        dbhelper = new Dbhelper(this, "db_work", null, 1);
        sqLiteDatabase = dbhelper.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("select * from user where name =?",new String[]{User});
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.item_userwindow_listview,
                c,
                new String[]{"_id", "name", "password", "email", "phone"},
                new int[]{R.id.display_id, R.id.displayname, R.id.displaypassword, R.id.displayemail, R.id.displayphone},
                0);
        this.listView.setAdapter(adapter);
       Button zhuxiao = (Button) findViewById(R.id.user_zx);
        Button function = (Button) findViewById(R.id.user_function);
        zhuxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserWindow.this, "返回登录界面", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserWindow.this, MainActivity.class);
                startActivity(intent);
            }
        });
        function.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserWindow.this, "进入功能界面", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserWindow.this, Function_window.class);
                startActivity(intent);
            }
        });
    }
}
