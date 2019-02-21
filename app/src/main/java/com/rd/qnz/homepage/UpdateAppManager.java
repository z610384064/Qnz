package com.rd.qnz.homepage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.rd.qnz.R;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.mine.PersonInfomationAct;
import com.rd.qnz.tools.BaseParam;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

/**
 * app更新时的对话框
 */
public class UpdateAppManager {

    //下载应用存放全路径
    private static final String FILE_PATH =
            Environment.getExternalStorageDirectory() + File.separator + "QianNeiZhu"
                    + File.separator + "update";

    private static final int UPDATE_PROGRESSBAR_MSG = 0x01;
    private static final int INSTALL_MSG = 0x02;
    private Context context;
    private String appUrl = "";
    //下载应用的对话框
    private Dialog downloadDialog;
    //下载应用的进度条
    private ProgressBar progressBar;
    private int curProgress;
    //用户是否取消下载
    private boolean isDownloadCancel;

    public UpdateAppManager(Context context) {
        this.context = context;
        File path = new File(FILE_PATH);
        if (!path.exists()) {
            path.mkdirs();
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PROGRESSBAR_MSG:
                    progressBar.setProgress(curProgress);
                    break;
                case INSTALL_MSG:
                    installApp(context,(String) msg.obj);
                    break;
            }
        }
    };


    /**
     * 提示更新
     */
    public void showUpdateDialog(final String url, final String filename, final Context context, String str, String type) {

        appUrl = url;
        LayoutInflater inflaterDl = LayoutInflater.from(context);
        RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.dialog_update_layout, null);
        final Dialog update_dialog = new AlertDialog.Builder(context).create();

        update_dialog.show();
        update_dialog.getWindow().setContentView(layout);

        TextView message = (TextView) layout.findViewById(R.id.message);
        message.setText(str);
        ImageView close = (ImageView) layout.findViewById(R.id.close);
        if (type.equals("1")) {
            close.setVisibility(View.GONE);
            update_dialog.setCanceledOnTouchOutside(false);
            update_dialog.setCancelable(false);
        } else {
            close.setVisibility(View.VISIBLE);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {   //点击了更新底下的关闭图标,把数据存入up_data字段
                    update_dialog.dismiss();
                    SharedPreferences sp = context.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("up_data", filename);
                    editor.commit();
                }
            });
        }

        Button update = (Button) layout.findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: 2017/4/1 0001  判断是否有存储权限
                update_dialog.dismiss();
                File downloadPath = new File(FILE_PATH);
                if (downloadPath.exists()) {
                    //删除路径下的文件
                    File[] childFiles = downloadPath.listFiles();
                    for (int i = 0; i < childFiles.length; i++) {
                        childFiles[i].delete();
                    }
                }
                showDownloadDialog(filename,url);
            }
        });
    }

    /**
     * 显示下载对话框
     */
    public void showDownloadDialog(String filename,String url) {
        appUrl = url;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_download, null);
        progressBar = (ProgressBar) view.findViewById(R.id.download_progressbar);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        /*builder.setView(view)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        isDownloadCancel = true;
                    }
                });*/

        builder.setCancelable(false);
        downloadDialog = builder.create();
        downloadDialog.show();
        downloadApp(filename);
    }

    /**
     * 下载新版本应用
     */
    private void downloadApp(final String filename) {
        new Thread(new Runnable() {

            @Override
            public void run() {


                OkGo.get(appUrl)//
                        .tag(this)//
                        .execute(new FileCallback() {  //文件下载时，可以指定下载的文件目录和文件名
                            @Override
                            public void onSuccess(File file, Call call, Response response) {
                                // file 即为文件数据，文件保存在指定目录

                                file.renameTo(new File(FILE_PATH, filename));

                                downloadDialog.dismiss();
                                Message msg = new Message();
                                msg.what = INSTALL_MSG;
                                msg.obj = filename;
                                handler.sendMessage(msg);
//                                installApp3(file);
                                Log.e("####", "#####下载完成");

                            }

                            @Override
                            public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                                //这里回调下载进度(该回调在主线程,可以直接更新ui)
                                Log.e("####", "#####下载中");
                                Log.e("####", "#####下载中" + progress);
                                //  Log.e("####", "#####下载中" + (int) (progress * 100));
                                curProgress = (int) (progress * 100);
                                handler.sendEmptyMessage(UPDATE_PROGRESSBAR_MSG);

                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                Log.e("####", "#####解析错误");
                                MineShow.toastShow("解析包发生错误", context);
                                downloadDialog.dismiss();
                            }
                        });

            }
        }).start();
    }

    /**
     * 安装新版本应用
     */
    private void installApp(Context context,String filename) {
        Log.e("####", "#####installApp");
        File appFile = new File(FILE_PATH, filename);
        if (!appFile.exists()) {
            return;
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authority = context.getPackageName() + ".provider";
            uri = FileProvider.getUriForFile(context, authority, appFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            context.startActivity(intent);
        } else {
            uri = Uri.fromFile(appFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void installApp3(File filen) {
        if (filen.exists()) {
            openFile(filen, context);
        } else {
            return;
        }
    }

    public void openFile(File var0, Context var1) {
        Intent var2 = new Intent();
        var2.addFlags(268435456);
        var2.setAction("android.intent.action.VIEW");
        String var3 = getMIMEType(var0);
        var2.setDataAndType(Uri.fromFile(var0), var3);
        try {
            var1.startActivity(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
            Toast.makeText(var1, "没有找到打开此类文件的程序", Toast.LENGTH_SHORT).show();
        }
    }

    public String getMIMEType(File var0) {
        String var1 = "";
        String var2 = var0.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
    }


}
