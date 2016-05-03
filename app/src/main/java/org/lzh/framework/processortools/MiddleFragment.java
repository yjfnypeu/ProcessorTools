package org.lzh.framework.processortools;

import com.lzh.processor.annoapi.Field;
import com.lzh.processor.annoapi.Params;

/**
 * @author Administrator
 */
@Params(fields = {
        @Field(name = "username", type = String.class, doc = "用户名")
})
public class MiddleFragment extends TestFragment {
}
