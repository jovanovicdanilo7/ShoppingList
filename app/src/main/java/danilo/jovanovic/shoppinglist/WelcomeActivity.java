package danilo.jovanovic.shoppinglist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<Item> all_items = new ArrayList<>(), my_items = new ArrayList<>();
    private ListAdapter adapter;
    private ListView list;
    private DatabaseHelper db;
    private Button see_my_list_button, welcome_new_list, home_button, see_shared_lists;
    public static String logined_username;
    public static String SHARED_LISTS = MainActivity.BASE_URL + "/lists";
    private TextView welcome_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Intent serviceIntent = new Intent(this, DatabseService.class);
        startService(serviceIntent);

        list = findViewById(R.id.welcome_list);
        welcome_username = findViewById(R.id.welcome_username);
        welcome_new_list = findViewById(R.id.welcome_new_list_button);
        see_my_list_button = findViewById(R.id.welcome_see_my_list);
        home_button = findViewById(R.id.welcome_home_button);
        see_shared_lists = findViewById(R.id.welcome_see_shared_lists);

        logined_username = getIntent().getStringExtra("username");
        adapter = new ListAdapter(this, logined_username);
        list.setAdapter(adapter);

        welcome_username.setText(logined_username);

        see_shared_lists.setOnClickListener(this);
        welcome_new_list.setOnClickListener(this);
        see_my_list_button.setOnClickListener(this);
        home_button.setOnClickListener(this);

        db = new DatabaseHelper(this);
        all_items = db.loadLists(logined_username);
        my_items = db.loadMyLists(logined_username);

        for(int i  = 0; i < all_items.size(); i++){
            adapter.addItem(all_items.get(i));
        }
    }

    public void onClick(View view){
        if(view.getId() == R.id.welcome_new_list_button){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New List Dialog");
            builder.setMessage("Are you sure you want to create new list?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(WelcomeActivity.this, NewListActivity.class);
                    intent.putExtra("username", logined_username);
                    startActivity(intent);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }else if(view.getId() == R.id.welcome_see_my_list){
            my_items = db.loadMyLists(logined_username);

            adapter.clearAllItems();
            for (int i = 0; i < my_items.size(); i++) {
                adapter.addItem(my_items.get(i));
            }
        } else if (view.getId() == R.id.welcome_see_shared_lists) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        HttpHelper httpHelper = new HttpHelper();
                        JSONArray sharedLists = httpHelper.getJSONArrayFromURL(SHARED_LISTS);

                        if (sharedLists.length() > 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.clearAllItems();
                                }
                            });

                            for (int i = 0; i < sharedLists.length(); i++) {
                                JSONObject jsonObject = sharedLists.getJSONObject(i);
                                String name = jsonObject.getString("name");
                                boolean shared = jsonObject.getBoolean("shared");
                                String creator = jsonObject.getString("creator");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.addItem(new Item(creator, name, shared));
                                    }
                                });
                            }
                        }
                    }catch (IOException | JSONException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }else if(view.getId() == R.id.welcome_home_button){
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        all_items = db.loadLists(logined_username);
        my_items = db.loadMyLists(logined_username);
        adapter.clearAllItems();

        for(int i  = 0; i < all_items.size(); i++){
            adapter.addItem(all_items.get(i));
        }
    }
}

