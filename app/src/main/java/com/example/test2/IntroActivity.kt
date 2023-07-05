package com.example.test2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro_activity) //xml , java 소스 연결
        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(applicationContext, MainActivity::class.java)
            requestPermissions()
            startActivity(intent) //인트로 실행 후 바로 MainActivity로 넘어감.
            finish()
        }, 1000) //1초 후 인트로 실행
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    private fun requestPermissions(): Boolean {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            return true
        }

        val permissions: Array<String> = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_MEDIA_IMAGES)

        ActivityCompat.requestPermissions(this, permissions, 0)
        return false
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            0 -> {
                if (grantResults.isNotEmpty()){
                    var isAllGranted = true
                    // 요청한 권한 허용/거부 상태 한번에 체크
                    for (grant in grantResults) {
                        if (grant != PackageManager.PERMISSION_GRANTED) {
                            isAllGranted = false
                            break;
                        }
                    }

                    // 요청한 권한을 모두 허용했음.
                    if (isAllGranted) {
                        // 다음 step으로 ~
                    }
                    // 허용하지 않은 권한이 있음. 필수권한/선택권한 여부에 따라서 별도 처리를 해주어야 함.
                    else {
                        if(!ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                            || !ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.CAMERA)){
                            // 다시 묻지 않기 체크하면서 권한 거부 되었음.
                        } else {
                            // 접근 권한 거부하였음.
                        }
                    }
                }
            }
        }
    }
}