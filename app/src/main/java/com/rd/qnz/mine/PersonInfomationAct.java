package com.rd.qnz.mine;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.community.CropPictureActivity;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.view.CircularNetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;


/**
 * 个人信息界面
 */
@RuntimePermissions
public class PersonInfomationAct extends BaseActivity implements View.OnClickListener, ActionSheet.ActionSheetListener {
    private String name,sex,birthday,phone,avayar_url;
    private TextView tv_touxiang,tv_name,tv_sex,tv_phone,tv_birthday;
    private CircularNetworkImageView iv_head; //头像
    APIModel apiModel = new APIModel();

    private ArrayList<String> param = null;
    private ArrayList<String> value = null;
    private MyApplication myApp;
    private GetDialog dia;
    private CustomProgressDialog progressDialog = null;
    private boolean modified;

    /**
     * sp文件
     *
     */
    private SharedPreferences preferences;
    private String oauthToken,cardId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_infomation);
        Intent i=getIntent();
        name=i.getStringExtra("name");
        sex=i.getStringExtra("sex");
        birthday=i.getStringExtra("birthday");
        phone=i.getStringExtra("phone_num");
        avayar_url=i.getStringExtra("avayar_url");

        this.dia = new GetDialog();
        myApp = (MyApplication) getApplication();

        //sp_user.xml
        preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        cardId = preferences.getString("cardId", ""); //身份证

        initBar();
        intView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        cardId = preferences.getString("cardId", ""); //身份证
    }

    private void intView() {
        tv_touxiang= (TextView) findViewById(R.id.tv_touxiang);
        tv_name= (TextView) findViewById(R.id.tv_name);
        tv_sex= (TextView) findViewById(R.id.tv_sex);
        tv_phone= (TextView) findViewById(R.id.tv_phone);
        tv_birthday= (TextView) findViewById(R.id.tv_birthday);
        iv_head= (CircularNetworkImageView) findViewById(R.id.mine_info_portrait_iv);
        iv_head.setOnClickListener(this);
        if (phone.length() == 11) {
            tv_phone.setText(phone.replace(phone.substring(3, 7), "****"));
        }
        tv_name.setText(name);
        if (!TextUtils.isEmpty(sex)){
            if (sex.equals("1")){
                tv_sex.setText("男");
            }else if (sex.equals("2")){
                tv_sex.setText("女");
            }else {
                tv_sex.setText("保密");
            }
        }else {
            tv_sex.setText("保密");
        }
        if (!TextUtils.isEmpty(birthday)){
            tv_birthday.setText(birthday);
        }else {
            tv_birthday.setText("保密");
        }

        if (avayar_url.contains("app_default_portrait.png")){ //说明之前没有头像
            iv_head.setImageResource(R.drawable.person);
        }else { //如果有头像这句话隐藏
            tv_touxiang.setVisibility(View.GONE);

            ImageLoader.getInstance().displayImage(avayar_url, iv_head, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    iv_head.setImageResource(R.drawable.person);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    iv_head.setImageBitmap(bitmap);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });


        }
    }

    private void initBar() {
        ImageView actionbar_side_left_iconfont = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        actionbar_side_left_iconfont.setVisibility(View.VISIBLE);
        actionbar_side_left_iconfont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (modified) {  //上传图片成功
                    setResult(RESULT_OK);
                }
                finish();
            }
        });
        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText("个人信息");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mine_info_portrait_iv:
                showChoosePicDialog();
                break;
        }
    }


    /**
     * 显示修改头像的对话框
     */
    protected void showChoosePicDialog() {
        setTheme(R.style.ActionSheetStyleIOS7);

        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles("拍照", "从相册选取")
                .setCancelableOnTouchOutside(true)
                .setListener(this)
                .show();
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch (index) {
            case 0: //打开相机

                if (Build.VERSION.SDK_INT >= 23) { //设备号是6.0及以上 动态判断权限
                    requestPermission();
                }else {
                    takePhoto();  //直接去拍照
                }

                break;
            case 1:  //从本地文件获取图片
                if (Build.VERSION.SDK_INT >= 23) { //设备号是6.0及以上 动态判断权限
                    requestPermission2();
                }else {
                    pickPicture();
                }
                break;
        }
    }
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int IMAGE_REQUEST_CODE = 2;
    private static final int CROP_REQUEST_CODE = 3;
    private String path = "";
    private String picname = "";

    private void takePhoto() {
        // TODO: 2017/3/31 0031  做动态权限判断

        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        picname = "MyHeadPortrait_" + new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        File file = new File(MyApplication.IMG_SAVE_PATH, picname);
        path = file.getPath();
        Uri imageUri;

        // Android 7 FileProvider处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authority = getPackageName() + ".provider";
            imageUri = FileProvider.getUriForFile(PersonInfomationAct.this, authority, file);
            photoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            imageUri = Uri.fromFile(file);
        }

        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(photoIntent, CAMERA_REQUEST_CODE);
    }

    private void pickPicture() {
        Intent pickPic = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPic, IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE: //从相机界面返回
                    Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri;
                    File file = new File(MyApplication.IMG_SAVE_PATH, picname);

                    // Android 7 FileProvider处理
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        String authority = getPackageName() + ".provider";
                        uri = FileProvider.getUriForFile(PersonInfomationAct.this, authority, file);
                        localIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } else {
                        uri = Uri.fromFile(file);
                    }
                    localIntent.setData(uri);
                    sendBroadcast(localIntent);
                    Intent cropPicture = new Intent(this, CropPictureActivity.class);  //去头像选取界面
                    cropPicture.putExtra("path", path);
                    startActivityForResult(cropPicture, CROP_REQUEST_CODE); //去截图图片
                    break;
                case IMAGE_REQUEST_CODE:  //从相册选取(点击了勾之后拿到照片)
                    Uri selectedImage = data.getData();
                    String[] filePathColumns = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                    String picturePath = "";
                    if (c != null) {
                        c.moveToFirst();
                        int columnIndex = c.getColumnIndex(filePathColumns[0]);
                        picturePath = c.getString(columnIndex);
                        c.close();
                    } else {
                        picturePath = selectedImage.getPath();
                    }
                    Intent intent2 = new Intent(this, CropPictureActivity.class);
                    intent2.putExtra("path", picturePath);
                    startActivityForResult(intent2, CROP_REQUEST_CODE);
                    break;
                case CROP_REQUEST_CODE://相册界面返回
                    Bitmap bitmap = null;
                    if (data != null) {
                        byte[] bis = data.getByteArrayExtra("bitmap");
                        bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
                    }
                    if (bitmap != null) {
                        savePortrait(bitmap);
                        iv_head.setImageBitmap(bitmap);
                    }
                    break;

            }
        }

    }

    /**
     * 保存图片到本地,并上传头像
     * @param bitmap
     */
    private void savePortrait(Bitmap bitmap) {
        final File imgcache = new File(MyApplication.IMG_SAVE_PATH, MyApplication.PORTRAIT_FILE_NAME);
        if (imgcache.exists()) {
            imgcache.delete();
        }
        try {
            imgcache.createNewFile();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imgcache));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        myApp.bt_head=bitmap;
        uploadImage(imgcache);
    }

    public void uploadImage(File file) {

        Log.e("uploadImage", "头像上传");
        JSONObject param = new JSONObject();
        String sign = null;
        try {
            param.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, oauthToken);
            param.put(BaseParam.URL_QIAN_API_APPID, myApp.appId);
            param.put(BaseParam.URL_QIAN_API_SERVICE, "uploadAvatar");
            param.put(BaseParam.URL_QIAN_API_SIGNTYPE, myApp.signType);
            String[] array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=uploadAvatar",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};
            sign = apiModel.sortStringArray(array);
            param.put(BaseParam.URL_QIAN_API_SIGN, sign);
            progressDialog = dia.getLoginDialog(PersonInfomationAct.this, "正在上传头像..");
            progressDialog.show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = BaseParam.URL_QIAN_AVAYARURL
                + "?appId=" + myApp.appId
                + "&" + "service=" + "uploadAvatar"
                + "&" + "signType=" + myApp.signType
                + "&" + "sign=" + sign
                + "&" + "oauthToken=" + oauthToken;

        OkGo.post(url)
                .tag(this)
                .params("avatarFile", file)   // 可以添加文件上传
                //  .connTimeOut()
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, okhttp3.Response response) {
                        if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                            progressDialog.dismiss();
                        }
                        Log.i("头像上传返回", s);
                        try {
                            JSONObject object = new JSONObject(s);
                            int errorcode = object.getInt("resultCode");
                            if (errorcode == 1) {
                                showToast("头像上传成功");
                                modified = true;
                                Log.e("头像", avayar_url);
                            } else {
                                showToast("头像上传失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(okhttp3.Call call, okhttp3.Response response, Exception e) {
                        super.onError(call, response, e);
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调上传进度(该回调在主线程,可以直接更新ui)
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (modified) {  //成功之后显示图片
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }




    /**
     * 申请相机权限
     */


    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission() {
        //申请权限
        PersonInfomationActPermissionsDispatcher.needPermissionWithCheck(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PersonInfomationActPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void needPermission() {  //获得了权限
        takePhoto();  //去拍照
    }
    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onShowRation(final PermissionRequest request) {
        request.proceed();
    }

    @OnPermissionDenied({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onDenied() {
        Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onNeverAsk() {
        LayoutInflater inflaterDl = LayoutInflater.from(PersonInfomationAct.this);
        LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_cameraperssion_layout, null);
        final Dialog dialog = new AlertDialog.Builder(PersonInfomationAct.this).create();
        dialog.show();
        dialog.getWindow().setContentView(layout);

        Window dialogWindow = dialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值

        p.width = (int) (d.getWidth() * 0.7); // 宽度设置为屏幕的0.65，根据实际情况调整
        p.height = (int) (0.8*p.width); // 高度设置为屏幕的0.6，根据实际情况调整
        dialogWindow.setAttributes(p);


        Button cancel = (Button) layout.findViewById(R.id.negativeButton);
        Button sure = (Button) layout.findViewById(R.id.positiveButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent localIntent = new Intent();
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivity(localIntent);

            }
        });

    }

    /**
     * 从本地文件获取
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission2() {
        //申请权限
        PersonInfomationActPermissionsDispatcher.needsPermission2WithCheck(this);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void needsPermission2() {

            pickPicture();
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void onShowRation2(final PermissionRequest request) {
        request.proceed();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void onDenied2() {
        Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void onNeverAsk2() {
        LayoutInflater inflaterDl = LayoutInflater.from(PersonInfomationAct.this);
        LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_photoperssion_layout, null);
        final Dialog dialog = new AlertDialog.Builder(PersonInfomationAct.this).create();
        dialog.show();
        dialog.getWindow().setContentView(layout);

        Window dialogWindow = dialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值

        p.width = (int) (d.getWidth() * 0.7); // 宽度设置为屏幕的0.65，根据实际情况调整
        p.height = (int) (0.8*p.width); // 高度设置为屏幕的0.6，根据实际情况调整
        dialogWindow.setAttributes(p);




        Button cancel = (Button) layout.findViewById(R.id.negativeButton);
        Button sure = (Button) layout.findViewById(R.id.positiveButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent localIntent = new Intent();
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivity(localIntent);

            }
        });
    }




}
