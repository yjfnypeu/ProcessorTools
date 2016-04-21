package org.lzh.framework.processortools;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.lzh.processor.annoapi.Field;
import com.lzh.processor.annoapi.Params;

import butterknife.Bind;
import butterknife.ButterKnife;

@Params(fields = {
        @Field(name = "username", type = String.class)
})
public class ParentActivity extends Activity {

    @Bind(R.id.username)
    Button username;
    @Bind(R.id.password)
    Button password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        ButterKnife.bind(this);

        ParentActivity_Dispatcher.RequestData data = ParentActivity_Dispatcher.getData(getIntent());
        username.setText(data.getUsername());

    }
}
