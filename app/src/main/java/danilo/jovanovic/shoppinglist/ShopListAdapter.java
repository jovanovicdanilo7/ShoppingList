package danilo.jovanovic.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShopListAdapter extends BaseAdapter {

    private Context context;
    private List<Task> tasks;
    private DatabaseHelper db;
    public static String TASKS_URL = MainActivity.BASE_URL + "/tasks";

    public ShopListAdapter(Context context) {
        this.context = context;
        this.tasks = new ArrayList<Task>();
        db =  new DatabaseHelper(context);
    }

    public void clearTasks(){
        tasks.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    public void setCheck(int position, boolean check){
        db.setChecked(tasks.get(position).getId(), check);
        new HttpHelper().setChecked(tasks.get(position).getId(), check);
        tasks.get(position).setCheck(check);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void removeItem(int position){
        db.removeTask(tasks.get(position).getId(), tasks.get(position).getOwner());
        new HttpHelper().removeTask(tasks.get(position).getId(), tasks.get(position).getOwner());
        tasks.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(Task task){
        tasks.add(task);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.show_list_row, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.title = convertView.findViewById(R.id.show_list_row_task_name);
            viewHolder.check = convertView.findViewById(R.id.show_list_row_checkbox);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Task task = (Task) getItem(position);
        viewHolder.title.setText(task.getTitle());
        try {
            viewHolder.check.setChecked(db.getChecked(task.getId()));
        }catch (Exception e){
            e.printStackTrace();
        }

        viewHolder.check.setOnClickListener(view -> {
            setCheck(position, viewHolder.check.isChecked());
        });

        if (task.isCheck()) {
            viewHolder.title.setPaintFlags(viewHolder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            viewHolder.title.setPaintFlags(viewHolder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        convertView.setOnLongClickListener(view -> {
            removeItem(position);
            return true;
        });

        return convertView;
    }

    private static class ViewHolder{
        TextView title;
        CheckBox check;
    }
}
