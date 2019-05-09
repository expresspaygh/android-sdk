# expressPay SDK for Android Package

This package contains the expressPay library, which allows you to
integrate expressPay into your Android app

expressPay SDK for Android

REQUIREMENTS

    Android API  >=  10

INSTALLATION

1. Open your project in Android Studio
2. Download and unzip the library
3. Add the expressPayLibrary-1.0.aar to your libs folder
4. Open your app’s build.gradle and add the “repositories” section:
    ```java
    repositories{
     flatDir{
        dirs 'libs'
      }}
      ```
5. Also add this in the "depencies" section:
    ```java
    dependencies{
        //...
        compile (name: ‘expressPayLibrary-1.4’, ext:’aar’) 
        compile 'com.mcxiaoke.volley:library:1.0.19'
    }``
