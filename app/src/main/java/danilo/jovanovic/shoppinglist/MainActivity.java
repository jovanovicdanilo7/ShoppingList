package danilo.jovanovic.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button main_login_button, main_register_button;
    public static String BASE_URL = "http://192.168.1.6:3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_login_button = findViewById(R.id.main_login_button);
        main_register_button = findViewById(R.id.main_register_button);

        main_register_button.setOnClickListener(this);
        main_login_button.setOnClickListener(this);

        String dbFilePath = getApplicationContext().getDatabasePath("shared_list_app.db").getAbsolutePath();
        Log.d("Database Path", dbFilePath);

        JNIExample jni = new JNIExample();
        int res = jni.increment(5);
        Log.d("JNI_TAG",String.valueOf(res));
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.main_login_button){
            main_login_button.setVisibility(View.INVISIBLE);
            main_register_button.setVisibility(View.INVISIBLE);

            LoginFragment fragment = LoginFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_blank, fragment).commit();
        }else if(view.getId() == R.id.main_register_button){
            main_login_button.setVisibility(View.INVISIBLE);
            main_register_button.setVisibility(View.INVISIBLE);

            RegisterFragment fragment = RegisterFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_blank, fragment).commit();
        }
    }
}