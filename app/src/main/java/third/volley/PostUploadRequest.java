package third.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by chenjianliang on 2018/1/12.
 */

public class PostUploadRequest extends Request<String> {

    private final String prefix = "--";
    private final String end = "\r\n";
    private final String boundary = "--------------" + System.currentTimeMillis();
    private final String mimeType = "multipart/form-data;boundary=" + boundary;
    private Response.Listener<String> mListener;
    private Map<String, String[]> fileMap;

    public PostUploadRequest(String url, Map<String, String[]> fileMap,
                             Response.Listener<String> mListener,
                             Response.ErrorListener listener) {
        super(Method.POST, url, listener);
        this.mListener = mListener;
        this.fileMap = fileMap;
        setShouldCache(false);
        setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String je = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(je, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException var3) {
            return Response.error(new ParseError(var3));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    public String getBodyContentType() {
        return mimeType;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream ds = new DataOutputStream(bos);
            buildTextPart(ds);
            buildFilePart(ds);
            ds.write((prefix + boundary + prefix + end).getBytes("UTF-8"));
            ds.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void buildFilePart(DataOutputStream ds) {
        StringBuilder sb = new StringBuilder();
        Object[] key_arr = fileMap.keySet().toArray();
        for (Object name : key_arr) {
            String[] val = fileMap.get(name.toString());
            String path = val[0];
            String filename = val[1];
            try {
                sb.append(prefix);
                sb.append(boundary);
                sb.append(end);
                sb.append("Content-Disposition: form-data;name=\"");
                sb.append(name.toString());
                sb.append("\";filename=\"");
                sb.append(filename);
                sb.append("\"" + end);//filename是文件名，如xxx.jpg，file是服务器传递参数的名字
                sb.append("Content-Type: application/octet-stream;charset=UTF-8" + end);
                sb.append(end);
                ds.write(sb.toString().getBytes("UTF-8"));
                /* 取得文件的FileInputStream */
                FileInputStream fStream = new FileInputStream(path);//path是文件本地地址
                /* 设置每次写入1024bytes */
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int length;
                /* 从文件读取数据至缓冲区 */
                while ((length = fStream.read(buffer)) != -1) {
                    /* 将资料写入DataOutputStream中 */
                    ds.write(buffer, 0, length);
                }
                ds.write(end.getBytes("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void buildTextPart(DataOutputStream ds) {
        try {
            Map<String, String> map = getParams();
            Object[] key_arr = map.keySet().toArray();
            for (Object name : key_arr) {
                String val = map.get(name.toString());
                ds.write((prefix + boundary + end).getBytes("UTF-8"));
                ds.write(("Content-Disposition: form-data;name=\"" + name.toString() + "\"" + end).getBytes("UTF-8"));
                ds.write(("Content-Type: text/plain;charset=UTF-8" + end).getBytes("UTF-8"));
                ds.write((end).getBytes("UTF-8"));
                ds.write((val + end).getBytes("UTF-8"));
            }
        } catch (IOException | AuthFailureError e) {
            e.printStackTrace();
        }
    }

}
