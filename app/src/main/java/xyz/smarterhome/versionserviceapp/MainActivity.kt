package xyz.smarterhome.versionserviceapp

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*
import xyz.smarterhome.versionservice.VersionService
import xyz.smarterhome.versionservice.VersionServiceResult
import android.content.Intent



class MainActivity : AppCompatActivity() {

    val versionService: VersionService by lazy {
        VersionService(this, "AIzaSyCSknD-fi_44ZxavSxwKWZbeBrAi8Y7uF8", "versionservice-aa663", "1:737516268806:android:141bbc88934f8ad5d60287")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        versionService.validateAppVersion(packageName, BuildConfig.VERSION_NAME) {
            when (it) {
                is VersionServiceResult.UpdateRequired -> {
                    val alertBuilder = AlertDialog.Builder(this)
                    alertBuilder.setTitle("App Update Available!")
                    alertBuilder.setMessage("A new required update is available in the Google Play Store: v${it.version}")
                    alertBuilder.setCancelable(false)
                    alertBuilder.setPositiveButton("Update Now") { _, _ ->
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, it.uri)
                        startActivity(browserIntent)
                    }
                    alertBuilder.show()
                }
                else -> {}
            }
        }
    }

}
