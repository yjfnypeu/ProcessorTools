package org.lzh.framework.processortools;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lzh.processor.annoapi.Params;
// 只添加@Params实现空跳转
@Params
public class EmptyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}
