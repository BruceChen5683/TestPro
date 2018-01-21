package cn.ws.sz.activity;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import cn.ws.sz.R;
import cn.ws.sz.utils.Eyes;

public class AfterPayActivity extends AppCompatActivity {

    private LinearLayout returnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_pay);

        Eyes.setStatusBarColor(this, ContextCompat.getColor(this,R.color.pay_success_title_bg_color));

        initView();
    }

    private void initView() {
        returnBack = (LinearLayout) findViewById(R.id.returnBack);
        returnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
