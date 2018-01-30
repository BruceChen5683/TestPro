package cn.ws.sz.adater;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.ws.sz.R;
import cn.ws.sz.bean.BusinessBean;
import cn.ws.sz.utils.CommonUtils;
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.DeviceUtils;

/**
 * Created by chenjianliang on 2018/1/13.
 */

public class BusinessItemAdapter extends BaseAdapter{

    private final static String TAG = "BusinessItemAdapter";
    private Context context;
    private List<BusinessBean> data;

    public BusinessItemAdapter(Context context,List<BusinessBean> data){
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        Log.d(TAG, "getCount: "+ data.size());
        return data.size();
    }

    @Override
    public BusinessBean getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.business_item, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.tvBusinessName = (TextView) convertView.findViewById(R.id.tvBusinessName);
            holder.tvBusinessAddress = (TextView) convertView.findViewById(R.id.tvBusinessAddress);
            holder.tvBusinessTel = (TextView) convertView.findViewById(R.id.tvBusinessTel);
            holder.rlFixedPhone = (RelativeLayout) convertView.findViewById(R.id.rlFixedPhone);
            holder.rlAddress = (RelativeLayout) convertView.findViewById(R.id.rlAddress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvBusinessName.setText(getItem(position).getName());
        holder.tvBusinessAddress.setText(getItem(position).getAddress());
        holder.tvBusinessTel.setText(getItem(position).getCellphone());
		String logoUrl = getItem(position).getLogoUrl();
		if(logoUrl.startsWith("http")){
			CommonUtils.setImageView(logoUrl,holder.imageView);
		}else {
			CommonUtils.setImageView(Constant.BASEURL + logoUrl,holder.imageView);
		}


        holder.rlFixedPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.callByDefault(context,holder.tvBusinessTel.getText().toString());
            }
        });

        convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                DeviceUtils.getDeviceScreeHeight(context)/7));

        return convertView;
    }

    public class ViewHolder{
        ImageView imageView;
        TextView tvBusinessName;
        TextView tvBusinessAddress;
        TextView tvBusinessTel;
        RelativeLayout rlFixedPhone;
        RelativeLayout rlAddress;
    }
}
