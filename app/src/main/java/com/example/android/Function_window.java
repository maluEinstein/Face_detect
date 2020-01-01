package com.example.android;

//生活照、证件照、身份证芯片照、带网纹照

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.baidu.aip.face.AipFace;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class Function_window extends AppCompatActivity {
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private ImageView picture;
    private Button takephotoByAlbum;
    private Button takephotoByCamera;
    private Button detect;
    private Uri imageuri;
    private static final String APP_ID = "15964243";
    private static final String API_KEY = "zycXczDprBLdjAGyno2Mw1Yq";
    private static final String SECRET_KEY = "f4aToMo2SEaHMV8i02GLoot0QLB7x6yQ";
    private int face_resultNum = -1;  //图像中人脸数量
    private String face_age = null, face_gender = null, face_race = null, face_beauty = null, face_expression = null;
    private JSONObject res;
    private Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_window);
        picture = (ImageView) findViewById(R.id.imageView_DefaultPicture);
        takephotoByAlbum = (Button) findViewById(R.id.takephotoByAlbum);                //1从相册中选择
        takephotoByCamera = (Button) findViewById(R.id.takephotoByCamera);                //2相机拍摄
        detect = (Button) findViewById(R.id.button_detect);
        takephotoByAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Function_window.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Function_window.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
            }
        });
        takephotoByCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File outputimage = new File(getExternalCacheDir(), "output_image.jpg");       //创建File对象，用于存储拍照后的图片
                try {                                                                               //照片保存在SDK缓存目录下
                    if (outputimage.exists()) {
                        outputimage.delete();
                    }
                    outputimage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageuri = FileProvider.getUriForFile(Function_window.this,
                        "com.example.android.fileprovider", outputimage);
                //启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                res = null;
                if (bitmap == null)
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.face_test);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                final byte[] arrays = stream.toByteArray();
                final String image = Base64.encodeToString(arrays, Base64.DEFAULT);
                //位图压缩转比特数组    再转BASE64格式
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HashMap<String, String> options = new HashMap<>();
                        options.put("face_field", "age,beauty,expression,face_shape,gender,glasses,landmark,landmark150,race,quality,eye_status,emotion,face_type");
                        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
                        client.setConnectionTimeoutInMillis(2000);
                        client.setSocketTimeoutInMillis(60000);
                        res = client.detect(image, "BASE64", options);
                        try {
                            Message message = Message.obtain();
                            message.what = 1;
                            message.obj = res;
                            handler.sendMessage(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Message message = Message.obtain();
                            message.what = 2;
                            handler.sendMessage(message);
                        }
                    }
                }).start();
            }
        });
        //尝试intent传回数据
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageuri));
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            case CHOOSE_PHOTO:
                try {
                    bitmap = BitmapFactory.decodeStream(
                            getContentResolver().openInputStream(data.getData()));
                    picture.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            face_resultNum = 0;
            face_gender = null;
            face_age=null;
            face_beauty=null;
            face_expression=null;
            if (msg.what == 1) {
                JSONObject res = (JSONObject) msg.obj;
                try {
                    res = res.getJSONObject("result");
                    JSONArray resJSONArray = res.getJSONArray("face_list");
                    face_resultNum = res.getInt("face_num");
                    res = resJSONArray.getJSONObject(0);
                    System.out.println(res.toString());
                    face_gender = res.getString("gender");
                    face_age = res.getString("age");
                    face_beauty = res.getString("beauty");
                    JSONObject expression = res.getJSONObject("expression");
                    face_expression = expression.getString("type");
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Function_window.this);
                    String[] mItems = {"检测到的人脸数量：" + face_resultNum, " 性别：" + face_gender, "年龄：" + face_age, "颜值：" + face_beauty, "笑容：" + face_expression};
                    alertDialog.setTitle("人脸识别报告").setItems(mItems, null).create().show();
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else {
                Toast.makeText(Function_window.this, "图片不够清晰请重新选择", Toast.LENGTH_LONG).show();
            }
        }
    };
}
