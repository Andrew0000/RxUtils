@file: Suppress("Unused")

package crocodile8008.rxutils.android.prefs

import android.content.SharedPreferences
import io.reactivex.rxjava3.core.Observable

class PrefsObservableInt(
    private val prefs: SharedPreferences,
    private val key: String,
    private val default: Int = 0,
    private val clearSignal: Observable<Unit>? = null,
) {
    var value: Int
        get() = prefs.getInt(key, default)
        set(value) = prefs.edit().putInt(key, value).apply()

    val stream by lazy { prefs.createObserver(key, clearSignal) { value } }
}