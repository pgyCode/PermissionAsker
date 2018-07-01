package com.github.pgycode.askpermission;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.pgycode.askpermission.permission.OnAskAppearListener;
import com.github.pgycode.askpermission.permission.PermissionAsker;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private PermissionAsker askerRead;//申请写入权限
    private PermissionAsker askerWrite;//申请读取权限
    private PermissionAsker askerRecode;//申请录音权限
    private PermissionAsker askerCamera;//申请拍照权限

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askerRead = new PermissionAsker(this, new OnAskAppearListener() {
            @Override
            public void onAppear() {
                Toast.makeText(MainActivity.this, "已经成功获取文件读取权限", Toast.LENGTH_SHORT).show();
            }
        },Manifest.permission.READ_EXTERNAL_STORAGE,2,2, "为了测试，我希望申请文件读取权限",true);

        askerWrite = new PermissionAsker(this, new OnAskAppearListener() {
            @Override
            public void onAppear() {
                Toast.makeText(MainActivity.this, "已经成功获取文件写入权限", Toast.LENGTH_SHORT).show();
            }
        },Manifest.permission.WRITE_EXTERNAL_STORAGE,3,3, "为了测试，我希望申请文件写入权限",true);

        askerRecode = new PermissionAsker(this, new OnAskAppearListener() {
            @Override
            public void onAppear() {
                Toast.makeText(MainActivity.this, "已经成功获取录音权限", Toast.LENGTH_SHORT).show();
            }
        },Manifest.permission.RECORD_AUDIO,4,4, "为了测试，我希望申请录音权限",false);

        askerCamera = new PermissionAsker(this, new OnAskAppearListener() {
            @Override
            public void onAppear() {
                Toast.makeText(MainActivity.this, "已经成功获取拍照权限", Toast.LENGTH_SHORT).show();
            }
        },Manifest.permission.CAMERA,5,5, "为了测试，我希望申请拍照权限",false);

        findViewById(R.id.btn_read).setOnClickListener(this);
        findViewById(R.id.btn_write).setOnClickListener(this);
        findViewById(R.id.btn_recode).setOnClickListener(this);
        findViewById(R.id.btn_carame).setOnClickListener(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        askerRead.onChoose(requestCode, grantResults);
        askerWrite.onChoose(requestCode, grantResults);
        askerRecode.onChoose(requestCode, grantResults);
        askerCamera.onChoose(requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        askerRead.onSet(requestCode);
        askerWrite.onSet(requestCode);
        askerRecode.onSet(requestCode);
        askerCamera.onSet(requestCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_read:
                askerRead.onAsk();
                break;
            case R.id.btn_write:
                askerWrite.onAsk();
                break;
            case R.id.btn_recode:
                askerRecode.onAsk();
                break;
            case R.id.btn_carame:
                askerCamera.onAsk();
                break;
        }
    }
}
