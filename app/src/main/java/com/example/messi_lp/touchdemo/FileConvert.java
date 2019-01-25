package com.example.messi_lp.touchdemo;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.messi_lp.touchdemo.NewData.ListBean.DataListBean;
import com.example.messi_lp.touchdemo.NewData.ListBean;
import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

public class FileConvert {
    private HashMap<String, List<ListBean>> hashMap;
    private static OkHttpClient client;
    public FileConvert() {
        hashMap = new HashMap<>();

    }


    public HashMap<String, List<ListBean>>getHashMap(){
        return hashMap;
    }
    //同步的好事请求
    public static String upLoad(NewData data) throws IOException {
        if (client==null){
            HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client=new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();
        }
        if (data.getList().isEmpty())return null;
        Gson gson=new Gson();
        String json=gson.toJson(data);

        RequestBody body=RequestBody.create(MediaType.parse("application/json; charset=utf-8"),json);
        Request request=new Request.Builder()
                .url("http://119.23.79.87:5555/api/test")
                .post(body)
                .build();

        return client.newCall(request)
                .execute().body().toString();
        }

    /**
     * 返回给定字符串的最后一部分
     * "[,\\-().]"
     */
    public void readFromFile(String name) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(name));
            String line;
            String appName = "";
            String DownName = "";
            List<DataListBean> operate = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] elem = line.split("[,\\-().]");
                if (elem[0].equals(CommonUtils.ACTION_DOWN)) {
                    DownName = appName;
                    continue;
                }
                if (elem[0].equals(CommonUtils.ACTION_UP)) {
                    List<ListBean> temp;
                    if ((temp = hashMap.get(DownName)) == null) continue;
                    temp=hashMap.get(DownName);
                    temp.add(new ListBean(operate));
                    hashMap.put(DownName, temp);
                    operate.clear();
                    continue;

                }
                if (!elem[elem.length - 1].matches("\\d+")) {
                    appName = elem[elem.length - 1];
                    if (!hashMap.containsKey(appName))
                        hashMap.put(appName, new ArrayList<ListBean>());
                    continue;
                }
                if (elem.length > 1) {
                    DataListBean data = new DataListBean();
                    data.setX(Integer.parseInt(elem[2]));
                    data.setY(Integer.parseInt(elem[3]));
                    data.setTime(elem[0]);
                    operate.add(data);
                    continue;
                }


            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
