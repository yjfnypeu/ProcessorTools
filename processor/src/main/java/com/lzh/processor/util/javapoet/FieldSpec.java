/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lzh.processor.util.javapoet;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;

import static com.lzh.processor.util.javapoet.Util.checkArgument;
import static com.lzh.processor.util.javapoet.Util.checkNotNull;
import static com.lzh.processor.util.javapoet.Util.checkState;

/** A generated field declaration. */
public final class FieldSpec {
  public final TypeName type;
  public final String name;
  public final com.lzh.processor.util.javapoet.CodeBlock javadoc;
  public final List<com.lzh.processor.util.javapoet.AnnotationSpec> annotations;
  public final Set<Modifier> modifiers;
  public final com.lzh.processor.util.javapoet.CodeBlock initializer;

  private FieldSpec(Builder builder) {
    this.type = checkNotNull(builder.type, "type == null");
    this.name = checkNotNull(builder.name, "name == null");
    this.javadoc = builder.javadoc.build();
    this.annotations = Util.immutableList(builder.annotations);
    this.modifiers = Util.immutableSet(builder.modifiers);
    this.initializer = (builder.initializer == null)
        ? com.lzh.processor.util.javapoet.CodeBlock.builder().build()
        : builder.initializer;
  }

  public boolean hasModifier(Modifier modifier) {
    return modifiers.contains(modifier);
  }

  void emit(com.lzh.processor.util.javapoet.CodeWriter codeWriter, Set<Modifier> implicitModifiers) throws IOException {
    codeWriter.emitJavadoc(javadoc);
    codeWriter.emitAnnotations(annotations, false);
    codeWriter.emitModifiers(modifiers, implicitModifiers);
    codeWriter.emit("$T $L", type, name);
    if (!initializer.isEmpty()) {
      codeWriter.emit(" = ");
      codeWriter.emit(initializer);
    }
    codeWriter.emit(";\n");
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    if (getClass() != o.getClass()) return false;
    return toString().equals(o.toString());
  }

  @Override public int hashCode() {
    return toString().hashCode();
  }

  @Override public String toString() {
    StringWriter out = new StringWriter();
    try {
      com.lzh.processor.util.javapoet.CodeWriter codeWriter = new com.lzh.processor.util.javapoet.CodeWriter(out);
      emit(codeWriter, Collections.<Modifier>emptySet());
      return out.toString();
    } catch (IOException e) {
      throw new AssertionError();
    }
  }

  public static Builder builder(TypeName type, String name, Modifier... modifiers) {
    checkNotNull(type, "type == null");
    checkArgument(SourceVersion.isName(name), "not a valid name: %s", name);
    return new Builder(type, name)
        .addModifiers(modifiers);
  }

  public static Builder builder(Type type, String name, Modifier... modifiers) {
    return builder(TypeName.get(type), name, modifiers);
  }

  public Builder toBuilder() {
    Builder builder = new Builder(type, name);
    builder.javadoc.add(javadoc);
    builder.annotations.addAll(annotations);
    builder.modifiers.addAll(modifiers);
    builder.initializer = initializer.isEmpty() ? null : initializer;
    return builder;
  }

  public static final class Builder {
    private final TypeName type;
    private final String name;

    private final com.lzh.processor.util.javapoet.CodeBlock.Builder javadoc = com.lzh.processor.util.javapoet.CodeBlock.builder();
    private final List<com.lzh.processor.util.javapoet.AnnotationSpec> annotations = new ArrayList<>();
    private final List<Modifier> modifiers = new ArrayList<>();
    private com.lzh.processor.util.javapoet.CodeBlock initializer = null;

    private Builder(TypeName type, String name) {
      this.type = type;
      this.name = name;
    }

    public Builder addJavadoc(String format, Object... args) {
      javadoc.add(format, args);
      return this;
    }

    public Builder addAnnotations(Iterable<com.lzh.processor.util.javapoet.AnnotationSpec> annotationSpecs) {
      checkArgument(annotationSpecs != null, "annotationSpecs == null");
      for (com.lzh.processor.util.javapoet.AnnotationSpec annotationSpec : annotationSpecs) {
        this.annotations.add(annotationSpec);
      }
      return this;
    }

    public Builder addAnnotation(com.lzh.processor.util.javapoet.AnnotationSpec annotationSpec) {
      this.annotations.add(annotationSpec);
      return this;
    }

    public Builder addAnnotation(com.lzh.processor.util.javapoet.ClassName annotation) {
      this.annotations.add(AnnotationSpec.builder(annotation).build());
      return this;
    }

    public Builder addAnnotation(Class<?> annotation) {
      return addAnnotation(ClassName.get(annotation));
    }

    public Builder addModifiers(Modifier... modifiers) {
      Collections.addAll(this.modifiers, modifiers);
      return this;
    }

    public Builder initializer(String format, Object... args) {
      return initializer(com.lzh.processor.util.javapoet.CodeBlock.builder().add(format, args).build());
    }

    public Builder initializer(CodeBlock codeBlock) {
      checkState(this.initializer == null, "initializer was already set");
      this.initializer = checkNotNull(codeBlock, "codeBlock == null");
      return this;
    }

    public FieldSpec build() {
      return new FieldSpec(this);
    }
  }
}
