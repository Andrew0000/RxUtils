package crocodile8008.rxutils

import androidx.lifecycle.*
import crocodile8008.InstantRxSchedulersRule
import crocodile8008.rxutils.android.observeWhenStarted
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class ObserveWhenStartedTest {

    @get:Rule
    val instantRx = InstantRxSchedulersRule()

    @Test
    fun `When go through lifecycle - Then receive events only when started and don't receive anyway after ON_DESTROY`() {
        val collectedResults = mutableListOf<Int>()
        lateinit var emitter: ObservableEmitter<Int>
        val observable = Observable.create<Int> { e ->
            emitter = e
        }
        lateinit var lifecycle: LifecycleRegistry
        val liveOwner = object : LifecycleOwner {
            val lifecycleInternal = LifecycleRegistry(this).also {
                lifecycle = it
            }

            override fun getLifecycle(): Lifecycle = lifecycleInternal
        }

        observable.observeWhenStarted(
            lifecycleOwner = liveOwner,
            onNext = { collectedResults += it },
            onNextWrapper = { it() }
        )

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
        emitter.onNext(1)
        emitter.onNext(2)
        assertFalse(emitter.isDisposed)

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        emitter.onNext(3)
        emitter.onNext(4)
        assertTrue(emitter.isDisposed)

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
        emitter.onNext(5)
        assertFalse(emitter.isDisposed)

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        emitter.onNext(6)
        emitter.onNext(7)
        assertTrue(emitter.isDisposed)

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
        emitter.onNext(8)
        emitter.onNext(9)
        assertTrue(emitter.isDisposed)

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        emitter.onNext(10)
        emitter.onNext(11)
        assertTrue(emitter.isDisposed)

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        emitter.onNext(12)
        emitter.onNext(13)
        assertTrue(emitter.isDisposed)

        assertEquals(listOf(1, 2, 5), collectedResults)
    }
}