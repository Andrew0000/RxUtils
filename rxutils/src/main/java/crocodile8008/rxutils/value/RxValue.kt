@file:Suppress("unused")

package crocodile8008.rxutils.value

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

open class RxValue<T : Any>(defaultValue: T) {

    protected val subject: BehaviorSubject<T> = BehaviorSubject.createDefault(defaultValue)

    val value: T?
        get() = subject.value

    val stream: Observable<T>
        get() = subject
}
