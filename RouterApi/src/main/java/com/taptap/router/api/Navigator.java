package com.taptap.router.api;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import com.taptap.router.api.RouterManager.RouteDataHandler;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Navigator {

  private Map<String, Integer> integerParams = new HashMap<>();
  private Map<String, Boolean> booleanParams = new HashMap<>();
  private Map<String, Long> longParams = new HashMap<>();
  private Map<String, String> stringParams = new HashMap<>();
  private Map<String, Parcelable> parcelableParams = new HashMap<>();
  private String path;

  public String getPath() {
    return path;
  }

  public Bundle toBundle(){
    Bundle bundle = new Bundle();
    if (integerParams.size() > 0) {
      Iterator<Entry<String, Integer>> iterator = integerParams.entrySet().iterator();
      while (iterator.hasNext()) {
        Entry<String, Integer> next = iterator.next();
        bundle.putInt(next.getKey(), next.getValue());
      }
    }

    if (longParams.size() > 0) {
      Iterator<Entry<String, Long>> iterator = longParams.entrySet().iterator();
      while (iterator.hasNext()) {
        Entry<String, Long> next = iterator.next();
        bundle.putLong(next.getKey(), next.getValue());
      }
    }

    if (stringParams.size() > 0) {
      Iterator<Entry<String, String >> iterator = stringParams.entrySet().iterator();
      while (iterator.hasNext()) {
        Entry<String, String> next = iterator.next();
        bundle.putString(next.getKey(), next.getValue());
      }
    }

    if (booleanParams.size() > 0) {
      Iterator<Entry<String, Boolean >> iterator = booleanParams.entrySet().iterator();
      while (iterator.hasNext()) {
        Entry<String, Boolean> next = iterator.next();
        bundle.putBoolean(next.getKey(), next.getValue());
      }
    }

    if (parcelableParams.size() > 0) {
      Iterator<Entry<String, Parcelable >> iterator = parcelableParams.entrySet().iterator();
      while (iterator.hasNext()) {
        Entry<String, Parcelable> next = iterator.next();
        bundle.putParcelable(next.getKey(), next.getValue());
      }
    }

    return bundle;
  }

  public boolean navigate(Context from, RouteDataHandler routeDataHandler){
    return RouterManager.getInstance().navigate(from, this, routeDataHandler);
  }

  public static class Builder {

    private Map<String, Integer> integerParams = new HashMap<>();
    private Map<String, Boolean> booleanParams = new HashMap<>();
    private Map<String, Long> longParams = new HashMap<>();
    private Map<String, String> stringParams = new HashMap<>();
    private Map<String, Parcelable> parcelableParams = new HashMap<>();
    private String path;
    private boolean newActivity;
    private boolean replaceCurrentPage;

    public Builder url(String url){
      Uri uri = Uri.parse(url);
      this.path = uri.getPath();
      Set<String> queryParameterNames = uri.getQueryParameterNames();
      if (queryParameterNames != null) {
        Iterator<String> iterator = queryParameterNames.iterator();
        while (iterator.hasNext()) {
          String key = iterator.next();
          String queryParameter = uri.getQueryParameter(key);
          addString(key, queryParameter);
        }
      }
      return this;
    }

    public Builder path(String path){
      this.path = path;
      return this;
    }

    public Builder addInt(String key, int value){
      integerParams.put(key, value);
      return this;
    }

    public Builder addLong(String key, long value){
      longParams.put(key, value);
      return this;
    }

    public Builder addString(String key, String value){
      stringParams.put(key, value);
      return this;
    }

    public Builder addBoolean(String key, boolean value){
      booleanParams.put(key, value);
      return this;
    }

    public Builder addParcelable(String key, Parcelable value){
      parcelableParams.put(key, value);
      return this;
    }

    public Navigator build(){
      Navigator navigator = new Navigator();
      navigator.integerParams = this.integerParams;
      navigator.booleanParams = this.booleanParams;
      navigator.longParams = this.longParams;
      navigator.stringParams = this.stringParams;
      navigator.parcelableParams = this.parcelableParams;
      navigator.path = this.path;

      return navigator;
    };

    public Builder referer(String referer) {
      addString("referer", referer);
      return this;
    }

    public Builder source(String source) {
      addString("source", source);
      return this;
    }
  }
}
