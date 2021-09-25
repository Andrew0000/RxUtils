@file: Suppress("Unused")

package crocodile8008.rxutils.android.prefs

import android.content.SharedPreferences
import io.reactivex.rxjava3.core.Observable

class PrefsObservableBoolean(
    private val prefs: SharedPreferences,
    private val key: String,
    private val default: Boolean = false,
    private val clearSignal: Observable<Unit>? = null,
) {
    var value: Boolean
        get() = prefs.getBoolean(key, default)
        set(value) = prefs.edit().putBoolean(key, value).apply()

    val stream by lazy { prefs.createObserver(key, clearSignal) { value } }
}