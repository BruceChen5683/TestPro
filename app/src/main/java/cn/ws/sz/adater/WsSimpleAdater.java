package cn.ws.sz.adater;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.ws.sz.R;

/**
 * Created by chenjianliang on 2018/1/13.
 */

public class WsSimpleAdater extends BaseAdapter{

    private Context context;
    private List<String> data;
    private int selectedPosition = -1;// 选中的位置

    public WsSimpleAdater(Context context,List<String> data){
        this.context = context;
        this.data = data;
    }

    public void setSelectedPosition(int selectedPosition){
        this.selectedPosition = selectedPosition;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.sw_simple_layout, null);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.simpleTV);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText(getItem(position).toString());

        if(selectedPosition == position){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                convertView.setBackgroundColor(context.getResources().getColor(R.color.grayLight,null));
                holder.tv.setTextColor(context.getResources().getColor(R.color.text_red,null));
            }else {
                convertView.setBackgroundColor(context.getResources().getColor(R.color.grayLight));
                holder.tv.setTextColor(context.getResources().getColor(R.color.text_red));
            }
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                convertView.setBackgroundColor(context.getResources().getColor(R.color.white,null));
                holder.tv.setTextColor(context.getResources().getColor(R.color.black,null));
            }else {
                convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
                holder.tv.setTextColor(context.getResources().getColor(R.color.black));
            }
        }
        return convertView;
    }

    public class ViewHolder{
        TextView tv;
    }
}
