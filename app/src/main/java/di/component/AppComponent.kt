package di.component

import api.ApiService
import dagger.Component
import di.AppModule
import mvp.presenter.GlobarPresenter
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
   fun apiService() : ApiService

    fun inject(globarPresenter: GlobarPresenter)
}