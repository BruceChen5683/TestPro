package cn.ws.sz.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.ws.sz.R;

public class ServiceActivity extends AppCompatActivity {
    private TextView tvTitle;
    private LinearLayout llReturnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        tvTitle= (TextView) findViewById(R.id.title_value);
        tvTitle.setText("联系客服");
        llReturnBack = (LinearLayout) findViewById(R.id.returnBack);
        llReturnBack.setVisibility(View.VISIBLE);

        llReturnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceActivity.this.finish();
            }
        });
    }
}
