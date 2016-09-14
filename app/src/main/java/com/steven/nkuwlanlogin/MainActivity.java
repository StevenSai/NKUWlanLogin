package com.steven.nkuwlanlogin;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
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
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    AutoCompleteTextView autoCompleteTextView;
    EditText pwd;
    Button btn_login,btn_origin,btn_unLogin;
    WebView webView;
    String userName,passWord;
    CheckBox checkBox;
    TextView textView;
    NKNetWork nkNetWork;
    NetworkInfo networkInfo;

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
                    //Log.d("info",fee+" "+flow+" "+time);
                    textView.setText("登陆成功!\n上网信息\n用户:"+uid+"\n当前余额:"+fee+" 元\n已用流量:"+flow+" GB\n连接时间:"+time+" 分钟");
                    break;
                case 0x004:
                    textView.setText("当前尚未登录!请确认连接校园网、检查密码正确性并再次尝试登陆!");
                    break;
                case 0x005:
                    textView.setText("非登录状态,请正确登陆后再注销");
                    break;
                case 0x006:
                    textView.setText("注销成功!");
                    break;
                case 0x007:
                    textView.setText("已经登陆!无需再次登陆\n请点击余额信息查看详情");
                    break;
                case 0x008:
                    textView.setText("登陆请求已发送,请点击余额信息查看登陆状态");
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        textView = (TextView) findViewById(R.id.Tv_info);
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isrmb = pref.getBoolean("remenber_password",false);
        usersNum  = pref.getInt("userNum",0);

        if(isrmb){
            String pass = pref.getString("password","");
            String uname = pref.getString("username","");

            autoCompleteTextView.setText(uname);
            pwd.setText(pass);
            pwd.requestFocus();
            checkBox.setChecked(true);
            Toast.makeText(MainActivity.this,"已载入保存的学号密码",Toast.LENGTH_LONG).show();
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = autoCompleteTextView.getText().toString();
                passWord = pwd.getText().toString();
                new HttpLink().checkLoginLink(userName,passWord,nkNetWork,mHandler);
                if(checkBox.isChecked()){
                    editor = pref.edit();
                    editor.putString("username", autoCompleteTextView.getText().toString());
                    editor.putBoolean("remenber_password", true);
                    editor.putString("password", pwd.getText().toString());
                    editor.apply();
                }

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
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
