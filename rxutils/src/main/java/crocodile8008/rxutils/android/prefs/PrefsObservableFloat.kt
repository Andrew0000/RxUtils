@file: Suppress("Unused")

package crocodile8008.rxutils.android.prefs

import android.content.SharedPreferences
import io.reactivex.rxjava3.core.Observable

class PrefsObservableFloat(
    private val prefs: SharedPreferences,
    private val key: String,
    private val default: Float = 0f,
    private val clearSignal: Observable<Unit>? = null,
) {
    var value: Float
        get() = prefs.getFloat(key, default)
        set(value) = prefs.edit().putFloat(key, value).apply()

    val stream by lazy { prefs.createObserver(key, clearSignal) { value } }
}