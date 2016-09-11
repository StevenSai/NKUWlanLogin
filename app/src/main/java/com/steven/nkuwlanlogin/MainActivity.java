package com.steven.nkuwlanlogin;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    AutoCompleteTextView autoCompleteTextView;
    EditText pwd;
    Button btn_login,btn_origin;
    WebView webView;
    String userName,passWord;
    CheckBox checkBox;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int usersNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                HttpLink httpLink = new HttpLink();
                httpLink.postLink(userName,passWord);
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
                webView.loadUrl("http://www.baidu.com");
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
