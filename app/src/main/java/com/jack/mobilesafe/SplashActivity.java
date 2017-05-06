package com.jack.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jack.mobilesafe.utils.CacheUtils;
import com.jack.mobilesafe.utils.LogUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.jack.mobilesafe.utils.CacheUtils.NO_NEED_UPDATE_VERSION;
import static com.jack.mobilesafe.utils.Constans.SERVER_UPDATE_FILE_URL;

public class SplashActivity extends Activity {

    private static final int NO_NEED_UPDATE = 0;
    private static final int NEED_UPDATE = 1;

    private Context context;
    private int newVersionCode;
    private String newVersionName;
    private String description;
    private String downloadUrl;
    private String fileName;
    private boolean isneedupdate;

    private ProgressBar pb_splash;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NO_NEED_UPDATE:
                    new Thread() {
                        @Override
                        public void run() {
                            SystemClock.sleep(2000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    enterHome();
                                }
                            });
                        }
                    }.start();
                    break;
                case NEED_UPDATE:
                    pb_splash.setVisibility(View.GONE);
                    showUpdateDialog();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        context = this;

        //初始化视图
        initView();

        checkUpdate();
    }

    private void initView() {
        TextView tv_version;
        tv_version = (TextView) findViewById(R.id.tv_version_splash);
        try {
            tv_version.append(getVersionName());
        } catch (PackageManager.NameNotFoundException e) {
            tv_version.setText(getString(R.string.unkown_version_name));
        }
        pb_splash = (ProgressBar) findViewById(R.id.pb_splash);
    }

    private void checkUpdate() {
        new Thread() {
            @Override
            public void run() {
                InputStream is = getStreamFromUrl(SERVER_UPDATE_FILE_URL, 5000);
                if (is != null) {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        String result = reader.readLine();
                        JSONObject jsonObject = new JSONObject(result);
                        newVersionCode = jsonObject.getInt("versioncode");
                        LogUtils.LogI("newVersionCode = " + newVersionCode);
                        newVersionName = jsonObject.getString("versionname");
                        LogUtils.LogI("newVersionName = " + newVersionName);
                        description = jsonObject.getString("description");
                        LogUtils.LogI("description = " + description);
                        downloadUrl = jsonObject.getString("downloadurl");
                        LogUtils.LogI("downloadUrl = " + downloadUrl);
                        fileName = jsonObject.getString("filename");
                        LogUtils.LogI("fileName = " + fileName);
                        isneedupdate = newVersionCode > getVersionCode() && newVersionCode != CacheUtils.getInt(context, NO_NEED_UPDATE_VERSION);
                    } catch (IOException e) {
                        LogUtils.LogI(getString(R.string.get_stream_error));
                    } catch (JSONException e) {
                        LogUtils.LogI(getString(R.string.json_analyzing_error));
                    }
                }
                Message msg = mHandler.obtainMessage();
                if (isneedupdate) {
                    msg.what = NEED_UPDATE;
                } else {
                    msg.what = NO_NEED_UPDATE;
                }
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = View.inflate(this, R.layout.updatedialog, null);
        final TextView tv_message = (TextView) view.findViewById(R.id.tv_message_updatedialog);
        final CheckBox cbox = (CheckBox) view.findViewById(R.id.cbox_updatedialog);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setTitle(getString(R.string.title_update));
        tv_message.append(newVersionName + "\n" + description);
        builder.setPositiveButton(getString(R.string.btn_update_now), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadFile(downloadUrl, getExternalCacheDir().getAbsolutePath() + "/" + fileName);
            }
        });
        builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (cbox.isChecked()) {
                    CacheUtils.putInt(context, NO_NEED_UPDATE_VERSION, newVersionCode);
                }
                Message msg = mHandler.obtainMessage();
                msg.what = NO_NEED_UPDATE;
                mHandler.sendMessage(msg);
            }
        });
        builder.create().show();
    }

    private void downloadFile(String url, final String path) {
        InputStream is = getStreamFromUrl(url, 5000);
        if (is != null) {
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.download(url, path, new RequestCallBack<File>() {
                        @Override
                        public void onSuccess(ResponseInfo<File> responseInfo) {
                            installApk(path);
                        }

                        @Override
                        public void onLoading(long total, long current, boolean isUploading) {
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            Message msg = mHandler.obtainMessage();
                            msg.what = NO_NEED_UPDATE;
                            mHandler.sendMessage(msg);
                        }
                    });
        }
    }

    private InputStream getStreamFromUrl(String url, int timeout) {
        try {
            URL httpUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(timeout);
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return conn.getInputStream();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private void enterHome() {
        Intent intent = new Intent();
        intent.setClass(context, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void installApk(String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
    }

    private String getVersionName() throws PackageManager.NameNotFoundException {
        PackageManager pm = getPackageManager();
        PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
        return info.versionName;
    }

    private int getVersionCode() {
        int versionCode;
        try {
            PackageManager pm = getPackageManager();
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            versionCode = 0;
        }
        return versionCode;
    }
}