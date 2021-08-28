@file:Suppress("unused")

package crocodile8008.rxutils.stream

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class ConsumableStream<T : Any> {

    private val stream = BehaviorSubject.createDefault<List<T>>(listOf())

    @Synchronized
    fun push(value: T) {
        stream.onNext(stream.value!! + value)
    }

    fun observe(): Observable<T> = stream
        .filter { it.isNotEmpty() }
        .doOnNext { clean() }
        .concatMapIterable { it }

    @Synchronized
    private fun clean() {
        stream.onNext(listOf())
    }
}
