@file:Suppress("unused")

package crocodile8008.rxutils

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

fun <T> BehaviorSubject<T>.updateUnsafe(updater: (T?) -> Unit) {
    val valueCopy = value
    updater(valueCopy)
    onNext(valueCopy)
}

fun <T> BehaviorSubject<T>.update(updater: (T?) -> Unit) {
    synchronized(this) {
        updateUnsafe(updater)
    }
}

fun <T : Any> Observable<T>.doOnNextOnce(action: () -> Unit): Observable<T> {
    var nextCounter = 0
    return doOnNext {
        synchronized(this) {
            if (nextCounter < 1) {
                action()
                nextCounter++
            }
        }
    }
}
