package cn.ws.sz.adater;

import android.content.Context;

import java.util.List;

import cn.ws.sz.bean.CityBean;
import cn.ws.sz.bean.ProvinceBean;
import third.wheelviewchoose.AbstractWheelTextAdapter;

/**
 * Created by chenjianliang on 2018/1/18.
 */

public class ChooseCityAdapter extends AbstractWheelTextAdapter{
    private List<CityBean> mList;
    private Context mContext;

    public ChooseCityAdapter(Context context, List<CityBean> list){
        super(context);
        mList = list;
        mContext = context;
    }
    /**
     * Gets items count
     *
     * @return the count of wheel items
     */
    @Override
    public int getItemsCount() {
        return mList.size();
    }

    /**
     * Returns text for specified item
     *
     * @param index the item index
     * @return the text of specified items
     */
    @Override
    protected CharSequence getItemText(int index) {
        return mList.get(index).getCity();
    }
}
