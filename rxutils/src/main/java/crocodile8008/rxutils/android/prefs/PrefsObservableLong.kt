@file: Suppress("Unused")

package crocodile8008.rxutils.android.prefs

import android.content.SharedPreferences
import io.reactivex.rxjava3.core.Observable

class PrefsObservableLong(
    private val prefs: SharedPreferences,
    private val key: String,
    private val default: Long = 0,
    private val clearSignal: Observable<Unit>? = null,
) {
    var value: Long
        get() = prefs.getLong(key, default)
        set(value) = prefs.edit().putLong(key, value).apply()

    val stream by lazy { prefs.createObserver(key, clearSignal) { value } }
}