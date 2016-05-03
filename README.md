# ProcessorTools
编译时注解库。为Activity添加注解。简化页面跳转传值逻辑。

如何引进processorTool:

- 在工程的根目录的build.gradle中加入：
```
classpath 'com.neenbedankt.gradle.plugins:android-apt:1.4'
```
- 在要使用的工程module中添加应用apt插件

```
apply plugin: 'com.neenbedankt.android-apt'
```
- 加入引用：

```
dependencies {
	...
    compile 'org.lzh.compiler.lib:processortool-api:0.2'
    apt 'org.lzh.compiler.lib:processortool-compiler:0.2'
}
```

具体使用姿势，首先。给你的Activity添加@Params注解：

```
@Params
public class EmptyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}
```
这是Activity跳转无参数传递时的用法，只需要对Activity添加一个@Params注解即可。然后在跳转时。调用一行代码即可：

```
EmptyActivity_Dispatcher.create().start(MainActivity.this);
```
EmptyActivity_Dispatcher类即时EmptyActivity添加了注解@Params之后生成的java类，非常方便直观。

如果是跳转时需要传递参数的情况。还有个@Field注解。此注解结合@Params一起使用，如：

```
@Params(fields = {
        @Field(name = "username",type = String.class,),
        @Field(name = "password",type = String.class)
})
public class ParamsActivity extends AppCompatActivity {
	...
}
```
传递几个参数。就在Params注解中添加几个Field注解即可。

然后是跳转时的代码：

```
ParamsActivity_Dispatcher.create()
                .setUsername("123456")
                .setPassword("111111")
                .requestCode(-1)
                .start(this);
```
同样的。生成了以后缀名为_Dispatcher的类。链式调用设置属性值。

start方法也支持使用四种类型的参数进行跳转,Activity,Context,Fragment,V4Fragment：

跳转之后肯定是需要取值啊。框架也对取值进行了封装处理：

```
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
```
调用生成类ParamsActivity_Dispatcher的getData方法。即可拿到装有全部参数的类RequestData的实例。拿到数据后任性的给界面绑定数据吧！

- Fragment的支持

```
@Params(fields = {
        @Field(name = "username", type = String.class, doc = "用户名")
})
public class TestFragment extends Fragment {
	...
	// 传递参数数据类
	TestFragment_Builder.RequestData requestData;
	@Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取传递参数
        requestData = TestFragment_Builder.getData(this);
    }
}
```
对Fragment使用此注解主要用于创建Fragment实例：

```
TestFragment build = TestFragment_Builder.create().setUsername
		("TestFragment pass : username").build();
getFragmentManager().beginTransaction()
                .replace(R.id.frag_layout,build)
                .commit();
```
对于多继承关系的Activity：

```
// 界面两个控件。ParentActivity负责传递username值并设置
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
		// 获取传递的username并设置
        ParentActivity_Dispatcher.RequestData data = ParentActivity_Dispatcher.getData(getIntent());
        username.setText(data.getUsername());

    }
}
```

```
// 继承的Activity所使用的@Filed注解中。name名不得与被继承的类ParentActivity中使用的@Field的name名重复。
@Params(fields = {
        @Field(name = "password",type = String.class)
})
public class SubActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取子Activity传递的password值并更新界面
        SubActivity_Dispatcher.RequestData data = SubActivity_Dispatcher.getData(getIntent());
        password.setText(data.getPassword());
    }
}
```

界面跳转时，子类SubActivity跳转可设置的属性会将继承的Activity中设置过的值都加入进来：

```
SubActivity_Dispatcher.create()
				.setUsername("SubActivity pass : username")// 父类传参
                .setPassword("SubActivity pass : password")// 子类传参
                .start(this);
```

fragment多继承时也是一样的

```
// 父类Fragment只传递username
@Params(fields = {
        @Field(name = "username", type = String.class, doc = "用户名")
})
public class TestFragment extends Fragment {

    @Bind(R.id.username)
    Button username;
    @Bind(R.id.password)
    Button password;

    TestFragment_Builder.RequestData requestData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestData = TestFragment_Builder.getData(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_parent, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        username.setText(requestData.getUsername());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
```

```
// 子类传递参数password
@Params(fields = {
        @Field(name = "password", type = String.class, doc = "用户密码")
})
public class SubFragment extends TestFragment {

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
```

跳转子Fragment传参：

```
SubFragment build = SubFragment_Builder.create()
				.setUsername("SubFragment pass : username")
                .setPassword("SubFragment pass : password")
                .build();
getFragmentManager().beginTransaction()
                .replace(R.id.frag_layout,build)
                .commit();
```

对于某些时候。可能不需要设置值后立即来跳转。比较通知栏的pendingIntent：可以设置值之后。拿到已设置值后的Bundle或者Intent，方便在各种场景下使用。

```
Intent intent = ParamsActivity_Dispatcher.create()
                .setUsername("123456")
                .setPassword("654321")
                .createIntent(MainActivity.this);
Bundle bundle = SubFragment_Builder.create().setUsername("username")
                .setPassword("password")
                .createBundle();
```

下面介绍一下Field注解含有的以下五个参数：
 - name	:	(必填)要传递的参数名。如上的username、password
 - type		:	(必填)传递参数的类型，支持基本数据类型及实现了Serializable接口的类
 - doc		:	(非必填)当前传递参数的注释。页面跳转时设置传递参数值时可得到提示
 - defValue:	(非必填)当前传递参数的默认值。
 - FieldType:(非必填)有四种类型，Serializable、list、array、set;默认使用Serializable。与type参数结合使用。
