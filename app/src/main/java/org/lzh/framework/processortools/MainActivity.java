package org.lzh.framework.processortools;

import android.app.Activity;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.toEmptyParamsActivity)
    void toEmptyActivity() {
        // start跳转支持以Activity/Context/Fragment/V4Fragment进行跳转
        EmptyActivity_Dispatcher.create().requestCode(-1).start(this);
    }

    @OnClick(R.id.toParamsActivity)
    void toParamaActivity () {
        ParamsActivity_Dispatcher.create()
                .setUserinfo(new UserInfo().setUsername("豪大爷").setPassword("你猜"))
                .start(this);
    }
}
