package pxl.student.be.hackatongroup1.ui.login

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Facing
import kotlinx.android.synthetic.main.activity_login.*
import pxl.student.be.hackatongroup1.R
import pxl.student.be.hackatongroup1.data.async.OnHttpDataAvailable
import pxl.student.be.hackatongroup1.data.model.RequestDetect
import pxl.student.be.hackatongroup1.data.model.ResponsePersonLargePersonGroup
import pxl.student.be.hackatongroup1.domain.entity.Person
import pxl.student.be.hackatongroup1.domain.services.FaceService


private const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity(), OnHttpDataAvailable, BottomSheetFragment.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val service = FaceService(this)
        camera.setLifecycleOwner(this)
        camera.addCameraListener(object: CameraListener(){
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                service.detectFace(RequestDetect(result.data))
            }
        })

        takePictureButton.setOnClickListener {
            camera.takePicture()
        }

        switchCameraButton.setOnClickListener {
            when (camera.facing){
                Facing.FRONT -> camera.facing = Facing.BACK
                Facing.BACK -> camera.facing = Facing.FRONT
            }
        }
    }

    override fun onHttpDataAvailable(data: String) {
        if(data != "No person founded"){
            Log.d(TAG, Person.fromModelToEntity(ResponsePersonLargePersonGroup.fromJsonToModel(data)).name)
        } else {
            Log.d(TAG, "No person founded")
        }
        showLoginBottomDialog(data)
    }

    fun showLoginBottomDialog(data: String){
        val bottomSheet = BottomSheetFragment(Person(data))
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }

    override fun onFragmentInteraction(uri: Uri) {
        Log.d(TAG, "FRAGMENT INTERACTION")
    }
}
