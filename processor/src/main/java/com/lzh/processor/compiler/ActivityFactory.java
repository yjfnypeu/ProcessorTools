package com.lzh.processor.compiler;

import com.lzh.processor.data.FieldData;
import com.lzh.processor.util.StringUtils;
import com.lzh.processor.util.javapoet.FieldSpec;
import com.lzh.processor.util.javapoet.MethodSpec;
import com.lzh.processor.util.javapoet.TypeName;
import com.lzh.processor.util.javapoet.TypeSpec;

import java.io.IOException;
import java.util.List;

import javax.lang.model.element.Modifier;

/**
 * @author Administrator
 */
public class ActivityFactory extends FileFactory{

    private final static String REQUEST_CODE_FIELD_NAME = "requestCode";

    private final static String START_METHOD = "start";
    private final static String CREATE_INTENT = "createIntent";
    private final static String GETDATA_METHOD = "getData";

    private final static String ACTIVITY_NAME = "android.app.Activity";
    private final static String CONTEXT_NAME = "android.content.Context";
    private final static String FRAGMENT_NAME = "android.app.Fragment";
    private final static String V4FRAGMENT_NAME = "android.support.v4.app.Fragment";
    private final static String INTENT_NAME = "android.content.Intent";

    private final static String SUFFIX = "_Dispatcher";

    public ActivityFactory(ElementParser parser) {
        super(parser);
    }

    @Override
    String getSuffix() {
        return SUFFIX;
    }

    public void generateCode() throws IOException {
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

        build(typeBuilder);

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
        MethodSpec.Builder builder = MethodSpec.methodBuilder(CREATE_INTENT)
                .addModifiers(Modifier.PUBLIC)
                .returns(getTypeName(INTENT_NAME))
                .addParameter(getTypeName(CONTEXT_NAME), "context")
                .addStatement("$T intent = new $T($L,$L.class)",
                        intent, intent, "context", parser.getElement().getSimpleName());

        if (generateParentClassName != null) {
            builder.addStatement("$T parentIntent = $L.$L(context)",intent,PARENT_CLASS_FIELD_NAME,CREATE_INTENT);
            builder.addStatement("intent.putExtras(parentIntent)");
        }

        builder.addStatement("intent.putExtra($L,$L)",TAG_FIELD,REQUEST_DATA_FIELD_NAME)
                .addStatement("return intent");

//        MethodSpec builder = MethodSpec.methodBuilder(CREATE_INTENT)
//                .addModifiers(Modifier.PRIVATE)
//                .returns(getTypeName(INTENT_NAME))
//                .addParameter(getTypeName(CONTEXT_NAME), "context")
//                .addStatement("$T intent = new $T($L,$L.class)",
//                        intent, intent, "context",parser.getElement().getSimpleName())
//                .addStatement("intent.putExtra($L,$L)",TAG_FIELD,REQUEST_DATA_FIELD_NAME)
//                .addStatement("return intent")
//                .build();

        typeBuilder.addMethod(builder.build());
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





    private void addFields(TypeSpec.Builder typeBuilder) {
        // add tag
        typeBuilder.addField(FieldSpec.builder(TypeName.get(String.class), TAG_FIELD, Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
                .initializer("$S", parser.getClzName()).build());
        // add RequestData filed
        typeBuilder.addField(FieldSpec.builder(getTypeName(REQUEST_DATA_CLASS), REQUEST_DATA_FIELD_NAME, Modifier.PRIVATE).build());
        // add request code field
        typeBuilder.addField(FieldSpec.builder(TypeName.INT, REQUEST_CODE_FIELD_NAME,Modifier.PRIVATE)
                .initializer("-1")
                .build());

        if (generateParentClassName != null) {
            typeBuilder.addField(FieldSpec.builder(generateParentClassName,PARENT_CLASS_FIELD_NAME,Modifier.PRIVATE).build());
        }
    }

    /**
     * create generate class builder
     */
    private TypeSpec.Builder generateTypeBuilder() {
        String clzName = parser.getClzName();
        clzName = clzName + SUFFIX;
        return TypeSpec.classBuilder(clzName)
                .addModifiers(Modifier.PUBLIC);
    }



}
