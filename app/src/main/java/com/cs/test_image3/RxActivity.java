package com.cs.test_image3;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by chenshuai on 2016/12/7.
 */

public class RxActivity extends Activity implements View.OnClickListener {
    private Button mBtnRxjava;
    private ImageView mImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_rxjava);
        initView();
    }

    private void initView() {
        mBtnRxjava = (Button) findViewById(R.id.btn_rxjava);
        mImage = (ImageView) findViewById(R.id.image);
        mBtnRxjava.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_rxjava:
                break;
        }
    }
}
