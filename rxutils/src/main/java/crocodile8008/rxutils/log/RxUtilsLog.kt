package crocodile8008.rxutils.log

import android.util.Log

object RxUtilsLog {

    var target = RxUtilsLogTarget.EMPTY

    private const val TAG = "RxUtilsLog"

    fun d(msg: String) {
        when (target) {
            RxUtilsLogTarget.CONSOLE -> println(msg)
            RxUtilsLogTarget.ANDROID_LOG -> Log.d(TAG, msg)
            else -> { /* Empty */ }
        }
    }

    fun e(msg: String = "", throwable: Throwable) {
        when (target) {
            RxUtilsLogTarget.CONSOLE -> {
                println(msg)
                throwable.printStackTrace()
            }
            RxUtilsLogTarget.ANDROID_LOG -> {
                Log.e(TAG, msg, throwable)
            }
            else -> { /* Empty */ }
        }
    }
}
