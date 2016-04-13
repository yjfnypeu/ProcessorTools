package com.lzh.processor.compiler;

import com.lzh.processor.annoapi.Field;
import com.lzh.processor.annoapi.Params;
import com.lzh.processor.data.FieldData;
import com.lzh.processor.util.UtilMgr;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

/**
 * @author Administrator
 */
public class ElementParser {

    private static final String ACT_NAME = "android.app.Activity";

    /**
     * class name use @Params
     */
    private String clzName;
    /**
     * annotations filed
     */
    private List<FieldData> fieldList;
    /**
     * use @Params annotation class
     */
    private TypeElement element;

    public String getClzName() {
        return clzName;
    }

    public List<FieldData> getFieldList() {
        return fieldList;
    }

    public TypeElement getElement() {
        return element;
    }

    public static ElementParser createParser(TypeElement element) {
        ElementParser parser = new ElementParser();
        parser.element = element;
        parser.parse();
        return parser;
    }

    void parse() {
        checkIsTargetName(ACT_NAME);
        clzName = element.getSimpleName().toString();
        parseField();
    }

    private void parseField() {
        Params annotation = element.getAnnotation(Params.class);
        Field[] fields = annotation.fields();
        fieldList = new ArrayList<>();
        for (int i = 0; i < (fields == null ? 0 : fields.length); i++) {
            Field field = fields[i];
            FieldData data = new FieldData();
            data.setDoc(field.doc());
            data.setName(field.name());
            data.setType(getClzType(field));
            data.setFieldType(field.fieldType());
            data.setDefValue(field.defValue());
            fieldList.add(data);
        }
    }

    private TypeElement getClzType (Field field) {
        TypeElement type;
        String clzName;
        try {
            Class<?> clazz = field.type();
            clzName = clazz.getCanonicalName();
            type = UtilMgr.getMgr().getElementUtils().getTypeElement(clzName);
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            type = (TypeElement) classTypeMirror.asElement();
        }
        return type;
    }

    private void checkIsTargetName(String target) {
        TypeElement type = element;
        while (true) {
            if (type == null) {
                throw new IllegalArgumentException(String.format("class %s is not extends from %s",element.getQualifiedName(),target));
            } else if (target.equals(type.getQualifiedName().toString())) {
                return;
            }
            type = getParentClass(type);
        }
    }

    TypeElement getParentClass (TypeElement child) {
        return (TypeElement) UtilMgr.getMgr().getTypeUtils().asElement(child.getSuperclass());
    }
}
