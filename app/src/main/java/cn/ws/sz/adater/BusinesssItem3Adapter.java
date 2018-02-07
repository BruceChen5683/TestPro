package cn.ws.sz.adater;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

public class BusinesssItem3Adapter extends BaseAdapter{
    private Context context;
    private List<BusinessBean> data;

    public BusinesssItem3Adapter(Context context, List<BusinessBean> data){
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.business_item_3, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.tvBusinessName = (TextView) convertView.findViewById(R.id.tvBusinessName);
            holder.tvAddress = (TextView) convertView.findViewById(R.id.tvBusinessAddress);
            holder.tvCellPhone = (TextView) convertView.findViewById(R.id.tvBusinessTel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvBusinessName.setText(getItem(position).getName());
        if(!TextUtils.isEmpty(getItem(position).getAddress())){
            holder.tvAddress.setText(getItem(position).getAddress());
        }else {
            holder.tvAddress.setText("默认上海浦东新区张江高科");
        }
        holder.tvCellPhone.setText(getItem(position).getCellphone());

		String[] images = getItem(position).getImages();
		String logoUrl = "";
		if(images != null && images.length > 0){
			logoUrl = images[0];
		}
		CommonUtils.setImageView(logoUrl,holder.imageView);


        convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                DeviceUtils.getDeviceScreeHeight(context)/4));
        return convertView;
    }

    public class ViewHolder{
        ImageView imageView;
        TextView tvBusinessName;
        TextView tvAddress;
        TextView tvCellPhone;
    }
}
