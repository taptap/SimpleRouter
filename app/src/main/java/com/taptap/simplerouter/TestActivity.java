package com.taptap.simplerouter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.taptap.annotation.TapRoute;
import com.taptap.annotation.TapRouteParams;
import com.taptap.router.api.RouterManager;

@TapRoute(path = "/test")
public class TestActivity extends AppCompatActivity {

  @TapRouteParams("name")
  String name;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RouterManager.getInstance().inject(this);

    setContentView(R.layout.activity_test);

    ((TextView) findViewById(R.id.test_text)).setText(name);


  }
}
