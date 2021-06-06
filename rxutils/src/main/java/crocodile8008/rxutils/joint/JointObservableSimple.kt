package crocodile8008.rxutils.joint

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

@Suppress( "UNUSED")
class JointObservableSimple<V : Any>(
    private val joint: JointObservable<Unit, V>
) {

    fun getObservable(): Observable<V> = joint.getObservable(Unit)

    companion object {

        fun <V : Any> create(
            workObservationScheduler: Scheduler = Schedulers.trampoline(),
            work: (key: Unit) -> Observable<V>
        ) = JointObservableSimple(
            JointObservable(
                workObservationScheduler,
                work
            )
        )
    }
}
