package com.steven.nkuwlanlogin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    AutoCompleteTextView autoCompleteTextView;
    LinearLayout l1,l2,l3;
    EditText pwd;
    Button btn_login,btn_origin,btn_unLogin;
    WebView webView;
    String userName,passWord;
    CheckBox checkBox;
    TextView textView,savedInfo;
    NKNetWork nkNetWork;
    //NetworkInfo networkInfo;
    User user;
    ProgressDialog progressDialog;
    boolean isrmb,isLogin;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    int usersNum;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what)
            {
                case 0x001:
                    Toast.makeText(MainActivity.this,"网络正常",Toast.LENGTH_SHORT).show();
                    break;
                case 0x002:
                    Toast.makeText(MainActivity.this,"网络不通",Toast.LENGTH_SHORT).show();
                    break;
                case 0x003:

                    Bundle b = msg.getData();
                    String fee = b.getString("fee");
                    String flow = b.getString("flow");
                    String time = b.getString("time");
                    String uid = b.getString("uid");
                    if(isrmb){
                        checkBox.setChecked(true);
                    }
                    if(checkBox.isChecked()){
                        isrmb = true;
                        editor = pref.edit();
                        editor.putBoolean("remenber_password",true);
                        editor.apply();
                        savedInfo.setText("已保存用户:"+uid);
                    }else{
                        savedInfo.setText("");
                    }
                    //Log.d("info",fee+" "+flow+" "+time);
                    textView.setText("用户"+uid+"已登录\n\n当前余额:"+fee+" 元\n已用流量:"+flow+" GB\n连接时间:"+time+" 分钟");
                    btn_login.setVisibility(View.INVISIBLE);
                    btn_unLogin.setVisibility(View.VISIBLE);
                    savedInfo.setVisibility(View.VISIBLE);
                    l1.setVisibility(View.GONE);
                    l2.setVisibility(View.GONE);
                    l3.setVisibility(View.GONE);
                    isLogin = true;
                    break;
                case 0x004:
                    textView.setText("当前尚未登录!请确认连接校园网、检查密码正确性并再次尝试登陆!");
                    break;
                case 0x005:
                    textView.setText("非登录状态,请正确登陆后再注销");
                    break;
                case 0x006:
                    textView.setText("注销成功!");
                    btn_unLogin.setVisibility(View.INVISIBLE);
                    btn_login.setVisibility(View.VISIBLE);
                    isLogin=false;
                    break;
                case 0x007:
                    textView.setText("用户已登陆");
                    isLogin=true;
                    btn_unLogin.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                    new HttpLink().getUserInfo(nkNetWork,mHandler);
                    break;
                case 0x008:
                    textView.setText("已接入校园网,但用户未登录。\n请确保学号和密码的正确性。");
                    isLogin = false;
                    progressDialog.dismiss();
                    btn_login.setVisibility(View.VISIBLE);
                    if(isrmb){
                        String pass = pref.getString("password","");
                        String uname = pref.getString("username","");
                        user = new User(uname,pass);
                        savedInfo.setVisibility(View.VISIBLE);
                        savedInfo.setText("已保存用户:"+user.uid);
                        new HttpLink().postLink(uname,pass);
                        progressDialog.setMessage("正在自动登陆校园网...");
                        progressDialog.show();
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
                    textView.setText("连接超时,请确认连接南开内网");
                    progressDialog.dismiss();
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setCancelable(false);
                    alert.setMessage("连接超时,请确认连接南开内网");
                    alert.setPositiveButton("重试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new HttpLink().checkLoginLink(nkNetWork,mHandler);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("正在连接校园网...");
                            progressDialog.show();
                        }
                    });
                    alert.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    alert.show();

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("NKU校园网登陆神器");
        nkNetWork = new NKNetWork();
        webView = (WebView) findViewById(R.id.Web);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings ws = webView.getSettings();
        ws.setBuiltInZoomControls(true);
        ws.setJavaScriptEnabled(true);
        //webView.loadUrl("http://202.113.18.110/a30.htm");
        checkBox = (CheckBox) findViewById(R.id.cb);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.username);
        pwd = (EditText) findViewById(R.id.password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_origin = (Button) findViewById(R.id.btn_login1);
        btn_unLogin = (Button) findViewById(R.id.btn_login2);
        btn_login.setVisibility(View.INVISIBLE);
        btn_origin.setVisibility(View.INVISIBLE);
        btn_unLogin.setVisibility(View.INVISIBLE);
        textView = (TextView) findViewById(R.id.Tv_info);
        savedInfo = (TextView) findViewById(R.id.savedinfo);
        l1 = (LinearLayout) findViewById(R.id.line1);
        l2 = (LinearLayout) findViewById(R.id.line2);
        l3 = (LinearLayout) findViewById(R.id.line3);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("正在连接校园网...");
        progressDialog.show();
        savedInfo.setVisibility(View.GONE);
        l1.setVisibility(View.GONE);
        l2.setVisibility(View.GONE);
        l3.setVisibility(View.GONE);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        isrmb = pref.getBoolean("remenber_password",false);
        isLogin = false;
        usersNum  = pref.getInt("userNum",0);

        new HttpLink().checkLoginLink(nkNetWork,mHandler);


        if(isrmb){
            String pass = pref.getString("password","");
            String uname = pref.getString("username","");
            user = new User(uname,pass);
            savedInfo.setVisibility(View.VISIBLE);
            savedInfo.setText("已保存用户:"+user.uid);
            //autoCompleteTextView.setText(uname);
            //pwd.setText(pass);
            //pwd.requestFocus();
            //checkBox.setChecked(true);
            //Toast.makeText(MainActivity.this,"已载入保存的学号密码",Toast.LENGTH_LONG).show();
        }else {
            l1.setVisibility(View.VISIBLE);
            l2.setVisibility(View.VISIBLE);
            l3.setVisibility(View.VISIBLE);
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isrmb){
                    userName = user.uid;
                    passWord = user.upwd;
                }else{
                    userName = autoCompleteTextView.getText().toString();
                    passWord = pwd.getText().toString();
                }

                new HttpLink().postLink(userName,passWord);

                progressDialog.setMessage("正在登陆校园网...");
                progressDialog.show();

                if(checkBox.isChecked()){
                    editor = pref.edit();
                    editor.putString("username", autoCompleteTextView.getText().toString());
                    //editor.putBoolean("remenber_password", true);
                    editor.putString("password", pwd.getText().toString());
                    editor.apply();
                    //isrmb = true;
                    user = new User(autoCompleteTextView.getText().toString(),pwd.getText().toString());
                }
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        new HttpLink().checkLoginLink(nkNetWork,mHandler);
                    }
                },888);
            }
        });
        btn_origin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //webView.loadUrl("http://www.baidu.com");
                //new HttpLink().checkLink(nkNetWork,mHandler);
                new HttpLink().getUserInfo(nkNetWork,mHandler);
            }
        });

        btn_unLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Sure?");
                builder.setMessage("\n你确定要注销吗?");
                builder.setCancelable(true);
                builder.setPositiveButton("注销", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new HttpLink().unLogin(nkNetWork,mHandler);
                        if(!isrmb){
                            l1.setVisibility(View.VISIBLE);
                            l2.setVisibility(View.VISIBLE);
                            l3.setVisibility(View.VISIBLE);
                            textView.setText("请输入学号、密码登陆校园网");
                        }else {
                            savedInfo.setText(savedInfo.getText().toString()+"\n切换用户请点击右上角。");
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.zan:
                Toast.makeText(MainActivity.this,"Made by Steven",Toast.LENGTH_LONG).show();
                break;
            case R.id.resave:

                AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                if(isLogin){
                    ab.setMessage("切换用户必须注销当前登陆,确定?");
                    ab.setCancelable(true);
                    ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new HttpLink().unLogin(nkNetWork,mHandler);
                            savedInfo.setVisibility(View.GONE);
                            autoCompleteTextView.setText("");
                            pwd.setText("");
                            autoCompleteTextView.requestFocus();
                            l1.setVisibility(View.VISIBLE);
                            l2.setVisibility(View.VISIBLE);
                            l3.setVisibility(View.VISIBLE);
                            textView.setText("请输入学号、密码登陆校园网");
                            isrmb = false;
                            editor = pref.edit();
                            editor.putBoolean("remenber_password", false);
                            editor.apply();
                        }
                    });
                    ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    ab.show();
                }else {
                    savedInfo.setVisibility(View.GONE);
                    autoCompleteTextView.setText("");
                    pwd.setText("");
                    autoCompleteTextView.requestFocus();
                    l1.setVisibility(View.VISIBLE);
                    l2.setVisibility(View.VISIBLE);
                    l3.setVisibility(View.VISIBLE);
                    textView.setText("请输入学号、密码登陆校园网");
                    isrmb = false;
                    editor = pref.edit();
                    editor.putBoolean("remenber_password", false);
                    editor.apply();
                }

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
