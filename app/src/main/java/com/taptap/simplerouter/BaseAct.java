package com.taptap.simplerouter;

import android.support.v7.app.AppCompatActivity;
import com.taptap.annotation.TapRouteParams;

public class BaseAct extends AppCompatActivity {

  @TapRouteParams("referer")
  String referer;
}
