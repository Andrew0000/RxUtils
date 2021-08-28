package crocodile8008

import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class InstantRxSchedulersRule : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    override fun finished(description: Description?) {
        super.finished(description)
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()
    }
}
