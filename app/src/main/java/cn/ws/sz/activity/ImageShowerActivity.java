package cn.ws.sz.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

import cn.ws.sz.R;
import cn.ws.sz.utils.CommonUtils;

public class ImageShowerActivity extends AppCompatActivity {

	private ImageView iv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_shower);

		Bundle bundle = getIntent().getExtras();
		String image = (String) bundle.get("image");
		iv = (ImageView) findViewById(R.id.iv);
		CommonUtils.setImageView(image,iv);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}
}
