package base

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BasePresenter<View : MvpView> : MvpPresenter<View>() {
    var compositeDisposable : CompositeDisposable = CompositeDisposable()

    protected fun unsubscribe(disposible : Disposable? = null){
        compositeDisposable.add(disposible!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}