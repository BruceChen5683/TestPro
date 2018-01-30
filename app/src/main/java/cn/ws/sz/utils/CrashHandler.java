package cn.ws.sz.utils;

/**
 * Created by BattleCall on 2018/1/30.
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
	private static final String TAG = CrashHandler.class.getSimpleName();

	private static CrashHandler instance; // 单例模式

	private Context context; // 程序Context对象
	private Thread.UncaughtExceptionHandler defalutHandler; // 系统默认的UncaughtException处理类
	private DateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd_HH-mm-ss.SSS", Locale.CHINA);
	private Map<String, String> info = new HashMap<String, String>();// 用来存储设备信息和异常信息
	private CrashHandler() {

	}

	/**
	 * 获取CrashHandler实例
	 *
	 * @return CrashHandler
	 */
	public static CrashHandler getInstance() {
		if (instance == null) {
			synchronized (CrashHandler.class) {
				if (instance == null) {
					instance = new CrashHandler();
				}
			}
		}
		return instance;
	}

	/**
	 * 异常处理初始化
	 *
	 * @param context
	 */
	public void init(Context context) {
		this.context = context;
		// 获取系统默认的UncaughtException处理器
		defalutHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

		// 自定义错误处理
		boolean res = handleException(ex);
		if (!res && defalutHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			defalutHandler.uncaughtException(thread, ex);

		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 *
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return false;
		}
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				ex.printStackTrace();
				String err = "[" + ex.getMessage() + "]";
				Toast.makeText(context, "程序出现异常." + err, Toast.LENGTH_LONG)
						.show();
				Looper.loop();
			}

		}.start();

		// 收集设备参数信息
		collectDeviceInfo(context);
		// 保存日志文件
		saveCrashInfo2File(ex);
		return true;
	}

	private String saveCrashInfo2File(Throwable ex) {
		Log.d(TAG, "saveCrashInfo2File: ");
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : info.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\r\n");
		}
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		ex.printStackTrace(pw);
		Throwable cause = ex.getCause();
		// 循环着把所有的异常信息写入writer中
		while (cause != null) {
			cause.printStackTrace(pw);
			cause = cause.getCause();
		}
		pw.close();// 记得关闭
		String result = writer.toString();
		sb.append(result);
		// 保存文件
		long timetamp = System.currentTimeMillis();
		String time = format.format(new Date());
		String fileName = "crash-" + time + "-" + timetamp + ".log";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			try {
				File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Constant.DIR_WS_CRASH);
				Log.i("CrashHandler", dir.toString());
				if (!dir.exists()) dir.mkdir();
				File crashFile = new File(dir, fileName);
				FileOutputStream fos = new FileOutputStream(crashFile);

				Log.d(TAG, "saveCrashInfo2File:crashFile.getAbsolutePath()  "+crashFile.getAbsolutePath());
				fos.write(sb.toString().getBytes());
				fos.close();

//				scanFile(context,crashFile.getAbsolutePath());
				scanFile(context,crashFile.getAbsolutePath());

//				MediaScannerConnection.scanFile(context, new String[] { dir.getAbsolutePath() }, null, null);

				return fileName;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}



	/**
	 * 收集设备参数信息
	 *
	 * @param context
	 */
	public void collectDeviceInfo(Context context) {
		Log.d(TAG, "collectDeviceInfo: ");
		try {
			PackageManager pm = context.getPackageManager();// 获得包管理器
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);// 得到该应用的信息，即主Activity
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				info.put("versionName", versionName);
				info.put("versionCode", versionCode);
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		Field[] fields = Build.class.getDeclaredFields();// 反射机制
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				info.put(field.getName(), field.get("").toString());
				Log.d(TAG, field.getName() + ":" + field.get(""));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 通知媒体库更新文件夹
	 * @param context
	 * @param filePath 文件夹
	 *
	 * */
//	public void scanFile(Context context, String filePath) {
//		Intent scanIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
//		scanIntent.setData(Uri.fromFile(new File(filePath)));
//		context.sendBroadcast(scanIntent);
//	}

	public void scanFile(Context context,String filePath){
		Uri data = Uri.parse("file://"+filePath);
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
	}
}

