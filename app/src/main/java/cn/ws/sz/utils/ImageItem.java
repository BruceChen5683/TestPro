package cn.ws.sz.utils;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by chenjianliang on 2018/1/10.
 */

public class ImageItem implements Serializable{

    private static final long serialVersionUID = 1416662018954001614L;

    public String imageId;
    public String thumbnailPath;
    public String imagePath;
    public Bitmap bitmap;

    public String getImageId() {
        return imageId;
    }
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
    public String getThumbnailPath() {
        return thumbnailPath;
    }
    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public Bitmap getBitmap() {
        //bitmap = BitmapFactory.decodeFile(thumbnailPath);
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
