package com.taptap.router;

public class RouteData {

  public static final int TYPE_ACTIVITY = 1;

  // 包含Fragment 以及开发者自己定义的页面类型
  public static final int TYPE_OTHERS = -1;

  public String path;
  public Class targetClass;
  public int pageType = TYPE_OTHERS;

  public RouteData(int pageType, String path, Class pageClass) {
    this.pageType = pageType;
    this.path = path;
    this.targetClass = pageClass;
  }
}
