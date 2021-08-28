@file:Suppress("unused")

package crocodile8008.rxutils.value

class RxValueMutable<T : Any>(defaultValue: T) : RxValue<T>(defaultValue) {

    fun set(newValue: T) {
        subject.onNext(newValue)
    }

    fun asImmutable(): RxValue<T> = this

    companion object {
        fun <T : Any> of(defaultValue: T) = RxValueMutable(defaultValue)
    }
}
