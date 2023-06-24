
package danilo.jovanovic.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Item> items;
    private DatabaseHelper db;
    private HttpHelper httpHelper;
    private String username;

    public ListAdapter(Context context, String username){
        this.context = context;
        this.items = new ArrayList<Item>();
        db = new DatabaseHelper(context);
        httpHelper = new HttpHelper();
        this.username = username;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(Item item){
        items.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        if(items.get(position).getShared()){
            httpHelper.removeList(items.get(position).getOwner(), items.get(position).getTitle());
        }
        db.removeList(items.get(position).getTitle());
        items.remove(position);
        notifyDataSetChanged();
    }

    public void clearAllItems(){
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.welcome_list_row, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.title = convertView.findViewById(R.id.welcome_list_row_title);
            viewHolder.shared = convertView.findViewById(R.id.welcome_list_row_shared);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Item item = items.get(position);
        viewHolder.title.setText(item.getTitle());

        if(item.getShared()){
            viewHolder.shared.setText("True");
        }else{
            viewHolder.shared.setText("False");
        }

        convertView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ShowListActivity.class);
            intent.putExtra("title", items.get(position).getTitle());
            intent.putExtra("shared", items.get(position).getShared());
            intent.putExtra("username", username);
            intent.putExtra("owner", items.get(position).getOwner());
            context.startActivity(intent);
        });

        convertView.setOnLongClickListener(view -> {
            removeItem(position);
            return true;
        });
        return convertView;
    }

    private static class ViewHolder {
        TextView title;
        TextView shared;
    }
}
