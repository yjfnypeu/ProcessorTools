package org.lzh.framework.processortools;

import android.os.Bundle;

import com.lzh.processor.annoapi.Field;
import com.lzh.processor.annoapi.Params;

@Params(fields = {
        @Field(name = "password",type = String.class)
})
public class SubActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SubActivity_Dispatcher.RequestData data = SubActivity_Dispatcher.getData(getIntent());
        password.setText(data.getPassword());
    }
}
