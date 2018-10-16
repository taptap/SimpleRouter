SimpleRouter
====================

## Description
Router for android!

## Usage
```java
 new Navigator.Builder()
            .path("/test")
            .addString("name", "TapTap")
            .build().navigate(MainActivity.this, null);
```

## Proguard
```
-kee class **$$RouteInjector
-keep class **AllRoutes
```
