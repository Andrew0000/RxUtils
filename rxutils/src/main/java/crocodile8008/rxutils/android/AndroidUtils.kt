package crocodile8008.rxutils.android

import android.os.Handler
import android.os.Looper

val mainHandler by lazy { Handler(Looper.getMainLooper()) }

fun runOnMainThread(action: () -> Unit) {
    if (isMainThread()) {
        action()
    } else {
        mainHandler.post(action)
    }
}

fun isMainThread() =
    Looper.myLooper() == Looper.getMainLooper()
