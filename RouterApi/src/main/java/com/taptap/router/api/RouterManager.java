package com.taptap.router.api;

import android.content.Context;
import com.taptap.RouteConstant;
import com.taptap.router.ParamsInject;
import com.taptap.router.RouteData;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class RouterManager {

  private static RouterManager instance;
  private HashMap<String, RouteData> pathMaps = new HashMap<>();
  private boolean initSuccess = false;

  // cache class
  private Map<String, Class> cacheClass = new HashMap<>();

  private DefaultRouteHandler defaultRouteHandler;

  private RouterManager(){
  }

  public static RouterManager getInstance(){
    if (null == instance) {
      synchronized (RouterManager.class) {
        if (null == instance) {
          instance = new RouterManager();
        }
      }
    }
    return instance;
  }

  public void init(){
    try {
      Class<?> AllRoutes = Class
          .forName(RouteConstant.All_ROUTES_PKG + "." + RouteConstant.All_ROUTES_CLASS);
      Field declaredField = AllRoutes.getDeclaredField(RouteConstant.All_ROUTES_FIELD);
      Object allRoutes = declaredField.get(null);
      if (allRoutes instanceof HashMap) {
        Set<Entry> set = ((HashMap) allRoutes).entrySet();
        Iterator<Entry> iterator = set.iterator();
        while (iterator.hasNext()) {
          Entry next = iterator.next();
          pathMaps.put((String)next.getKey(), (RouteData)next.getValue());
        }
      }
      defaultRouteHandler = new DefaultRouteHandler();
      initSuccess = true;
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
  }

  public void inject(Object target){
    String name = target.getClass().getName();
    try {
      String className = name + "$$" + "RouteInjector";

      Class<?> pageParamsClass = null;
      if (cacheClass.containsKey(className)) {
        pageParamsClass= cacheClass.get(className);
      } else {
        pageParamsClass = Class.forName(className);
        cacheClass.put(className, pageParamsClass);
      }
      Object o = pageParamsClass.newInstance();
      if (o instanceof ParamsInject) {
        ((ParamsInject) o).inject(target);
      }
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    }
  }

  public boolean navigate (Context from, Navigator navigator, RouteDataHandler routeDataHandler){
    if (!initSuccess) {
      init();
    }

    if (initSuccess) {
      if (pathMaps.containsKey(navigator.getPath())) {
        RouteData routeData = pathMaps.get(navigator.getPath());
        if (null != routeDataHandler) {
          return routeDataHandler.handlerRouteData(from, routeData, navigator);
        } else {
          return defaultRouteHandler.handlerRouteData(from, routeData, navigator);
        }
      }
    } else {

    }
    return false;
  }

  public interface RouteDataHandler {
    boolean handlerRouteData(Context from, RouteData routeData, Navigator navigatorData);
  }
}
