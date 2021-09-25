@file: Suppress("Unused")

package crocodile8008.rxutils.android.prefs

import android.content.SharedPreferences
import io.reactivex.rxjava3.core.Observable

class PrefsObservableString(
    private val prefs: SharedPreferences,
    private val key: String,
    private val default: String = "",
    private val clearSignal: Observable<Unit>? = null,
) {
    var value: String
        get() = prefs.getString(key, default) ?: default
        set(value) = prefs.edit().putString(key, value).apply()

    val stream by lazy { prefs.createObserver(key, clearSignal) { value } }
}