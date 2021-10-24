# Rx utils

🔹 Sharing / caching ongoing streams by key.  
🔹 Retrying with delay.  
🔹 Rx Preferences.  
🔹 RxValue / RxValueMutable like LiveData.  
🔹 observeWhenStarted() / observeUntilDetach() functions for LifecycleOwner and View.  

# Setup:  

[![](https://jitpack.io/v/Andrew0000/RxUtils.svg)](https://jitpack.io/#Andrew0000/RxUtils)

1. Add `maven { url 'https://jitpack.io' }` to the `allprojects` or `dependencyResolutionManagement` section in top-leve `build.gradle` or `settings.gradle`.  
For example (`settings.gradle`):
```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter() // Warning: this repository is going to shut down soon
        maven { url "https://jitpack.io" }
    }
}
```
2. Add `implementation 'com.github.Andrew0000:RxUtils:$latest_version'` to the module-level `build.gradle`  

# Usage:

Helpers for RxJava. 

For example: reusing ongoing Observable/Single work without duplication.  
It may be helpful for reduce network traffic or another heavy or long-running work.   
It also supports requests keys and can be managed easier than ConnectableObservable.  

Example:  
```
val joint = JointObservableSimple.create { 
    // This work will be invoked only 1 time
    longRunningWork() 
}

// We can invoke joint many times but underlying work won't be duplicated
repeat(10) {
    joint
        .getObservable()
        .subscribe { result ->
            // Handle result
        }
}
```

Observable:  
[**JointObservable**](rxutils/src/main/java/crocodile8008/rxutils/joint/JointObservable.kt) 
and [**JointObservableSimple**](rxutils/src/main/java/crocodile8008/rxutils/joint/JointObservableSimple.kt)  

Single:  
[**JointSingle**](rxutils/src/main/java/crocodile8008/rxutils/joint/JointSingle.kt) and 
[**JointSingleSimple**](rxutils/src/main/java/crocodile8008/rxutils/joint/JointSingleSimple.kt)  
  
Sample:  
[**MainActivity**](app/src/main/java/crocodile8008/rxutils/MainActivity.kt)  

Some other functions:  
[**observeWhenAttached for View**](rxutils/src/main/java/crocodile8008/rxutils/android/RxAndroidUtils.kt)    
[**observeWhenStarted for LifecycleOwner**](rxutils/src/main/java/crocodile8008/rxutils/android/RxAndroidUtils.kt)  
[**withRetrying**](rxutils/src/main/java/crocodile8008/rxutils/Retrying.kt)  

Tests:  
[**Tests package**](rxutils/src/test/java/crocodile8008/rxutils/)  

