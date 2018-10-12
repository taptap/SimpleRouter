package com.taptap.simplerouter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import com.taptap.router.RouteData;
import com.taptap.router.api.Navigator;
import com.taptap.router.api.RouterManager;
import com.taptap.router.api.RouterManager.RouteDataHandler;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.test_activity).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        new Navigator.Builder()
            .path("/test")
            .addString("name", "TapTap_new")
            .build().navigate(MainActivity.this, null);
      }
    });
  }
}
