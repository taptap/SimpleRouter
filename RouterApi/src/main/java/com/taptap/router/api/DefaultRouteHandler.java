package com.taptap.router.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.taptap.router.RouteData;
import com.taptap.router.api.RouterManager.RouteDataHandler;

public class DefaultRouteHandler implements RouteDataHandler {

  @Override
  public boolean handlerRouteData(Context context, RouteData routeData, Navigator navigatorData) {
    switch (routeData.pageType) {
      case RouteData.TYPE_ACTIVITY:
        Intent intent = new Intent();
        intent.setClass(context, routeData.targetClass);
        intent.putExtras(navigatorData.toBundle());
        if (!(context instanceof Activity)) {
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        break;
    }
    return false;
  }
}
