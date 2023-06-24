package danilo.jovanovic.shoppinglist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShowListActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView list_title;
    private String welcome_list_title, username, owner;
    private ShopListAdapter adapter;
    private List<Task> tasks = new ArrayList<>();
    private ListView tasks_list;
    private EditText task_title;
    private Button add_button, home_button, refresh_button;
    private DatabaseHelper db;
    private boolean shared;
    private HttpHelper httpHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        db = new DatabaseHelper(this);
        httpHelper = new HttpHelper();

        list_title = findViewById(R.id.show_list_title);
        tasks_list = findViewById(R.id.show_list_tasks_list);
        task_title = findViewById(R.id.show_list_task_title);
        add_button = findViewById(R.id.show_list_add_button);
        refresh_button = findViewById(R.id.show_list_refresh_button);
        home_button = findViewById(R.id.show_list_home_button);

        welcome_list_title = getIntent().getStringExtra("title");
        shared = getIntent().getBooleanExtra("shared", false);
        username = getIntent().getStringExtra("username");
        owner = getIntent().getStringExtra("owner");
        list_title.setText(welcome_list_title);

        adapter = new ShopListAdapter(this);
        tasks_list.setAdapter(adapter);


        refresh_button.setOnClickListener(this);
        home_button.setOnClickListener(this);

        tasks = db.loadTasks(welcome_list_title);
        for(int i = 0; i < tasks.size(); i++){
            adapter.addItem(tasks.get(i));
        }

        if(shared && !owner.equals(username)){
            refresh_button.setVisibility(View.VISIBLE);
            adapter.clearTasks();
        }else{
            refresh_button.setVisibility(View.GONE);
        }

        add_button.setOnClickListener(view -> {
            if (task_title.getText().toString().isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Title empty!");
                builder.setPositiveButton("Ok", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                String randomId = generateRandomString(16);
                db.addTask(task_title.getText().toString(), welcome_list_title, randomId, 0);
                if(shared){
                    httpHelper.addTask(task_title.getText().toString(), welcome_list_title, randomId);
                }

                tasks = db.loadTasks(welcome_list_title);
                adapter.clearTasks();

                for(int i = 0; i < tasks.size(); i++){
                    adapter.addItem(tasks.get(i));
                }
            }
        });

        refresh_button.setOnClickListener(view -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.clearTasks();
                        }
                    });
                    try{
                        HttpHelper httpHelper = new HttpHelper();
                        JSONArray tasks = httpHelper.getJSONArrayFromURL(ShopListAdapter.TASKS_URL + "/" + welcome_list_title);

                        for (int i = 0; i < tasks.length(); i++) {
                            JSONObject jsonObject = tasks.getJSONObject(i);
                            String owner = jsonObject.getString("list");
                            boolean check = jsonObject.getBoolean("done");
                            String taskId = jsonObject.getString("taskId");
                            String name = jsonObject.getString("name");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.addItem(new Task(owner, taskId, name, check));
                                }
                            });
                        }
                    }catch (IOException | JSONException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.show_list_home_button){
            Intent intent = new Intent(ShowListActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + random.nextInt(26)));
        }

        return sb.toString();

    }
}
