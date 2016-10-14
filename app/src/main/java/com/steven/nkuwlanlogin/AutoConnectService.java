package com.steven.nkuwlanlogin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.*;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class AutoConnectService extends Service {

    private ConnectivityManager connectivityManager;
    private android.net.NetworkInfo info;
    User user;
    NKNetWork nkNetWork;
    Toast toast;

    ListenNetworkService1 mReceiver;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what)
            {
                case 0x001:
                    break;
                case 0x002:
                    break;
                case 0x003:
                    Bundle b = msg.getData();
                    String fee = b.getString("fee");
                    String flow = b.getString("flow");
                    String time = b.getString("time");
                    String uid = b.getString("uid");
                    Log.d("info",fee+" "+flow+" "+time);
                    //toast.setText("NKU无线神器自动登录NKU内网成功\n账户:"+uid+"\n余额:"+fee+"元\n已用流量:"+flow+"GB");
                    //toast.show();
                    noti(uid,fee,flow);
                    break;
                case 0x004:
                    break;
                case 0x005:
                    break;
                case 0x006:
                    break;
                case 0x007:
                    new HttpLink().getUserInfo(nkNetWork,mHandler);
                    break;
                case 0x008:
                    if(user!=null){
                        new HttpLink().postLink(user.uid,user.upwd);
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                new HttpLink().checkLoginLink(nkNetWork,mHandler);
                            }
                        },888);
                    }
                    break;
                case 0x009:
                    break;
                default:
                    break;
            }
        }
    };

    public class ListenNetworkService1 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Log.d("wlan", "网络状态已经改变");
                connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    String name = info.getTypeName();
                   //Log.d("wlan", "当前网络名称：" + name);
                    WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                    String ssid = wifiManager.getConnectionInfo().getSSID().replace("\"", "");
                    //Log.d("wlan", "当前网络非南开名称：" + ssid);
                    if(name.equals("WIFI")&&(ssid.equals("NKU_WLAN")||ssid.equals("Adobe"))){
                        Log.d("wlan", "当前网络名称：" + ssid);
                        if(user!=null){
                            new HttpLink().checkLoginLink(nkNetWork,mHandler);
                        }
                    }
                } else {
                    Log.d("wlan", "没有可用网络");
                }
            }
        }
    }

    public AutoConnectService() {
    }

    @Override
    public void onCreate() {
        mReceiver = new ListenNetworkService1();
        super.onCreate();
        nkNetWork = new NKNetWork();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);
        toast = Toast.makeText(AutoConnectService.this,"",Toast.LENGTH_LONG);
        Log.d("user", "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent.hasExtra("user")){
            Bundle b = intent.getBundleExtra("user");
            user = (User) b.getSerializable("user");
            //Log.d("user", "user:" + user.uid + "   "  +user.upwd);
        }else {
            user = null;
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        Log.d("user", "onDestroy");
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void noti(String uid,String fee,String flow){
        //Toast.makeText(ActivityTimer.this,"时间到了！！！",Toast.LENGTH_SHORT).show();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder NB = new Notification.Builder(AutoConnectService.this);
        //Uri sound = Uri.fromFile(new File("/system/media/audio/notifications/Clank.ogg"));
        //NB.setSound(sound);
        long[] vibrates = {0,300,200,300};
        NB.setVibrate(vibrates);
        //NB.setLights(Color.WHITE, 2000, 500);
        NB.setSmallIcon(R.drawable.appicon);
        NB.setAutoCancel(true);
        NB.setContentTitle("NKU无线神器已自动登录NKU_WLAN");
        NB.setContentText("账户:"+uid+"\n余额:"+fee+"元\n已用流量:"+flow+"GB");
        NB.setPriority(Notification.PRIORITY_HIGH);
        NB.setWhen(System.currentTimeMillis());
        PendingIntent pi = PendingIntent.getActivity(AutoConnectService.this, 0, new Intent(AutoConnectService.this, MainActivity.class), 0);
        NB.setContentIntent(pi);

        Notification notification = NB.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        manager.notify(2,notification);
    }
}
