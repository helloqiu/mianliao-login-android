package cn.edu.uestc.helloqiu.mianliaologin.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.CircularProgressButton;

import cn.edu.uestc.helloqiu.mianliaologin.R;
import cn.edu.uestc.helloqiu.mianliaologin.component.LoginHttpsWorker;

/**
 * Created by helloqiu on 16/4/5.
 */
public class MainActivity extends Activity {
    EditText password;
    EditText username;
    CircularProgressButton loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        username = (EditText) findViewById(R.id.main_username_editText);
        password = (EditText) findViewById(R.id.main_password_editText);

        loginButton = (CircularProgressButton) findViewById(R.id.main_login_button);
        loginButton.setOnClickListener(listener);

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Toast.makeText(getApplicationContext(), "Login ...", Toast.LENGTH_SHORT).show();
            loginButton.setIndeterminateProgressMode(true);
            loginButton.setProgress(50);
            final Handler buttonHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    loginButton.setProgress(0);
                    return false;
                }
            });
            final Handler handler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.what == 0) {
                        //Toast.makeText(getApplicationContext(), "Login success!", Toast.LENGTH_SHORT).show();
                        loginButton.setProgress(100);
                    }
                    /*
                    if (msg.what == 1) {
                        Toast.makeText(getApplicationContext(), "Login Fail!Check the password and username!", Toast.LENGTH_SHORT).show();
                    }
                    if (msg.what == 2) {
                        Toast.makeText(getApplicationContext(), "Maybe the server is down!", Toast.LENGTH_SHORT).show();
                    }
                    */
                    if (msg.what == 1 || msg.what == 2) {
                        loginButton.setProgress(-1);
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Message message = new Message();
                            buttonHandler.sendMessage(message);
                        }
                    }).start();
                    return false;
                }
            });
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LoginHttpsWorker loginHttpsWorker = new LoginHttpsWorker(getApplicationContext());
                    String returnValue = loginHttpsWorker.login(username.getText().toString(), password.getText().toString());
                    Message message = new Message();
                    if (returnValue.equals(loginHttpsWorker.SUCCESS)) {
                        message.what = 0;
                    }
                    if (returnValue.equals(loginHttpsWorker.FAIL)) {
                        message.what = 1;
                    }
                    if (returnValue.equals(loginHttpsWorker.SERVERERROR)) {
                        message.what = 2;
                    }
                    handler.sendMessage(message);
                }
            }).start();

        }
    };
}
