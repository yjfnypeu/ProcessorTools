package com.lzh.processor.util;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class FileLog {

	private static final boolean DEBUG = true;
//	private static String filePath = "C:\\apt_gene\\apt_intent_generate.log";
	private static String filePath = "/build/log.txt";

	public FileLog() {
	}

	public static void reload() {
		File file = new File(filePath);
		if(!file.exists()) {
			file.getParentFile().mkdirs();
		}

		try {
			file.delete();
		} catch (Exception var2) {
		}

	}
	
	private synchronized static BufferedWriter getWriter () {
		try {
			return new BufferedWriter(new FileWriter(filePath, true));
		} catch (IOException e) {
			return null;
		}
	}
	
	public static void print(String log,Object ...objects) throws Exception {
		if (!DEBUG) {
			return;
		}
		BufferedWriter writer = getWriter();
		try {
			String data = String.format(log, objects);
			writer.write("\t" + data);
			writer.newLine();
			writer.newLine();
			writer.flush();
		} catch(IOException ioe) {
		} catch (Exception e) {
			throw e;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void printException(Throwable e) {
		if (!DEBUG) {
			return;
		}
		BufferedWriter writer = getWriter();
		try {
			e.printStackTrace(new PrintWriter(writer));
			writer.flush();
		} catch (Exception e1) {
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException ex) {
				}
			}
		}
	}
	
	public static void printElement (Element ele) throws Exception {
		if (!DEBUG) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("Element:%s", ele));
		sb.append(String.format("\r\n + getAnnotation:%s",ele.getAnnotationMirrors()));
		sb.append(String.format("\r\n + getAnnotationMirrors:%s",ele.getAnnotationMirrors()));
		sb.append(String.format("\r\n + asType:%s",ele.asType()));
		sb.append(String.format("\r\n + getClass:%s",ele.getClass()));
		sb.append(String.format("\r\n + getEnclosedElements:%s",ele.getEnclosedElements()));
		sb.append(String.format("\r\n + getEnclosingElement:%s",ele.getEnclosingElement()));
		sb.append(String.format("\r\n + getKind:%s",ele.getKind()));
		sb.append(String.format("\r\n + getModifiers:%s",ele.getModifiers()));
		sb.append(String.format("\r\n + getSimpleName:%s",ele.getSimpleName()));
		
		if (ele instanceof TypeElement) {
			sb.append(String.format("\r\n + (TypeElement)getInterfaces:%s",((TypeElement) ele).getInterfaces()));
			sb.append(String.format("\r\n + (TypeElement)getNestingKind:%s",((TypeElement) ele).getNestingKind()));
			sb.append(String.format("\r\n + (TypeElement)getQualifiedName:%s",((TypeElement) ele).getQualifiedName()));
			sb.append(String.format("\r\n + (TypeElement)getSuperclass:%s",((TypeElement) ele).getSuperclass()));
			sb.append(String.format("\r\n + (TypeElement)getTypeParameters:%s",((TypeElement) ele).getTypeParameters()));
		} else if (ele instanceof PackageElement) {
			sb.append(String.format("\r\n + (PackageElement)getQualifiedName:%s",((PackageElement) ele).getQualifiedName()));
			sb.append(String.format("\r\n + (PackageElement)isUnnamed:%s",((PackageElement) ele).isUnnamed()));
		} else if (ele instanceof VariableElement) {
			sb.append(String.format("\r\n + (VariableElement)getConstantValue:%s",((VariableElement) ele).getConstantValue()));
			((VariableElement) ele).getConstantValue();
		} else if (ele instanceof TypeParameterElement) {
			sb.append(String.format("\r\n + (TypeParameterElement)getGenericElement:%s",((TypeParameterElement) ele).getGenericElement()));
			sb.append(String.format("\r\n + (TypeParameterElement)getBounds:%s",((TypeParameterElement) ele).getBounds()));
		} else if (ele instanceof ExecutableElement) {
			sb.append(String.format("\r\n + (ExecutableElement)getDefaultValue:%s",((ExecutableElement) ele).getDefaultValue()));
			sb.append(String.format("\r\n + (ExecutableElement)getParameters:%s",((ExecutableElement) ele).getParameters()));
			sb.append(String.format("\r\n + (ExecutableElement)getReturnType:%s",((ExecutableElement) ele).getReturnType()));
		}
		print(sb.toString());
	}

	public static void printMirror(TypeMirror mirror) throws Exception {
		if (!DEBUG) {
			return;
		}
		print("TypeMirror:%s"
				+ "\r\n + getKind:%s"
				+ "\r\n + getClass:%s"
				,mirror
				,mirror.getKind()
				,mirror.getClass()
				);
	}
	
}
