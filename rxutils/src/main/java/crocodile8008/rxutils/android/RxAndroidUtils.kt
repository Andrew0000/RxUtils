@file:Suppress("unused")

package crocodile8008.rxutils.android

import android.view.View
import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import crocodile8008.rxutils.subscribe.subscribeSafe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

@MainThread
fun <T : Any> Observable<T>.observeUntilDetach(view: View, onNext: (T) -> Unit) {
    var disposable: Disposable? = null
    view.doOnAttachOnce {
        disposable = this
            .toMain()
            .subscribeSafe { value: T ->
                onNext(value)
            }
    }
    view.doOnDetachOnce {
        disposable?.dispose()
        disposable = null
    }
}

@MainThread
fun <T : Any> Observable<T>.observeWhenAttached(view: View, onNext: (T) -> Unit) {
    var disposable: Disposable? = null

    if (view.isAttachedToWindow) {
        disposable = subscribeSafe { onNext(it) }
    }

    view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View?) {
            disposable = subscribeSafe { onNext(it) }
        }

        override fun onViewDetachedFromWindow(v: View?) {
            disposable?.dispose()
            disposable = null
        }
    })
}

@MainThread
fun <T : Any> Observable<T>.observeWhenStarted(
    lifecycleOwner: LifecycleOwner,
    onNext: (T) -> Unit,
) {
    observeWhenStarted(
        lifecycleOwner = lifecycleOwner,
        onNext = onNext,
        onNextWrapper = { runOnMainThread { it() } },
    )
}

@MainThread
fun <T : Any> Observable<T>.observeWhenStarted(
    lifecycleOwner: LifecycleOwner,
    onNext: (T) -> Unit,
    onNextWrapper: (() -> Unit) -> Unit,
) {
    lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
        private var disposable: Disposable? = null

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart() {
            disposable = subscribeSafe {
                onNextWrapper {
                    onNext(it)
                }
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
            disposable?.dispose()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            lifecycleOwner.lifecycle.removeObserver(this)
        }
    })
}
