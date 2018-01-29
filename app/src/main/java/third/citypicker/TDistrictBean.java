package third.citypicker;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @2Do:
 * @Author M2
 * @Version v ${VERSION}
 * @Date 2017/7/7 0007.
 */
public class TDistrictBean implements Parcelable {

    private String id; /*110101*/
    
    private String name; /*东城区*/

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
    }

    public TDistrictBean() {
    }

    protected TDistrictBean(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
    }

    public static final Creator<TDistrictBean> CREATOR = new Creator<TDistrictBean>() {
        @Override
        public TDistrictBean createFromParcel(Parcel source) {
            return new TDistrictBean(source);
        }

        @Override
        public TDistrictBean[] newArray(int size) {
            return new TDistrictBean[size];
        }
    };
}
