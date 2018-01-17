package cn.ws.sz.utils;

/**
 * Created by chenjianliang on 2018/1/10.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;

public class AlbumHelper {
    final String TAG = getClass().getSimpleName();
    Context context;
    ContentResolver cr;
    HashMap<String, String> thumbnailList = new HashMap<String, String>();
    //List<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();
    List<ImageItem> imageList = new ArrayList<ImageItem>();
    private Boolean isBuilded = false;

    private static AlbumHelper instance;

    private AlbumHelper() {
    }

    public static AlbumHelper getInstance() {
        if (instance == null) {
            instance = new AlbumHelper();
        }
        return instance;
    }

    public void init(Context context) {
        if (this.context == null) {
            this.context = context;
            cr = context.getContentResolver();
        }
    }

    public List<ImageItem> getImageList() {
        if (!isBuilded && CheckUtils.checkSDCard(context)) {
            buildImageList();
        }

        return imageList;
    }

    private void getThumbnail() {
        String[] projection = { Thumbnails._ID, Thumbnails.IMAGE_ID,
                Thumbnails.DATA };
        Cursor cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection,
                null, null, null);
        getThumbnailColumnData(cursor);
    }

    private void getThumbnailColumnData(Cursor cur) {
        if (cur.moveToFirst()) {
            int image_id;
            String image_path;
            int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
            int dataColumn = cur.getColumnIndex(Thumbnails.DATA);

            do {
                image_id = cur.getInt(image_idColumn);
                image_path = cur.getString(dataColumn);
                thumbnailList.put("" + image_id, image_path);
            } while (cur.moveToNext());
        }
    }

    private void  buildImageList() {
        long startTime = System.currentTimeMillis();
        // 获取缩略图信息
        getThumbnail();
        // 获取实际的图片信息
        String columns[] = new String[] { Media._ID, Media.BUCKET_ID,
                Media.PICASA_ID, Media.DATA, Media.DISPLAY_NAME, Media.TITLE,
                Media.SIZE, Media.BUCKET_DISPLAY_NAME };
        Cursor cur = cr.query(Media.EXTERNAL_CONTENT_URI, columns, null, null,
                Media.DATE_ADDED + " DESC");
        if (cur.moveToFirst()) {
            int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
            int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
//			int photoNameIndex = cur.getColumnIndexOrThrow(Media.DISPLAY_NAME);
//			int photoTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE);
//			int photoSizeIndex = cur.getColumnIndexOrThrow(Media.SIZE);
//			int bucketDisplayNameIndex = cur
//					.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
//			int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
//			int picasaIdIndex = cur.getColumnIndexOrThrow(Media.PICASA_ID);

            do {
                String _id = cur.getString(photoIDIndex);
                //String name = cur.getString(photoNameIndex);
                String path = cur.getString(photoPathIndex);
//				String title = cur.getString(photoTitleIndex);
//				String size = cur.getString(photoSizeIndex);
//				String bucketName = cur.getString(bucketDisplayNameIndex);
//				String bucketId = cur.getString(bucketIdIndex);
//				String picasaId = cur.getString(picasaIdIndex);

                ImageItem imageItem = new ImageItem();
                imageItem.imageId = _id;
                imageItem.imagePath = path;
                imageItem.thumbnailPath = thumbnailList.get(_id);

                imageList.add(imageItem);
            } while (cur.moveToNext());
        }
        isBuilded = true;
        long endTime = System.currentTimeMillis();
        Log.d(TAG, "use time: " + (endTime - startTime) + " ms");
    }
}
