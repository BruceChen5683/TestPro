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
public class TProvinceBean implements Parcelable {

  private String id; /*110101*/

  private String name; /*东城区*/


  private ArrayList<TCityBean> cityList;



  @Override
  public String toString() {
    return  name ;
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

  public ArrayList<TCityBean> getCityList() {
    return cityList;
  }

  public void setCityList(ArrayList<TCityBean> cityList) {
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

  public TProvinceBean() {
  }

  protected TProvinceBean(Parcel in) {
    this.id = in.readString();
    this.name = in.readString();
    this.cityList = in.createTypedArrayList(TCityBean.CREATOR);
  }

  public static final Creator<TProvinceBean> CREATOR = new Creator<TProvinceBean>() {
    @Override
    public TProvinceBean createFromParcel(Parcel source) {
      return new TProvinceBean(source);
    }

    @Override
    public TProvinceBean[] newArray(int size) {
      return new TProvinceBean[size];
    }
  };
}
