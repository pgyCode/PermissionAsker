package com.github.pgycode.askpermission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

public class PermissionAsker {

    private Activity activity;//activity
    private Fragment fragment;//fragment
    private OnAskAppearListener listener;//申请权限时要执行的事件
    private String[] permissions;//权限
    private int permissionCode;//权限申请码，不同权限，不能冲突
    private int activityCode;//设置界面权限申请码，不同权限不能冲突
    private String askReason;//权限跳转提示语句
    private boolean must;

    private AlertDialog alertDialog;

    private boolean isAsking;

    /**
     * 构造一个asker
     * @param activity activity
     * @param listener 申请权限时要执行的事件
     * @param permisstions 权限
     * @param permissionCode 权限申请的requestCode
     * @param activityCode 跳转到设置界面的requestCode
     * @param askReason 是否弹出dialog跳转到权限设置
     */
    private PermissionAsker(Activity activity, Fragment fragment, OnAskAppearListener listener, String[] permisstions, int permissionCode, int activityCode, String askReason, boolean must){
        this.activity = activity;
        this.fragment = fragment;
        this.listener = listener;
        this.permissions = permisstions;
        this.permissionCode = permissionCode;
        this.activityCode = activityCode;
        this.askReason = askReason;
        this.must = must;
    }

    public void onAsk(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for (String permission : permissions){
                if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED){
                    ask();
                    return;
                }
            }
            listener.onAppear();
        } else {
            listener.onAppear();
        }
    }


    /**
     * 权限申请对方选取是否拒绝后
     * @param requestCode
     * @param grantResults
     */
    public void onChoose(int requestCode, int[] grantResults){
        isAsking = false;
        //返回了属于我的权限申请码---1.正常情况，2.异常返回
        if (requestCode == permissionCode){
            //检查权限码数量---->正常返回
            if (grantResults.length == permissions.length) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        showDialog();
                        return;
                    }
                }
                listener.onAppear();
            }
            //异常返回
            else {
                onAsk();
            }
        }
    }


    /**
     * 设置界面返回后
     * @param requestCode
     */
    public void onSet(int requestCode){
        if (requestCode == activityCode && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for (String permission : permissions){
                if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED){
                    if (must) {
                        showDialog();
                    }
                    return;
                }
            }
            listener.onAppear();
        }
    }

    private void showDialog(){
        if (!isAsking) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity).setTitle("权限申请").setMessage(askReason)
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            /**
                             * 如果这些权限中有被不再询问的权限，直接跳转到设置界面。 如果都没有被设置不再询问，弹出申请框
                             */
                            for (String permission : permissions) {
                                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
                                        && ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                                    goToAppSetting();
                                    return;
                                }
                            }
                            ask();
                        }
                    });
            if (must) {
                builder.setCancelable(false);
            } else {
                builder.setNegativeButton("以后开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
            }
            alertDialog = builder.create();
            alertDialog.show();
        }
    }


    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, activityCode);
    }

    //权限申请内部已经按需申请权限，直接申请全部权限即可
    private void ask(){
        isAsking = true;
        if (fragment != null) {
            fragment.requestPermissions(permissions, permissionCode);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(permissions, permissionCode);
            }
        }
    }


    public static class Builder{
        private Activity activity;
        private Fragment fragment;
        private OnAskAppearListener listener;
        private String[] permissions;
        private int permissionCode;
        private int activityCode;
        private String askReason;
        private boolean must;

        public Builder(){
            activity = null;
            fragment = null;
            listener = null;
            permissions = null;
            permissionCode = -1;
            activityCode = -1;
            askReason = null;
            must = false;
        }

        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setFragment(Fragment fragment){
            this.fragment = fragment;
            return this;
        }

        public Builder setListener(OnAskAppearListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setPermissions(String[] permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder setPermisstionCode(int permisstionCode) {
            this.permissionCode = permisstionCode;
            return this;
        }

        public Builder setActivityCode(int activityCode) {
            this.activityCode = activityCode;
            return this;
        }

        public Builder setAskReason(String askReason) {
            this.askReason = askReason;
            return this;
        }

        public Builder setMust(boolean must){
            this.must = must;
            return this;
        }

        public PermissionAsker build(){
            return new PermissionAsker(activity, fragment, listener, permissions, permissionCode, activityCode, askReason, must);
        }
    }
}