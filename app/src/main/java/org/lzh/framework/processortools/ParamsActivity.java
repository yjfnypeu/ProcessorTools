package org.lzh.framework.processortools;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.lzh.processor.annoapi.Field;
import com.lzh.processor.annoapi.FieldType;
import com.lzh.processor.annoapi.Params;

import butterknife.Bind;
import butterknife.ButterKnife;

@Params(fields = {
        @Field(/* 必填 传参名*/name = "userinfo",
                /* 必填 传参类型：可使用基本数据类型或实现了Serializable接口的类*/type = UserInfo.class,
                /* 非必填 生成类中文档。帮助在调用时理解该参数起何作用*/doc = "用户名",
                /* 非必填 默认值*/defValue = "",
                /* 非必填 默认Serializable，另支持array数组，List,Set集合类型，与type结合使用*/fieldType = FieldType.Serializable),
})
public class ParamsActivity extends AppCompatActivity {

    @Bind(R.id.username)
    TextView username;
    @Bind(R.id.password)
    TextView password;

    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_params);
        ButterKnife.bind(this);
        // 获取传参
        userInfo = ParamsActivity_Dispatcher.getData(getIntent()).getUserinfo();
        username.setText(userInfo.getUsername());
        password.setText(userInfo.getPassword());

    }
}
