package crocodile8008.rxutils.joint

import crocodile8008.rxutils.log.RxUtilsLog
import crocodile8008.rxutils.log.RxUtilsLogTarget
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

@Suppress("UNCHECKED_CAST", "UNUSED")
class JointObservable<K, V : Any>(
    private val workObservationScheduler: Scheduler = Schedulers.trampoline(),
    private val work: (key: K) -> Observable<V>
) {

    private val requests = HashMap<K, Observable<V>>()
    private val activeObservers = HashMap<K, Int>()

    fun getObservable(key: K): Observable<V> =
        Observable
            .defer {
                synchronized(requests) {
                    requests[key]?.let { existing ->
                        log("exists", key)
                        incrementActiveObservers(key)
                        return@defer existing
                    }

                    val newRequest = work(key)
                        .observeOn(workObservationScheduler)
                        .replay(1)
                        .refCount()
                        .doFinally {
                            synchronized(requests) {
                                decrementActiveObservers(key)
                                if (getActiveObserversCount(key) <= 0) {
                                    log("completed", key)
                                    requests.remove(key)
                                    activeObservers.remove(key)
                                }
                            }
                        }

                    requests[key] = newRequest
                    incrementActiveObservers(key)
                    log("new", key)
                    return@defer newRequest
                }
            }

    private fun log(msg: String, key: K) {
        if (RxUtilsLog.target != RxUtilsLogTarget.EMPTY) {
            RxUtilsLog.d("[JointObservable] $msg: $key")
        }
    }

    private fun incrementActiveObservers(key: K) {
        val activeObserversCount = getActiveObserversCount(key)
        activeObservers[key] = activeObserversCount + 1
    }

    private fun decrementActiveObservers(key: K) {
        val activeSubscribersCount = getActiveObserversCount(key)
        activeObservers[key] = (activeSubscribersCount - 1).coerceAtLeast(0)
    }

    private fun getActiveObserversCount(key: K) = activeObservers[key] ?: 0

    companion object {

        fun <K, V : Any> create(
            workObservationScheduler: Scheduler = Schedulers.trampoline(),
            work: (key: K) -> Observable<V>
        ) = JointObservable(
            workObservationScheduler,
            work
        )
    }
}
