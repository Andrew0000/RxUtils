package crocodile8008.rxutils

import android.content.Context
import crocodile8008.rxutils.android.prefs.rxString
import io.reactivex.rxjava3.subjects.PublishSubject

class PreferencesRepository(context: Context) {

    private val prefs = context.getSharedPreferences("default", Context.MODE_PRIVATE)
    private val clearSignal = PublishSubject.create<Unit>()

    val someString = prefs.rxString("some_string", clearSignal = clearSignal)

    fun clearAll() {
        prefs.edit().clear().apply()
        clearSignal.onNext(Unit)
    }
}