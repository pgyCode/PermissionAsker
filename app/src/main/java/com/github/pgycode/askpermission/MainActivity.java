package com.github.pgycode.askpermission;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.pgycode.askpermission.permission.OnAskAppearListener;
import com.github.pgycode.askpermission.permission.PermissionAsker;

public class MainActivity extends AppCompatActivity {


    //初始化强制权限
    private PermissionAsker initAsker;

    //内部非强制权限
    private PermissionAsker softFileAsker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAsker = new PermissionAsker.Builder()
                .setActivity(this)
                .setActivityCode(2)
                .setPermisstionCode(2)
                .setAskReason("为了测试，我们需要你的这些权限")
                .setPermissions( new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA
                })
                .setMust(true)
                .setListener(new OnAskAppearListener() {
                    @Override
                    public void onAppear() {
                        Toast.makeText(MainActivity.this, "成功啦", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

        softFileAsker = new PermissionAsker.Builder()
                //activity
                .setActivity(this)
                //activty请求码
                .setActivityCode(4)
                //请求原因
                .setAskReason("非强制读取你的文件权限。")
                //回调事件
                .setListener(new OnAskAppearListener() {
                    @Override
                    public void onAppear() {
                        Toast.makeText(MainActivity.this, "非强制拿到", Toast.LENGTH_SHORT).show();
                    }
                })
                //是否必须获取
                .setMust(false)
                //要获取的权限
                .setPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})
                //权限请求码
                .setPermisstionCode(4)
                .build();

        initAsker.onAsk();


        findViewById(R.id.btn_soft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                softFileAsker.onAsk();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initAsker.onChoose(requestCode, grantResults);
        softFileAsker.onChoose(requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initAsker.onSet(requestCode);
        softFileAsker.onSet(requestCode);
    }
}
