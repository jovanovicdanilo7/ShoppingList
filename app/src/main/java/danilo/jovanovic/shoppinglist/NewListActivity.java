package danilo.jovanovic.shoppinglist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class NewListActivity extends AppCompatActivity implements View.OnClickListener{

    private Button new_list_button_ok, new_list_button_save, home_button;
    private String logined_username;
    private RadioGroup radioGroup;
    private int shared;
    private DatabaseHelper db;
    private EditText edit_list_title;
    private TextView list_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        new_list_button_ok = findViewById(R.id.new_list_button_ok);
        new_list_button_save = findViewById(R.id.new_list_save_button);
        home_button = findViewById(R.id.new_list_home_button);
        edit_list_title = findViewById(R.id.new_list_edit_title);
        list_title = findViewById(R.id.new_list_title);
        radioGroup = findViewById(R.id.new_list_radio_group);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(selectedId);
                String selected = radioButton.getText().toString();

                if(selected.equals("Yes")){
                    shared = 1;
                }else{
                    shared = 0;
                }
            }
        });

        new_list_button_ok.setOnClickListener(this);
        new_list_button_save.setOnClickListener(this);
        home_button.setOnClickListener(this);

        db = new DatabaseHelper(this);
    }

    @Override
    public void onClick(View view) {
        logined_username = getIntent().getStringExtra("username");
        if(view.getId() == R.id.new_list_button_ok){
            if(edit_list_title.getText().toString().isEmpty()){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Title empty!");
                builder.setPositiveButton("Ok", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                list_title.setText(edit_list_title.getText().toString());
            }
        }else if(view.getId() == R.id.new_list_save_button){
            if(list_title.getText().toString().equals("")){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Title empty!");
                builder.setPositiveButton("Ok", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (shared == 1) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject list = new JSONObject();
                            list.put("name", list_title.getText().toString());
                            list.put("creator", logined_username);
                            list.put("shared", (shared == 1));

                            HttpHelper httpHelper = new HttpHelper();
                            boolean successfullyCreated = httpHelper.postJSONObjectFromURL(WelcomeActivity.SHARED_LISTS, list);

                            if (successfullyCreated && db.addList(list_title.getText().toString(), logined_username, shared)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "List cannot be created!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }catch (IOException | JSONException e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else if (shared == 0) {
                if(db.addList(list_title.getText().toString(), logined_username, shared)){
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "List cannot be created!", Toast.LENGTH_SHORT).show();
                }
            }
        }else if(view.getId() == R.id.new_list_home_button){
            Intent intent = new Intent(NewListActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
