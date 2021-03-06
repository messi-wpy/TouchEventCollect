package com.example.messi_lp.touchdemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private static String TAG = "SHELL";
    private final String NOTIFICATION_ID = "keep_alive";
    private TextView mdata_tv;
    private EditText mSelect_ed;
    private EditText mUserId_et;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Button mFileList_bt;
    private Button mShow_files;
    private NotificationManager notificationManager;
    private ProgressBar mProgressBar;
    private BottomNavigationView navigationView;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_v2);
        mdata_tv = findViewById(R.id.content_text);


        mSelect_ed = findViewById(R.id.select_text);
        mdata_tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        mShow_files = findViewById(R.id.file_bt);
        navigationView=findViewById(R.id.bottom_navigation);

        mUserId_et = findViewById(R.id.user_ed);
        // TODO: 19-4-14  add progressbar
        //mProgressBar=findViewById(R.id.progress_bar);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);

        toolbar=findViewById(R.id.toobar);
        toolbar.inflateMenu(R.menu.toobar_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            Uri uri = Uri.parse("https://github.com/messi-wpy/TouchEventCollect/blob/master/readme.md");
            Intent webintent = new Intent();
            webintent.setAction("android.intent.action.VIEW");
            webintent.setData(uri);
            startActivity(webintent);
            return true;
        });

        navigationView.setOnNavigationItemSelectedListener(this::BottomNavigationOnclick);
        mShow_files.setOnClickListener(v -> onButtonClick());

    }



    private void onButtonClick(){
        if (TextUtils.isEmpty(mSelect_ed.getText().toString())){
            String[] files = MainActivity.this.fileList();
            mdata_tv.setText("");
            for (String f : files) {
                mdata_tv.append(f + '\n');

            }
        }else {
            FileInputStream in = null;

            try {
                String fileName;
                if (!TextUtils.isEmpty(mSelect_ed.getText().toString()))
                    fileName = mSelect_ed.getText().toString();
                else {
                    fileName = MainActivity.this.fileList()[1];
                }
                in = MainActivity.this.openFileInput(fileName);
                mdata_tv.setText("");
                byte[] buffer = new byte[1024];
                int line = 0;
                while ((line = in.read(buffer)) > 0)
                    mdata_tv.append(new String(buffer, 0, line));

            } catch (FileNotFoundException e) {
                mdata_tv.setText("文件未找到！可以尝试清空输入框点击展示文件获取所有文件名");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null)
                        in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }




    private boolean isCollecting=false;

    private boolean BottomNavigationOnclick(MenuItem menuItem){
        switch (menuItem.getItemId()){

            case R.id.start:{
                if (isCollecting) {
                    mdata_tv.setText("数据收集中请勿重复点击");
                    break;
                }
                mdata_tv.setText("");
                try {
                    startGetevent();
                    createNotification();
                } catch (IOException e) {

                    e.printStackTrace();
                }
                break;
            }
            case R.id.end:{
                isCollecting=false;
                quit();

            }
            case R.id.send_menu:{
                compositeDisposable.dispose();
                try {
                    Observable.create(new ObservableOnSubscribe<HashMap<String, List<List<NewData.ListBean>>>>() {

                        @Override
                        public void subscribe(ObservableEmitter<HashMap<String, List<List<NewData.ListBean>>>> emitter) throws Exception {
                            FileConvert fileConvert = new FileConvert(MainActivity.this);
                            String filename;
                            if (!TextUtils.isEmpty(mSelect_ed.getText()))
                                filename= mSelect_ed.getText().toString();
                            else {
                                Toast.makeText(MainActivity.this,"请输入文件名",Toast.LENGTH_SHORT).show();
                                return;
                            }

                            fileConvert.readFromFile(getApplicationContext().getFilesDir() + "/" + filename);
                            emitter.onNext(fileConvert.getHashMap());
                        }
                    }).subscribeOn(Schedulers.io())
                            .flatMap((Function<HashMap<String, List<List<NewData.ListBean>>>, ObservableSource<NewData>>) stringListHashMap -> {
                                if (stringListHashMap == null)
                                    return Observable.error(Throwable::new);
                                List<NewData> list = new ArrayList<>();
                                for (HashMap.Entry<String, List<List<NewData.ListBean>>> entry : stringListHashMap.entrySet()) {
                                    NewData temp = new NewData();
                                    String name = TextUtils.isEmpty(mUserId_et.getText().toString()) ? "00000" : mUserId_et.getText().toString();
                                    temp.setUserID(Integer.parseInt(name));
                                    temp.setAppName(entry.getKey());
                                    temp.setList(entry.getValue());
                                    list.add(temp);
                                    Log.i(TAG, "apply: " + entry.getKey());

                                }
                                Log.i(TAG, "apply: flatMap 1");

                                return Observable.fromIterable(list);
                            })
                            .flatMap((Function<? super NewData, ? extends ObservableSource<Response>>) data -> {
                                Log.i(TAG, "buttonInit: flatMap 2");
                                Response httpresponse = FileConvert.upLoad(data);
                                if (httpresponse == null)
                                    return Observable.empty();
                                return Observable.create(emitter -> {
                                    emitter.onNext(httpresponse);
                                });
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe((Consumer<Response>) httpresponse -> {
                                        if (httpresponse.code() != 200) {
                                            Log.i(TAG, "httpresponse != 200");
                                            mdata_tv.setText("wrong post");
                                        }
                                        String s = httpresponse.body().string();
                                        String json = (String) s;
                                        ReData response;
                                        try {
                                            Gson gson = new Gson();
                                            response = gson.fromJson(json, ReData.class);
                                            mdata_tv.setText("success");
                                        } catch (JsonSyntaxException e) {
                                            Log.i(TAG, "buttonInit:  illegal Expection");
                                            e.printStackTrace();
                                        }


                                    }
                                    , Throwable::printStackTrace);
                } catch (Exception e) {
                    Log.i(TAG, "buttonInit: onError");
                    e.printStackTrace();
                }

            }


        }
        return true;


    }
    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();

    }

    public void quit() {
        compositeDisposable.dispose();
        mdata_tv.setText("结束收集");
        notificationManager.cancel(0);

    }

    public void createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_ID);
        builder.setContentTitle("收集数据程序")
                .setContentText("收集数据程序运行中")
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        intent.setClass(this, MainActivity.class);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        notificationManager.notify(0, builder.build());
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, keyEvent);

    }

    public void startGetevent() throws IOException {
        Date time = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String command;
        command = mSelect_ed.getText().toString();
        FileOutputStream wirter = MainActivity.this.openFileOutput(CommonUtils.timeName(), MODE_APPEND);
        Disposable d = RxBus.getDefault().toObservable(String.class)
                .subscribe(s -> {
                    synchronized (wirter) {
                        Log.i(TAG, "startGetevent: " + s);
                        wirter.write(s.getBytes());
                        wirter.write('\n');
                    }
                });
        compositeDisposable.add(d);

        Disposable disposable = Observable.create(new ObservableOnSubscribe<Coordinate>() {
            @Override
            public void subscribe(ObservableEmitter<Coordinate> emitter) throws Exception {
                Process process = null;
                BufferedReader successResult = null;
                DataOutputStream os = null;
                try {
                    process = Runtime.getRuntime().exec(MyShell.COMMAND_SU);
                    os = new DataOutputStream(process.getOutputStream());
                    if (TextUtils.isEmpty(command)) {
                        String temp = "getevent -l";
                        os.write(temp.getBytes());
                    } else
                        os.write(command.getBytes());

                    os.writeBytes(MyShell.COMMAND_LINE_END);
                    os.flush();
                    Log.i(TAG, "getEvent: start");


                    Log.i(TAG, "getEvent: restart");


                    successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String s;

                    int x, y;
                    x = y = 0;
                    //emitter.isDisposed() is important
                    while ((s = successResult.readLine()) != null && !emitter.isDisposed()) {
                        int tempX = 0;
                        int tempY = 0;
                        //更新
                        tempX = CommonUtils.convertToSting(s);
                        if (tempX == CommonUtils.ACTION_DOWN_NUM) {
                            emitter.onNext(new Coordinate(CommonUtils.ACTION_DOWN_NUM, y));
                            successResult.readLine();
                            continue;
                        }

                        if (tempX == CommonUtils.ACTION_UP_NUM) {
                            emitter.onNext(new Coordinate(CommonUtils.ACTION_UP_NUM, y));
                            successResult.readLine();
                            continue;
                        }

                        if (tempX != 0) {
                            tempY = CommonUtils.convertToSting(successResult.readLine());
                            if (tempY != 0) {
                                if (CommonUtils.distance2(x, y, tempX, -tempY) < 400)
                                    continue;
                                x = tempX;
                                y = -tempY;
                                emitter.onNext(new Coordinate(x, y));

                            } else if (tempX > 0) {
                                if (Math.abs(tempX - x) < 20)
                                    continue;
                                x = tempX;
                                emitter.onNext(new Coordinate(tempX, y));
                            } else {
                                if (Math.abs(-tempX - y) < 20)
                                    continue;
                                y = -tempX;
                                emitter.onNext(new Coordinate(x, -tempX));
                            }
                        } else
                            continue;

                    }

                } catch (IOException io) {
                    io.printStackTrace();
                } finally {
                    try {
                        if (wirter != null)
                            wirter.close();
                        if (os != null) {
                            os.close();
                        }
                        if (successResult != null) {
                            successResult.close();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (process != null) {
                        process.destroy();
                    }
                }
                Log.i(TAG, "subscribe: onNext");
            }
        }).subscribeOn(Schedulers.newThread())
                // .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable1 ->
                {
                    mdata_tv.setText("收集数据中");
                })
                .subscribe(new Consumer<Coordinate>() {
                    @Override
                    public void accept(Coordinate s) throws Exception {
                        Log.i(TAG, "accept: accept");

                        if (s == null) return;
                        if (s.getX() == CommonUtils.ACTION_UP_NUM) {
                            wirter.write(CommonUtils.ACTION_UP.getBytes());
                            wirter.write('\n');
                            return;
                        }
                        if (s.getX() == CommonUtils.ACTION_DOWN_NUM) {
                            wirter.write(CommonUtils.ACTION_DOWN.getBytes());
                            wirter.write('\n');
                            return;
                        }
                        wirter.write(String.valueOf(System.nanoTime()).getBytes());
                        wirter.write(s.getFormatP().getBytes());
                        wirter.write('\n');
                    }
                }, throwable -> throwable.printStackTrace(), () -> {


                });
        compositeDisposable.add(disposable);

    }

    public String getEvent(@NonNull String command) {
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;

        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(MyShell.COMMAND_SU);
            os = new DataOutputStream(process.getOutputStream());
            os.write(command.getBytes());
            os.writeBytes(MyShell.COMMAND_LINE_END);
            os.flush();
            Log.i(TAG, "getEvent: start");

            Thread.sleep(10000);

            Log.i(TAG, "getEvent: restart");

            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
                Log.i(TAG, "getEvent: " + s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }


        } catch (IOException io) {
            io.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (process != null) {
                process.destroy();
            }
        }
        return successMsg.length() != 0 ? successMsg.toString()
                : errorMsg.toString();


    }
}
