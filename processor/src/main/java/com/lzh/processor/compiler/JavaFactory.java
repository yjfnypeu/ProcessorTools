package com.lzh.processor.compiler;

import com.lzh.processor.annoapi.FieldType;
import com.lzh.processor.data.FieldData;
import com.lzh.processor.reflect.Reflect;
import com.lzh.processor.util.FileLog;
import com.lzh.processor.util.StringUtils;
import com.lzh.processor.util.UtilMgr;
import com.lzh.processor.util.javapoet.FieldSpec;
import com.lzh.processor.util.javapoet.JavaFile;
import com.lzh.processor.util.javapoet.MethodSpec;
import com.lzh.processor.util.javapoet.TypeName;
import com.lzh.processor.util.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

/**
 * @author Administrator
 */
public class JavaFactory {
    private final static String REQUEST_DATA_CLASS = "RequestData";

    private final static String REQUEST_DATA_FIELD_NAME = "data";
    private final static String REQUEST_CODE_FIELD_NAME = "requestCode";
    private final static String TAG_FIELD = "TAG";

    private final static String CREATE_METHOD = "create";
    private final static String START_METHOD = "start";
    private final static String CREATE_INTENT = "createIntent";
    private final static String GETDATA_METHOD = "getData";

    private final static String ACTIVITY_NAME = "android.app.Activity";
    private final static String CONTEXT_NAME = "android.content.Context";
    private final static String FRAGMENT_NAME = "android.app.Fragment";
    private final static String V4FRAGMENT_NAME = "android.support.v4.app.Fragment";
    private final static String INTENT_NAME = "android.content.Intent";

    private final static String SUFFIX = "_Dispatcher";
    private TypeName generateClassName = null;
    private String pkgName = null;
    private ElementParser parser;

    public JavaFactory(ElementParser parser) {
        this.parser = parser;
    }

    public void generateCode() throws IOException {
        pkgName = getPkgName();
        TypeSpec.Builder typeBuilder = generateTypeBuilder();
        // add field
        addFields(typeBuilder);
        // add class RequestData
        typeBuilder.addType(generateRequestData());
        // create private constructor method
        typeBuilder.addMethod(createPrivateConstructor());
        // add create method
        typeBuilder.addMethod(createMethod());
        // add setter method
        addParamsSetMethod(typeBuilder);
        // add request code method
        addRequestCodeMethod(typeBuilder);
        // add create intent method
        addCreateIntentMethod(typeBuilder);
        // add start activity method
        addStartMethod(typeBuilder);
        // add get request data method
        typeBuilder.addMethod(createGetDataMethod());

        JavaFile.Builder javaBuilder = JavaFile.builder(pkgName, typeBuilder.build());
        javaBuilder.addFileComment("The file is auto-generate by processorTool,do not modify!");
        javaBuilder.build().writeTo(UtilMgr.getMgr().getFiler());

    }

    private MethodSpec createGetDataMethod() {
        TypeName intent = getTypeName(INTENT_NAME);
        TypeName requestData = getTypeName(REQUEST_DATA_CLASS);
        String paramsName = "data";
        return MethodSpec.methodBuilder(GETDATA_METHOD)
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .returns(getTypeName(REQUEST_DATA_CLASS))
                .addParameter(intent, paramsName)
                .beginControlFlow("if (data == null || data.getSerializableExtra(TAG) == null)")
                .addStatement("return new $T()", requestData)
                .endControlFlow()
                .beginControlFlow("else")
                .addStatement("return ($T) data.getSerializableExtra(TAG)", requestData)
                .endControlFlow()
                .build();
    }

    private void addCreateIntentMethod(TypeSpec.Builder typeBuilder) {
        TypeName intent = getTypeName(INTENT_NAME);
        MethodSpec builder = MethodSpec.methodBuilder(CREATE_INTENT)
                .addModifiers(Modifier.PRIVATE)
                .returns(getTypeName(INTENT_NAME))
                .addParameter(getTypeName(CONTEXT_NAME), "context")
                .addStatement("$T intent = new $T($L,$L.class)",
                        intent, intent, "context",parser.getElement().getSimpleName())
                .addStatement("intent.putExtra($L,$L)",TAG_FIELD,REQUEST_DATA_FIELD_NAME)
                .addStatement("return intent")
                .build();

        typeBuilder.addMethod(builder);
    }

    private void addStartMethod(TypeSpec.Builder typeBuilder) {
        String paramsName = "target";
        MethodSpec.Builder startByActivity = createStartMethodBuilder(ACTIVITY_NAME, paramsName,paramsName);
        typeBuilder.addMethod(
                startByActivity.addStatement("target.startActivityForResult(intent,$L)", REQUEST_CODE_FIELD_NAME)
                        .addStatement("return this")
                        .build()
        );
        MethodSpec.Builder startByContext = createStartMethodBuilder(CONTEXT_NAME,paramsName,paramsName);
        typeBuilder.addMethod(
                startByContext.addStatement("target.startActivity(intent)")
                        .addStatement("return this")
                        .build()
        );
        MethodSpec.Builder startByFragment = createStartMethodBuilder(FRAGMENT_NAME, paramsName, paramsName + ".getActivity()");
        typeBuilder.addMethod(
                startByFragment.addStatement("target.startActivityForResult(intent,$L)",REQUEST_CODE_FIELD_NAME)
                        .addStatement("return this")
                        .build()
        );
        MethodSpec.Builder startByV4Fragment = createStartMethodBuilder(V4FRAGMENT_NAME, paramsName, paramsName + ".getActivity()");
        typeBuilder.addMethod(
                startByV4Fragment.addStatement("target.startActivityForResult(intent,$L)",REQUEST_CODE_FIELD_NAME)
                        .addStatement("return this")
                        .build()
        );


    }

    private MethodSpec.Builder createStartMethodBuilder(String paramsType,String paramsName, String context) {
        TypeName intent = getTypeName(INTENT_NAME);
        return MethodSpec.methodBuilder(START_METHOD)
                .addModifiers(Modifier.PUBLIC)
                .returns(generateClassName)
                .addParameter(getTypeName(paramsType),paramsName)
                .addStatement("$T intent = $L($L)",intent,CREATE_INTENT,context);
    }

    private void addRequestCodeMethod(TypeSpec.Builder typeBuilder) {
        MethodSpec build = MethodSpec.methodBuilder(REQUEST_CODE_FIELD_NAME)
                .addModifiers(Modifier.PUBLIC)
                .returns(generateClassName)
                .addParameter(TypeName.INT, REQUEST_CODE_FIELD_NAME)
                .addStatement("this.$L = $L", REQUEST_CODE_FIELD_NAME, REQUEST_CODE_FIELD_NAME)
                .addStatement("return this")
                .build();
        typeBuilder.addMethod(build);
    }

    private void addParamsSetMethod(TypeSpec.Builder typeBuilder) {
        List<FieldData> fieldList = parser.getFieldList();
        for (int i = 0; i < fieldList.size(); i++) {
            FieldData data = fieldList.get(i);
            typeBuilder.addMethod(createSetMethod(data));
        }
    }

    private MethodSpec createSetMethod(FieldData data) {
        String setMethodName = StringUtils.getSetMethodName(data.getName());
        return MethodSpec.methodBuilder(setMethodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(generateClassName)
                .addParameter(getTypeName(data.getFieldType(),data.getType()),data.getName())
                .addStatement("this.$L.$L($L)",REQUEST_DATA_FIELD_NAME,setMethodName,data.getName())
                .addStatement("return this")
                .addJavadoc(data.getDoc())
                .build();
    }

    /**
     * generate static create method
     */
    private MethodSpec createMethod() {
        return MethodSpec.methodBuilder(CREATE_METHOD)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addStatement("$T instance = new $T()", generateClassName, generateClassName)
                .addStatement("instance.$L = new $L()", REQUEST_DATA_FIELD_NAME, REQUEST_DATA_CLASS)
                .addStatement("return instance")
                .returns(generateClassName)
                .build();
    }

    private void addFields(TypeSpec.Builder typeBuilder) {
        // add tag
        typeBuilder.addField(FieldSpec.builder(TypeName.get(String.class), TAG_FIELD, Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
                .initializer("$S",parser.getClzName()).build());
        // add RequestData filed
        typeBuilder.addField(FieldSpec.builder(getTypeName(REQUEST_DATA_CLASS),REQUEST_DATA_FIELD_NAME,Modifier.PRIVATE).build());
        // add request code field
        typeBuilder.addField(FieldSpec.builder(TypeName.INT, REQUEST_CODE_FIELD_NAME,Modifier.PRIVATE)
                .initializer("-1")
                .build());
    }

    /**
     * create private constructor method
     */
    private MethodSpec createPrivateConstructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();
    }

    /**
     * create inner class RequestData
     */
    private TypeSpec generateRequestData() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(REQUEST_DATA_CLASS)
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .addSuperinterface(TypeName.get(Serializable.class));
        List<FieldData> fieldList = parser.getFieldList();

        for (int i = 0; i < fieldList.size(); i++) {
            FieldData data = fieldList.get(i);
            builder.addField(createField(data));
            builder.addMethod(createSetRequestBuilder(data));
            builder.addMethod(createGetRequestBuilder(data));
        }

        return builder.build();
    }

    private MethodSpec createGetRequestBuilder(FieldData data) {
        String getMethodName = StringUtils.getGetMethodName(data.getName());
        return MethodSpec.methodBuilder(getMethodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(getTypeName(data.getFieldType(), data.getType()))
                .addStatement("return this.$L", data.getName())
                .addJavadoc(data.getDoc())
                .build();
    }

    private FieldSpec createField(FieldData data) {
        FieldSpec.Builder builder = FieldSpec.builder(getTypeName(data.getFieldType(), data.getType()), data.getName(), Modifier.PRIVATE)
                .addJavadoc(data.getDoc());
        if (!StringUtils.isEmpty(data.getDefValue())) {
            builder.initializer(
                    (data.getType().equals(TypeName.get(String.class)) ? "$S" : "$L"),
                    data.getDefValue());
        }

        return builder.build();
    }

    private MethodSpec createSetRequestBuilder(FieldData data) {
        String setMethodName = StringUtils.getSetMethodName(data.getName());
        return MethodSpec.methodBuilder(setMethodName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(getTypeName(data.getFieldType(), data.getType()), data.getName())
                .returns(getTypeName(REQUEST_DATA_CLASS))
                .addStatement("this.$L = $L", data.getName(), data.getName())
                .addStatement("return this")
                .addJavadoc(data.getDoc())
                .build();

    }

    /**
     * create generate class builder
     */
    private TypeSpec.Builder generateTypeBuilder() {
        String clzName = parser.getClzName();
        clzName = clzName + SUFFIX;
        generateClassName = getTypeName(getName(clzName));
        return TypeSpec.classBuilder(clzName)
                .addModifiers(Modifier.PUBLIC);
    }

    public String getPkgName() {
        PackageElement pkgElement = UtilMgr.getMgr().getElementUtils().getPackageOf(parser.getElement());
        return pkgElement.isUnnamed() ? "":pkgElement.getQualifiedName().toString();
    }

    TypeName getTypeName(TypeElement element) {
        return TypeName.get(element.asType());
    }

    TypeName getTypeName (String clzName) {
        return Reflect.on(TypeName.class).create(clzName).get();
    }

    String getName (String clzSimpleName) {
        String name = StringUtils.isEmpty(pkgName)? clzSimpleName:pkgName + "." + clzSimpleName;
        try {
            FileLog.print("getName:" + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    TypeName getTypeName(FieldType parent,TypeName child) {
        if (parent.equals(FieldType.array)) {
            return getTypeName(child.toString() + "[]");
        } else if (parent.equals(FieldType.list)) {
            return getTypeName(List.class.getCanonicalName() + "<" + child.toString() + ">");
        } else if (parent.equals(FieldType.set)) {
            return getTypeName(Set.class.getCanonicalName() + "<" + child.toString() + ">");
        } else {
            return child;
        }
    }

}
