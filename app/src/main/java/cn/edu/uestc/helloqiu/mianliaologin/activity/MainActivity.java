package cn.edu.uestc.helloqiu.mianliaologin.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.edu.uestc.helloqiu.mianliaologin.R;
import cn.edu.uestc.helloqiu.mianliaologin.component.LoginHttpsWorker;

/**
 * Created by helloqiu on 16/4/5.
 */
public class MainActivity extends Activity {
    EditText password;
    EditText username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        username = (EditText) findViewById(R.id.main_username_editText);
        password = (EditText) findViewById(R.id.main_password_editText);

        Button loginButton = (Button) findViewById(R.id.main_login_button);
        loginButton.setOnClickListener(listener);

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "Login ...", Toast.LENGTH_SHORT).show();
            LoginHttpsWorker loginHttpsWorker = new LoginHttpsWorker(getApplicationContext());
            String returnValue = loginHttpsWorker.login(username.getText().toString(), password.getText().toString());
            if (returnValue.equals(loginHttpsWorker.SUCCESS)) {
                Toast.makeText(getApplicationContext(), "Login Success!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (returnValue.equals(loginHttpsWorker.FAIL)) {
                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                return;
            }
            if (returnValue.equals(loginHttpsWorker.SERVERERROR)) {
                Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };
}
