package com.lzh.processor.data;

import com.lzh.processor.annoapi.FieldType;

import javax.lang.model.element.TypeElement;

/**
 * @author Administrator
 */
public class FieldData {

    private String name;
    private String doc;
    private TypeElement type;
    private FieldType fieldType;
    private String defValue;

    public String getDefValue() {
        return defValue;
    }

    public void setDefValue(String defValue) {
        this.defValue = defValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public TypeElement getType() {
        return type;
    }

    public void setType(TypeElement type) {
        this.type = type;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

}
