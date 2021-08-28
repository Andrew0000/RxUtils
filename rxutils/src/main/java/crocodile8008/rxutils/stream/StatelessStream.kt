@file:Suppress("unused")

package crocodile8008.rxutils.stream

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

open class StatelessStream<T : Any> {

    private val stream: PublishSubject<T> = PublishSubject.create()

    fun push(value: T) {
        stream.onNext(value)
    }

    fun get(): Observable<T> = stream
}