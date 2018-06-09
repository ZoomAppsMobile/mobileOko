package mvp.view

import com.arellomobile.mvp.MvpView
import mvp.model.GlobalModel

interface GlobarView : MvpView {
    fun responseUploadGlobar(responseHistoryBrowserModel: GlobalModel)
    fun errorUploadGlobar(error:String)

}