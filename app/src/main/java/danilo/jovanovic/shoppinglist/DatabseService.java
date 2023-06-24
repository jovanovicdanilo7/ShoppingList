package danilo.jovanovic.shoppinglist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DatabseService extends Service {

    private HttpHelper httpHelper;
    private DatabaseHelper db;
    private boolean active = true;
    private static final int INTERVAL = 5 * 1000;
    private static final String CHANNEL_ID = "myChannelId", CHANNEL_NAME = "My Channel";

    @Override
    public void onCreate() {
        super.onCreate();

        httpHelper = new HttpHelper();
        db = new DatabaseHelper(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(active){
                    boolean changed = false;
                    try{
                        JSONArray sharedList = httpHelper.getJSONArrayFromURL(MainActivity.BASE_URL + "/lists");

                        for(int i = 0; i < sharedList.length(); i++){
                            JSONObject jsonObject = sharedList.getJSONObject(i);
                            String title = jsonObject.getString("name");
                            String creator = jsonObject.getString("creator");
                            boolean shared = jsonObject.getBoolean("shared");

                            if (!db.queryLists(title)){
                                db.addList(title, creator, shared ? 1 : 0);
                                changed = true;
                            }

                            JSONArray sharedTasks = httpHelper.getJSONArrayFromURL(MainActivity.BASE_URL + "/tasks/" + title);

                            for(int j = 0; j < sharedTasks.length(); j++){
                                JSONObject jsonObject1 = sharedTasks.getJSONObject(j);
                                String id = jsonObject1.getString("taskId");
                                String name = jsonObject1.getString("name");
                                boolean done = jsonObject1.getBoolean("done");

                                if(!db.queryTasks(id)){
                                    db.addTask(name, title, id, done ? 1 : 0);
                                    changed = true;
                                }
                            }
                        }
                    }catch (IOException | JSONException e){
                        e.printStackTrace();
                    }
                        if(changed) {
                            sendNotification(getApplicationContext(), "ShopService", "Shopping Lists Synced Successfully");
                        }
                    try{
                        Thread.sleep(INTERVAL);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        active = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    public static void sendNotification(Context context, String title, String message){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Notification.Builder builder = new Notification.Builder(context, CHANNEL_ID)
                .setContentTitle(title).
                setContentText(message).
                setSmallIcon(R.drawable.ic_home).
                setAutoCancel(true);

        notificationManager.notify(0, builder.build());
    }

}