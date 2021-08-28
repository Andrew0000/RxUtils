package crocodile8008.rxutils.android

import android.view.View

internal fun View.doOnAttachOnce(action: () -> Unit) {
    if (isAttachedToWindow) {
        action()
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View?) {
                removeOnAttachStateChangeListener(this)
                action()
            }

            override fun onViewDetachedFromWindow(view: View?) {
                removeOnAttachStateChangeListener(this)
            }
        })
    }
}

internal fun View.doOnDetachOnce(action: () -> Unit) {
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(view: View) {}

        override fun onViewDetachedFromWindow(view: View) {
            removeOnAttachStateChangeListener(this)
            action()
        }
    })
}