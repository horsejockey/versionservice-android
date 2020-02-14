package xyz.smarterhome.versionservice

import android.content.Context
import android.net.Uri
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

class VersionService(context: Context, apiKey: String, projectID: String, applicationID: String) {

    val fireStore: FirebaseFirestore

    companion object {
        val firebaseInstanceName = "VersionService"
    }

    init {
        try {
            FirebaseApp.getInstance(firebaseInstanceName)
        }catch(e: Throwable){
            val options = FirebaseOptions.Builder()
                .setApiKey(apiKey)
                .setApplicationId(applicationID)
                .setProjectId(projectID)
                .build()

            Firebase.initialize(context, options, firebaseInstanceName)
        }

        val versionService = FirebaseApp.getInstance(firebaseInstanceName)
        fireStore = FirebaseFirestore.getInstance(versionService)
    }

    fun validateAppVersion(bundleID: String, version: String, completion: (VersionServiceResult) -> Unit) {
        val uri = Uri.parse("https://play.google.com/store/apps/details?id=${bundleID}")

        fireStore.collection(FirebaseCollection.APP.path).get().addOnSuccessListener { result ->
            val targetApp = result.firstOrNull { it.id == bundleID }
            val minVersion = targetApp?.getString(FirebaseKey.MIN_VERSION.path)
            val currentVersion = targetApp?.getString(FirebaseKey.CURRENT_VERSION.path)
            if (minVersion == null) {
                completion(VersionServiceResult.Erred("Bad data from parse"))
            }else if (AppVersion(version) < AppVersion(minVersion)) {
                completion(VersionServiceResult.UpdateRequired(uri, minVersion, currentVersion))
            }else if (currentVersion != null && AppVersion(version) < AppVersion(currentVersion)) {
                completion(VersionServiceResult.UpdateAvailable(uri, currentVersion))
            }else{
                completion(VersionServiceResult.NoUpdate)
            }
        }.addOnFailureListener { error ->
            completion(VersionServiceResult.Erred(error.localizedMessage))
        }
    }

}