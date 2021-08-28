@file:Suppress("unused")

package crocodile8008.rxutils.stream

import crocodile8008.rxutils.update
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.ConcurrentHashMap

open class StatefulStream<K : Any, V : Any> {

    private val stream = BehaviorSubject.createDefault(ConcurrentHashMap<K, V>())

    @Suppress("UNCHECKED_CAST")
    fun all(): Observable<Map<K, V>> =
        stream as Observable<Map<K, V>>

    fun allBlocking(): Map<K, V> =
        stream.value!!

    fun set(key: K, value: V) {
        stream.update { values -> values!![key] = value }
    }

    fun remove(key: K) {
        stream.update { values -> values!!.remove(key) }
    }
}