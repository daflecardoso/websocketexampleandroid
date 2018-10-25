package info.dafle.websocketexample

import android.graphics.Bitmap
import android.util.Log
import info.dafle.websocketexample.remote.MainService
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.io.ByteArrayOutputStream
import java.util.*

class ImagePresenter(val view: MainActivity) {

    fun uploadImage(bitmap: Bitmap) {

        doAsync {

            val stream = ByteArrayOutputStream()

            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            val byteArray = stream.toByteArray()

            val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), byteArray)
            val filePart = MultipartBody.Part.createFormData("image", Date().time.toString() + ".png", requestBody)

            uiThread {

                MainService.Creator.newMainService().uploadPicture(filePart)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Observer<ResponseBody> {
                        override fun onSubscribe(d: Disposable) {
                            Log.i("Script", "onSubscribe")
                            view.toast("Enviando imagem")
                            // handler.post({ getMvpView().showProgressDialog(getMvpView().getStringRes(R.string.request_send_image)) })
                        }

                        override fun onNext(responseBody: ResponseBody) {
                            // chooseBehaviorFromAction(action)
                            view.toast("Enviado com sucesso")
                            Log.i("Script", "onNext")
                        }

                        override fun onError(e: Throwable) {
                            view.toast("Erro ao enviar")
                            e.printStackTrace()
                            // getMvpView().dismissProgressDialog()
                            // handleError(e, null)
                        }

                        override fun onComplete() {
                            Log.i("Script", "onComplete")
                            // getMvpView().dismissProgressDialog()
                        }
                    })
            }
        }
    }
}