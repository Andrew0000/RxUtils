# Rx utils

Helpers for re-using ongoing observable/single work without duplication.  
It may be helpful for reduce network traffic or another heavy or long-running work.   

Example:  
```
val joint1 = JointObservableSimple.create { 
    // This will invoke only 1 time
    longRunningWork() 
}

// We can invoke joint many times but underlying work won't be duplicated
for (i in 1..10) {
    joint1
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
