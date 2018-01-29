package cn.ws.sz.adater;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class BusinesssItem2Adapter extends BaseAdapter{
    private Context context;
    private List<BusinessBean> data;

    public BusinesssItem2Adapter(Context context,List<BusinessBean> data){
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
                    R.layout.business_item2, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.ivLogo);
            holder.tvBusinessName = (TextView) convertView.findViewById(R.id.tvName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (!TextUtils.isEmpty(getItem(position).getName())){
            holder.tvBusinessName.setText(getItem(position).getName());
        }else {
            holder.tvBusinessName.setText("商家地址信息不全，请联系客服");
        }

        String url = getItem(position).getLogoUrl();
        if(url.startsWith("http")){
			CommonUtils.setImageView(url,holder.imageView);
		}else {
			CommonUtils.setImageView(Constant.BASEURL + url,holder.imageView);
		}

        int h = (int) context.getResources().getDimension(R.dimen.dp_130);
		convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, h));

        return convertView;
    }

    public class ViewHolder{
        ImageView imageView;
        TextView tvBusinessName;
    }
}
