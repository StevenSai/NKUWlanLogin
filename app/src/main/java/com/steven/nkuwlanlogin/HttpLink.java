package com.steven.nkuwlanlogin;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;

import com.squareup.okhttp.Request;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by stevensai on 16/9/11.
 */
public class HttpLink {


    public void checkLoginLink(final NKNetWork nkNetWork,final Handler handler){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0x009);
                    }
                },5000);
                NetworkInfo info = nkNetWork.getLogStatus();
                timer.cancel();
                if(info.status == info.ONLINE){
                    handler.sendEmptyMessage(0x007);
                }else if(info.status == info.UN_LOGIN){
                    handler.sendEmptyMessage(0x008);
                }
            }
        }).start();
    }

    public void postLink(final String username, final String passWord){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient defaultclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://202.113.18.110:801/eportal/?c=ACSetting&a=Login");
                HttpResponse httpResponse;

                //设置post参数
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("DDDDD", username));
                params.add(new BasicNameValuePair("upass", passWord));
                params.add(new BasicNameValuePair("0MKKey", "\\265\\307 \\302\\274 (Login)"));

                //获得个人主界面的HTML
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    httpResponse = defaultclient.execute(httpPost);
                    Log.i("xyz", String.valueOf(httpResponse.getStatusLine().getStatusCode()));
                    HttpEntity entity = httpResponse.getEntity();
                    String MAINBODYHTML = EntityUtils.toString(entity);
                    //IsLoginSuccessful(MAINBODYHTML);
                    Log.i("xyz",MAINBODYHTML);

                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        StringBuffer sb = new StringBuffer();
                        //HttpEntity entity = httpResponse.getEntity();
                        //String MAINBODYHTML = EntityUtils.toString(entity);
                        //IsLoginSuccessful(MAINBODYHTML);
                        //Log.d("ddddd",MAINBODYHTML);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void checkLink(final NKNetWork nkNetWork, final Handler mHandler){
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean inter = nkNetWork.checkInternet();
                if(inter){
                    mHandler.sendEmptyMessage(0x001);
                }else{
                    mHandler.sendEmptyMessage(0x002);
                }
            }
        }).start();
    }

    public void getUserInfo(final NKNetWork nkNetWork, final Handler handler){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0x009);
                    }
                },5000);
                NetworkInfo info = nkNetWork.getLogStatus();
                timer.cancel();
                Log.d("state",info.status+"");
                if(info.status==info.ONLINE){
                    Message msg = new Message();
                    msg.what = 0x003;
                    Bundle bundle = new Bundle();
                    bundle.putString("uid",info.uid);
                    bundle.putString("fee",info.fee+"");
                    bundle.putString("flow",String.format("%.3f",info.flow));
                    bundle.putString("time",String.valueOf((int)info.time));
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }else if(info.status==info.UN_LOGIN){
                    handler.sendEmptyMessage(0x004);
                }
            }
        }).start();
    }

    public void unLogin(final NKNetWork nkNetWork,final Handler handler){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0x009);
                    }
                },5000);
                NetworkInfo info = nkNetWork.getLogStatus();
                timer.cancel();
                if (info.status!=NetworkInfo.ONLINE){
                    handler.sendEmptyMessage(0x005);//未登录,无法注销
                }else {
                    nkNetWork.logout();
                    handler.sendEmptyMessage(0x006);//成功注销
                }
            }
        }).start();
    }
}
