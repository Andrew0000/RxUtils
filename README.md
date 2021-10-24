# Rx utils  

Some functions:  

🔹 Sharing / caching ongoing streams by key.  
🔹 Retrying with delay.  
🔹 Rx Preferences.  
🔹 RxValue / RxValueMutable like LiveData.  
🔹 observeWhenStarted() / observeUntilDetach() functions for LifecycleOwner and View.  
🔹 ConsumableStream.   
🔹 fromIoToMain() and other scheduling extensions.  

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

# Some examples:

### JointObservable  
Reusing ongoing Observable/Single work without duplication.  
It may be helpful for reduce network traffic or another heavy or long-running work.   
It also supports request keys and can be managed easier than ConnectableObservable.  

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
It has some modifications: JointObservable, JointObservableSimple, JointSingle, JointSingleSimple.  

### observeWhenStarted()  
Lifecycle-aware subscription like with LiveData or some coroutines extensions based on LifecycleOwner.  
Example for activity of fragment:  
```
someObservable.observeWhenStarted(this) { newValue ->
    // handle newValue
}
```

### observeWhenAttached()  
Lifecycle-aware subscription for View based on onViewAttachedToWindow / onViewDetachedFromWindow callbacks.  
Example for View:  
```
someObservable.observeWhenAttached(someView) { newValue ->
    // handle newValue
}
```

### withRetrying()
Retries stream on errors with given interval:  
```
someStreamThatCanFail.withRetrying(
    fallbackValue = fallbackValue,
    tryCnt = 3,
    intervalMillis = { tryNum -> tryNum * 2000L },
)
```
It will retry 2 more times after first fail with 2 sec. delay for first retry and 4 sec. for second.

### RxPrefs
Rx preferences:  
```
class PreferencesRepository(context: Context) {

    private val prefs = context.getSharedPreferences("your_prefs_name", Context.MODE_PRIVATE)
    private val clearSignal = PublishSubject.create<Unit>()

    val rxPrefString = prefs.rxString("some_string", clearSignal = clearSignal)

    fun clearAll() {
        prefs.edit().clear().apply()
        clearSignal.onNext(Unit)
    }
}
```
Note: clear signal is needed if you want to clear you prefs with `prefs.edit().clear()` function.  
Because OnSharedPreferenceChangeListener ignores edit().clear() on API < 30 so we need a signal to react on it.  
If you clena you prefs individually than you can skip clearSignal.  
Like that:
```
val rxPrefString = prefs.rxString("some_string")
```
