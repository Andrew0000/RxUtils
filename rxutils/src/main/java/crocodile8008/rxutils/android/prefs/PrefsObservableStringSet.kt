@file: Suppress("Unused")

package crocodile8008.rxutils.android.prefs

import android.content.SharedPreferences
import io.reactivex.rxjava3.core.Observable

class PrefsObservableStringSet(
    private val prefs: SharedPreferences,
    private val key: String,
    private val default: Set<String> = setOf(),
    private val clearSignal: Observable<Unit>? = null,
) {
    var value: Set<String>
        get() = prefs.getStringSet(key, default) ?: default
        set(value) = prefs.edit().putStringSet(key, value).apply()

    val stream by lazy { prefs.createObserver(key, clearSignal) { value } }
}