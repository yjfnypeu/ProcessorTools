package org.lzh.framework.processortools;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.lzh.processor.annoapi.Field;
import com.lzh.processor.annoapi.Params;

/**
 * @author Administrator
 */
@Params(fields = {
        @Field(name = "password", type = String.class, doc = "用户密码")
})
public class SubFragment extends TestFragment {
    public static final String TAG = SubFragment.class.getCanonicalName();
    SubFragment_Builder.RequestData requestData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestData = SubFragment_Builder.getData(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        password.setText(requestData.getPassword());
    }
}
