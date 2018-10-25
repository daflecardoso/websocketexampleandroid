package info.dafle.websocketexample

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.provider.SyncStateContract
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.File
import java.net.URISyntaxException
import java.security.AccessController.getContext

class MainActivity : AppCompatActivity() {

    private lateinit var mSocket: Socket
    internal var photoFile: File? = null
    internal var bitmap: Bitmap? = null
    private lateinit var mPresenter: ImagePresenter
    private val URL = "http://172.18.2.21:3000/"
    private lateinit var adapter: ImageAdapter
    private var list = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mPresenter = ImagePresenter(this)

        try {
            mSocket = IO.socket(URL)
            mSocket.connect()
        } catch (e: URISyntaxException) {
            Log.e("Script", e.message)
        }

        mSocket.on("receive url", onReceiveMessage)

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        adapter = ImageAdapter(this, list)
        recyclerView.adapter = adapter

        buttom.setOnClickListener { checkPermissionCamera() }
    }

    private fun checkPermissionCamera() {

        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    captureImage()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", packageName, null)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {

                }
            }).check()
    }

    private fun captureImage() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                photoFile = ImageUtil(this).createImageFile()
                if (photoFile != null) {
                    val photoURI = FileProvider.getUriForFile(
                        this,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        photoFile!!
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, 1)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 1 && resultCode == RESULT_OK) {

            photoFile?.apply {
                bitmap = ImageUtil.rotateImage(BitmapFactory.decodeFile(absolutePath))
                mPresenter.uploadImage(bitmap!!)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mSocket.disconnect()
        mSocket.off("new message")
        mSocket.off("receive url", onReceiveMessage)
    }

    private val onReceiveMessage = Emitter.Listener {
        runOnUiThread {
            val data = it[0] as JSONObject
            val string = data.getString("url")

            val urlImage = "$URL$string"

            list.add(0, urlImage)
            adapter.notifyItemInserted(0)

            Log.w("Script", urlImage)
        }
    }
}
