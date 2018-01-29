package third.citypicker;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * @2Do:
 * @Author M2
 * @Version v ${VERSION}
 * @Date 2017/7/7 0007.
 */
public class TCityBean implements Parcelable {
    

    private String id; /*110101*/
    
    private String name; /*东城区*/


    private ArrayList<TDistrictBean> cityList;

    @Override
    public String toString() {
        return name;
    }

    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<TDistrictBean> getCityList() {
        return cityList;
    }

    public void setCityList(ArrayList<TDistrictBean> cityList) {
        this.cityList = cityList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeTypedList(this.cityList);
    }

    public TCityBean() {
    }

    protected TCityBean(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.cityList = in.createTypedArrayList(TDistrictBean.CREATOR);
    }

    public static final Creator<TCityBean> CREATOR = new Creator<TCityBean>() {
        @Override
        public TCityBean createFromParcel(Parcel source) {
            return new TCityBean(source);
        }

        @Override
        public TCityBean[] newArray(int size) {
            return new TCityBean[size];
        }
    };
}
