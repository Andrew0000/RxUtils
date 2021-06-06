package crocodile8008.rxutils.log

import android.util.Log

object RxUtilsLog {

    var target = RxUtilsLogTarget.EMPTY

    fun d(msg: String) {
        when (target) {
            RxUtilsLogTarget.CONSOLE -> println(msg)
            RxUtilsLogTarget.ANDROID_LOG -> Log.d("RxUtilsLog", msg)
            else -> { /* Empty */ }
        }
    }
}
