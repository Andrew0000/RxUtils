@file: Suppress("Unused")

package crocodile8008.rxutils.android.prefs

import android.content.SharedPreferences
import io.reactivex.rxjava3.core.Observable

fun SharedPreferences.rxBoolean(
    key: String,
    default: Boolean = false,
    clearSignal: Observable<Unit>? = null,
) = PrefsObservableBoolean(this, key, default, clearSignal)

fun SharedPreferences.rxInt(
    key: String,
    default: Int = 0,
    clearSignal: Observable<Unit>? = null,
) = PrefsObservableInt(this, key, default, clearSignal)

fun SharedPreferences.rxLong(
    key: String,
    default: Long = 0,
    clearSignal: Observable<Unit>? = null,
) = PrefsObservableLong(this, key, default, clearSignal)

fun SharedPreferences.rxFloat(
    key: String,
    default: Float = 0f,
    clearSignal: Observable<Unit>? = null,
) = PrefsObservableFloat(this, key, default, clearSignal)

fun SharedPreferences.rxString(
    key: String,
    default: String = "",
    clearSignal: Observable<Unit>? = null,
) = PrefsObservableString(this, key, default, clearSignal)

fun SharedPreferences.rxStringSet(
    key: String,
    default: Set<String> = setOf(),
    clearSignal: Observable<Unit>? = null,
) = PrefsObservableStringSet(this, key, default, clearSignal)

fun <T: Any> SharedPreferences.createObserver(
    key: String,
    // Note: OnSharedPreferenceChangeListener ignores edit().clear() so we need a signal to react on it
    clearSignal: Observable<Unit>? = null,
    retriever: () -> T,
): Observable<T> {
    return Observable
        .create<T> { e ->
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
                if (!e.isDisposed && changedKey == key) {
                    e.onNext(retriever())
                }
            }
            registerOnSharedPreferenceChangeListener(listener)
            e.onNext(retriever())
            e.setCancellable { unregisterOnSharedPreferenceChangeListener(listener) }
        }
        .let {
            if (clearSignal == null) {
                it
            } else {
                it.mergeWith(clearSignal.map { retriever() })
            }
        }
        .replay(1).refCount()
}