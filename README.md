# ProcessorTools
编译时注解库。为Activity添加注解。简化页面跳转传值逻辑。
首先。给你的Activity添加@Params注解：

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

下面介绍一下Field注解含有的以下五个参数：
 - name	:	(必填)要传递的参数名。如上的username、password
 - type		:	(必填)传递参数的类型，支持基本数据类型及实现了Serializable接口的类
 - doc		:	(非必填)当前传递参数的注释。页面跳转时设置传递参数值时可得到提示
 - defValue:	(非必填)当前传递参数的默认值。
 - FieldType:(非必填)有四种类型，Serializable、list、array、set;默认使用Serializable。与type参数结合使用。
