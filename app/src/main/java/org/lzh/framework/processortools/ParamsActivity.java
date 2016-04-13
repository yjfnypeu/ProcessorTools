package org.lzh.framework.processortools;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.lzh.processor.annoapi.Field;
import com.lzh.processor.annoapi.Params;

import butterknife.Bind;
import butterknife.ButterKnife;

@Params(fields = {
        @Field(name = "username",type = String.class),
        @Field(name = "password",type = String.class)
})
public class ParamsActivity extends AppCompatActivity {

    @Bind(R.id.username)
    TextView username;
    @Bind(R.id.password)
    TextView password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_params);
        ButterKnife.bind(this);
        // 获取传参
        ParamsActivity_Dispatcher.RequestData data = ParamsActivity_Dispatcher.getData(getIntent());
        username.setText(data.getUsername());
        password.setText(data.getPassword());

    }
}
