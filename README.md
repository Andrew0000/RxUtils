# Rx utils

Helpers for reusing ongoing Observable/Single work without duplication.  
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

Tests:  
[**Tests package**](rxutils/src/test/java/crocodile8008/rxutils/joint/)  
