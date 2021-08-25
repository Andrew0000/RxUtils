package crocodile8008.rxutils.log

import android.util.Log

object RxUtilsLog {

    var target = RxUtilsLogTarget.EMPTY

    private val tag = "RxUtilsLog"

    fun d(msg: String) {
        when (target) {
            RxUtilsLogTarget.CONSOLE -> println(msg)
            RxUtilsLogTarget.ANDROID_LOG -> Log.d(tag, msg)
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
                Log.e(tag, msg, throwable)
            }
            else -> { /* Empty */ }
        }
    }
}
