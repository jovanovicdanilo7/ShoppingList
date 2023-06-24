
package danilo.jovanovic.shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "shared_list_app.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS LISTS (title TEXT PRIMARY KEY, creator TEXT, shared INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS ITEMS (id TEXT PRIMARY KEY, taskTitle TEXT, listTitle TEXT, checked INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ArrayList<Item> loadLists(String username){
        ArrayList<Item> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("LISTS", new String[]{"creator", "title", "shared"}, "creator = ? OR shared = ?", new String[]{username, "1"}, null, null, null);

        while(cursor.moveToNext()){
            items.add(new Item(cursor.getString(cursor.getColumnIndexOrThrow("creator")), cursor.getString(cursor.getColumnIndexOrThrow("title")), (cursor.getInt(cursor.getColumnIndexOrThrow("shared")) == 1)));
        }

        cursor.close();
        db.close();

        return items;
    }

    public ArrayList<Item> loadMyLists(String username){
        ArrayList<Item> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("LISTS", new String[]{"creator", "title", "shared"}, "creator = ?", new String[]{username}, null, null, null);

        while(cursor.moveToNext()){
            items.add(new Item(cursor.getString(cursor.getColumnIndexOrThrow("creator")), cursor.getString(cursor.getColumnIndexOrThrow("title")), (cursor.getInt(cursor.getColumnIndexOrThrow("shared")) == 1)));
        }

        cursor.close();
        db.close();

        return items;
    }

    public boolean addList(String title, String creator, int shared){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query("LISTS", null, "title = ?", new String[]{title}, null, null, null);

        if(cursor.moveToFirst()){
            cursor.close();
            db.close();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("creator", creator);
        values.put("shared", shared);

        db.insert("LISTS", null, values);
        cursor.close();
        db.close();
        return true;
    }

    public boolean removeList(String title){
        SQLiteDatabase db = getWritableDatabase();

        if(db.delete("LISTS", "title = ?", new String[]{title}) == 0){
            return false;
        }

        db.delete("ITEMS", "listTitle = ?", new String[]{title});

        db.close();
        return true;
    }

    public ArrayList<Task> loadTasks(String listTitle){
        ArrayList<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("ITEMS", new String[]{"listTitle", "id", "taskTitle", "checked"}, "listTitle = ?", new String[]{listTitle}, null, null, null);

        while(cursor.moveToNext()){
            tasks.add(new Task(cursor.getString(cursor.getColumnIndexOrThrow("listTitle")), cursor.getString(cursor.getColumnIndexOrThrow("id")), cursor.getString(cursor.getColumnIndexOrThrow("taskTitle")), cursor.getInt(cursor.getColumnIndexOrThrow("checked")) == 1));
        }

        cursor.close();
        db.close();

        return tasks;
    }

    public void addTask(String title, String list, String id, int checked) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("taskTitle", title);
        values.put("listTitle", list);
        values.put("checked", checked);
        values.put("id", id);

        db.insert("ITEMS", null, values);
        db.close();

    }

    public void removeTask(String id, String owner) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("ITEMS", "id = ?", new String[]{id});
        db.close();
    }

    public boolean getChecked(String id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("ITEMS", new String[] {"checked"}, "id = ?", new String[]{id}, null, null, null);
        cursor.moveToFirst();

        if (cursor.getInt(cursor.getColumnIndexOrThrow("checked")) == 1) {
            cursor.close();
            db.close();
            return true;
        }

        cursor.close();
        db.close();

        return false;
    }

    public void setChecked(String id, boolean checked) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("checked", checked ? 1 : 0);

        db.update("ITEMS", values, "id = ?", new String[]{id});
        db.close();
    }

    public boolean queryLists(String key){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("LISTS", null, "title = ?", new String[]{key}, null, null, null);
        boolean found = cursor.moveToFirst();

        cursor.close();
        db.close();

        return found;
    }

    public boolean queryTasks(String key){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("ITEMS", null, "id = ?", new String[]{key}, null, null, null);
        boolean found = cursor.moveToFirst();

        cursor.close();
        db.close();

        return found;
    }
}

