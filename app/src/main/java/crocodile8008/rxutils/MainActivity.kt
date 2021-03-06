package crocodile8008.rxutils

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import crocodile8008.rxutils.android.observeWhenStarted
import crocodile8008.rxutils.joint.JointObservableSimple
import crocodile8008.rxutils.joint.JointSingleSimple
import crocodile8008.rxutils.log.RxUtilsLog
import crocodile8008.rxutils.log.RxUtilsLogTarget
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : AppCompatActivity() {

    private lateinit var textView1: TextView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var progress1: ProgressBar
    private lateinit var progress2: ProgressBar

    private val prefs by lazy { PreferencesRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView1 = findViewById(R.id.textView1)
        textView2 = findViewById(R.id.textView2)
        textView3 = findViewById(R.id.textView3)
        progress1 = findViewById(R.id.progress1)
        progress2 = findViewById(R.id.progress2)

        if (BuildConfig.DEBUG) {
            RxUtilsLog.target = RxUtilsLogTarget.ANDROID_LOG
        }

        runJointObservable10times()

        runJointSingle10times()

        prefsSample()
    }

    @SuppressLint("SetTextI18n")
    private fun prefsSample() {
        prefs.someString.stream.observeWhenStarted(this) {
            textView3.text = "prefs value: $it"
        }
        Observable.interval(1, TimeUnit.SECONDS, Schedulers.io())
            .observeWhenStarted(this) {
                if (it % 5L == 0L) {
                    prefs.clearAll()
                } else {
                    prefs.someString.value = it.toString()
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun runJointObservable10times() {
        val joint1 = JointObservableSimple.create { longRunningWork() }
        val requestTimes = 10
        repeat(requestTimes) {
            joint1
                .getObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    textView1.text = "Work #1 running: $result times"
                    progress1.visibility = View.INVISIBLE
                }
                //TODO Handle disposable
        }
    }

    @SuppressLint("SetTextI18n")
    private fun runJointSingle10times() {
        val joint2 = JointSingleSimple.notCached { longRunningWork().firstOrError() }
        val requestTimes = 10
        repeat(requestTimes) {
            joint2
                .getSingle()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    textView2.text = "Work #2 running: $result times"
                    progress2.visibility = View.INVISIBLE
                }
                //TODO Handle disposable
        }
    }

    private fun longRunningWork(): Observable<Int> {
        val workCount = AtomicInteger()
        return Observable.defer {
            Observable
                .fromCallable {
                    Thread.sleep(2000)
                    workCount.incrementAndGet()
                }
                .subscribeOn(Schedulers.io())
        }
    }
}
