package com.taptap.router;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public class Utils {

  public static boolean isAndroidActivity(Types typeUtils, TypeElement element){

    TypeMirror superclass = element.getSuperclass();
    if (null == superclass) {
      return element.asType().toString().equals("android.app.Activity");
    } else {
      TypeElement superClassElement = (TypeElement) typeUtils.asElement(superclass);
      if (null == superClassElement) {
        return false;
      } else {
        if (superClassElement.asType().toString().equals("android.app.Activity")) {
          return true;
        } else {
          return isAndroidActivity(typeUtils, superClassElement);
        }
      }
    }
  }

}
