package crocodile8008.rxutils.android

import android.os.Handler
import android.os.Looper

internal val mainHandler by lazy { Handler(Looper.getMainLooper()) }

internal fun runOnMainThread(action: () -> Unit) {
    if (isMainThread()) {
        action()
    } else {
        mainHandler.post(action)
    }
}

internal fun isMainThread() =
    Looper.myLooper() == Looper.getMainLooper()
