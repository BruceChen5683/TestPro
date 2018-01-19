package cn.ws.sz.adater;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import cn.ws.sz.R;
import cn.ws.sz.activity.MoneyActivity;
import cn.ws.sz.activity.ProxyActivity;
import cn.ws.sz.utils.Constant;

import static cn.ws.sz.utils.Constant.MAX_BUSINESS_PHOTO;
import static cn.ws.sz.utils.Constant.MAX_IDCARD_PHOTO;

/**
 * Created by chenjianliang on 2018/1/10.
 */

public class BusinessPhotoAdapter extends BaseAdapter{
    private Context mContext;
    private DeleteView deleteView;
    private boolean isShowDelete;// 根据这个变量来判断是否显示删除图标，true是显示，false是不显示
    private int id = -1;
    private int photoType = Constant.PHOTO_TYPE_BUSINESS;

    public BusinessPhotoAdapter(Context context,int photoType) {
        this.mContext = context;
        this.photoType = photoType;

    }

    public void setIsShowDelete(boolean isShowDelete,int id) {
        this.isShowDelete = isShowDelete;
        this.id = id;
        notifyDataSetChanged();
    }
    public void setDeleteView(DeleteView deleteView){
        this.deleteView = deleteView;
    }

    @Override
    public int getCount() {
        if (photoType == Constant.PHOTO_TYPE_BUSINESS){
            if (MoneyActivity.SelectedImages.size() == MAX_BUSINESS_PHOTO) {
                return MAX_BUSINESS_PHOTO;
            }
            return MoneyActivity.SelectedImages.size() + 1;
        }else{
            if (MoneyActivity.SelectedImages.size() == MAX_BUSINESS_PHOTO) {
                return MAX_BUSINESS_PHOTO;
            }
            return ProxyActivity.SelectedImages.size() + 1;
        }


    }

    @Override
    public Object getItem(int position) {
        if (photoType == Constant.PHOTO_TYPE_BUSINESS) {
            return MoneyActivity.SelectedImages.get(position);
        }else {
            return ProxyActivity.SelectedImages.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.business_photo_item, null);
            holder = new ViewHolder();
            holder.businessPhoto = (ImageView) convertView.findViewById(R.id.business_photo);
            holder.deleteView = (ImageView) convertView.findViewById(R.id.delete_markView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.deleteView.setVisibility(View.VISIBLE);// 设置删除按钮是否显示

        holder.deleteView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteView.delete(position);
            }
        });

        int size = photoType == Constant.PHOTO_TYPE_BUSINESS ? MoneyActivity.SelectedImages.size():ProxyActivity.SelectedImages.size();

        if (position ==  size) {
            holder.businessPhoto.setImageBitmap(BitmapFactory.decodeResource(
                    mContext.getResources(), R.mipmap.my_camera));
            holder.deleteView.setVisibility(View.GONE);
            if (position == (photoType == Constant.PHOTO_TYPE_BUSINESS ? MAX_BUSINESS_PHOTO:MAX_IDCARD_PHOTO)) {
                holder.businessPhoto.setVisibility(View.GONE);
            }
        } else {
            holder.businessPhoto.setImageBitmap(
                    photoType == Constant.PHOTO_TYPE_BUSINESS ?
                    MoneyActivity.SelectedImages.get(position).getBitmap():ProxyActivity.SelectedImages.get(position).getBitmap());
        }

        return convertView;
    }

    public class ViewHolder {
        ImageView businessPhoto;
        ImageView deleteView;
    }

    public interface DeleteView{
        void delete(int id);
    }
}
