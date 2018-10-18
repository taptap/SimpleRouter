package com.taptap.router;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.taptap.annotation.TapRouteParams;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.taptap.annotation.TapRouteParams"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ParamsProcessor extends AbstractProcessor {

  Filer filer = null;

  private List<TypeElement> paramsClass = new ArrayList<>();

  private Elements elementUtils = null;
  private Types typeUtils = null;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);
    filer = processingEnvironment.getFiler();
    elementUtils = processingEnvironment.getElementUtils();
    typeUtils = processingEnvironment.getTypeUtils();
  }



  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

    if (null == set || set.size() == 0) {
      return false;
    }
    // 解析页面参数
    Set<? extends Element> params = roundEnvironment
        .getElementsAnnotatedWith(TapRouteParams.class);
    Iterator<? extends Element> iterator = params.iterator();
    while (iterator.hasNext()) {
      Element next = iterator.next();
      Element key = next.getEnclosingElement();

      if (!paramsClass.contains(key)) {
        paramsClass.add((TypeElement) key);
      }
    }
    if (paramsClass.size() > 0) {
      processParams();
      return true;
    }
    return false;
  }

  void processParams() {
    for (int i = 0; i < paramsClass.size(); i++) {
      TypeElement taregetPageElement = paramsClass.get(i);
      String targetPageName = taregetPageElement.getQualifiedName().toString();
      String packageName = targetPageName.substring(0, targetPageName.lastIndexOf("."));

      Builder builder = MethodSpec.methodBuilder("inject")
          .addModifiers(Modifier.PUBLIC)
          .addAnnotation(Override.class)
          .returns(TypeVariableName.get("void"))
          .addParameter(
              ParameterSpec.builder(TypeVariableName.get(targetPageName), "target", Modifier.FINAL)
                  .build());

      CodeBlock.Builder methodBlock = CodeBlock.builder();

      // Intent argument = null;
      methodBlock.addStatement("$T argument = null",
          TypeVariableName.get("android.os.Bundle"));

      if (Utils.isAndroidActivity(typeUtils, taregetPageElement)) {
//        code statement
//        Intent intent = target.getIntent();
//        if (null != intent) {
//          argument = intent.getExtras();
//        }

        methodBlock.addStatement("android.content.Intent intent = target.getIntent()");

        methodBlock.beginControlFlow("if (null != intent)")
            .addStatement("argument = intent.getExtras()")
            .endControlFlow();

      } else {
        // argument = target.getArguments()
        methodBlock.addStatement("argument = target.getArguments()");
      }

      List<? extends Element> allElement = getAllElement(taregetPageElement);
      for (int m = 0; null != allElement && m < allElement.size(); m++) {
        if (allElement.get(m).getKind() == ElementKind.FIELD) {
          VariableElement element = (VariableElement) allElement.get(m);
          TapRouteParams annotation = element.getAnnotation(TapRouteParams.class);
          if (null != annotation) {
            String[] paramKeys = annotation.value();

            for (int k = 0; k < paramKeys.length; k++) {
              String paramKey = paramKeys[k];
              TypeMirror typeMirror = element.asType();

              methodBlock.beginControlFlow("if (null != argument && argument.containsKey($S))", paramKey);
              methodBlock.addStatement("Object value = argument.get($S)", paramKey);
              switch (typeMirror.toString()) {
                case "int":
                  methodBlock.addStatement("target." + element.getSimpleName().toString()
                      + " = Integer.parseInt(\"\" + value.toString())");
                  break;
                case "long":
                  methodBlock.addStatement("target." + element.getSimpleName().toString()
                      + " = Long.parseLong(\"\" + value.toString())");
                  break;
                case "boolean":
                  methodBlock.addStatement("target." + element.getSimpleName().toString()
                      + " = Boolean.parseBoolean(\"\" + value.toString())");
                  break;
                case "java.lang.String":
                  methodBlock.addStatement(
                      "target." + element.getSimpleName().toString() + " = value.toString()");
                  break;
                default:
                  TypeElement parcelable = (TypeElement) typeUtils.asElement(typeMirror);
                  List<? extends TypeMirror> interfaces = parcelable.getInterfaces();
                  boolean isParcelable = false;
                  if (parcelable.asType().toString().equals("android.os.Parcelable")) {
                    isParcelable = true;
                  } else if (null != interfaces && interfaces.size() > 0) {
                    for (int j = 0; j < interfaces.size(); j++) {
                      TypeMirror type = interfaces.get(j);
                      if (type.toString().equals("android.os.Parcelable")) {
                        isParcelable = true;
                      }
                    }
                  }
                  if (isParcelable) {
                    methodBlock.addStatement(
                        "target." + element.getSimpleName().toString() + " = argument.getParcelable($S)",
                        paramKey);
                  }
                  break;
              }
              methodBlock.endControlFlow();
            }
          }
        }
      }

      MethodSpec extendSpec = builder
          .addCode(methodBlock.build())
          .build();

      TypeSpec spec = TypeSpec.classBuilder(taregetPageElement.getSimpleName().toString() + "$$" + "RouteInjector")
          .addSuperinterface(ParameterizedTypeName.get(ClassName.get(ParamsInject.class), TypeVariableName.get(targetPageName)))
          .addModifiers(Modifier.PUBLIC)
          .addJavadoc("Auto Generated By TapTap Router! DO NOT MODIFY IT! Created by CaoJianbo")
          .addMethod(extendSpec)
          .build();

      JavaFile file = JavaFile.builder(packageName, spec).build();
      try {
        file.writeTo(filer);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public List<? extends Element> getAllElement(TypeElement element){
    ArrayList<Element> result = new ArrayList<>();
    TypeElement typeElement = element;
    while (typeElement != null) {
      List<? extends Element> tmp = typeElement.getEnclosedElements();
      if (null != tmp) {
        result.addAll(tmp);
      }
      TypeMirror superclass = typeElement.getSuperclass();
      if (superclass.toString().equals("java.lang.Object")) {
        typeElement  = null;
      } else {
        typeElement = (TypeElement) typeUtils.asElement(superclass);
      }
    }

    return result;
  }
}

