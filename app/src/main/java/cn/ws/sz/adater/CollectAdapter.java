package cn.ws.sz.adater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.ws.sz.R;
import cn.ws.sz.adater.BusinesssItem3Adapter;
import cn.ws.sz.bean.BusinessBean;
import cn.ws.sz.bean.CollectHistroyBean;

/**
 * Created by chenjianliang on 2018/1/17.
 */

public class CollectAdapter extends BaseAdapter{

    private Context context;
    private List<CollectHistroyBean> data;
    public CollectAdapter(Context context, List<CollectHistroyBean> data){
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public CollectHistroyBean getItem(int position) {
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
                    R.layout.collect_item, null);
            holder = new ViewHolder();
            holder.tvBusinessName = (TextView) convertView.findViewById(R.id.tvName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvBusinessName.setText(data.get(position).getName());

        return convertView;
    }

    public class ViewHolder{
        TextView tvBusinessName;
    }
}
