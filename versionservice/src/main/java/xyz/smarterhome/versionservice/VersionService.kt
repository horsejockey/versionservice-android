package xyz.smarterhome.versionservice

import android.content.Context
import android.net.Uri
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

private enum class FirebaseCollection(val path: String){
    APP("app")
}

private enum class FirebaseKey(val path: String){
    MIN_VERSION("androidMinVersion"),
    CURRENT_VERSION("currentVersion"),
    STORE_ID("storeID");
}

private class AppVersion(appVersion: String): Comparable<AppVersion> {
    val versionList: List<Int>

    init {
        versionList = appVersion.split(".").mapNotNull { it.toIntOrNull() }
    }

    override fun compareTo(other: AppVersion): Int {
        val countComparison = this.versionList.count().compareTo(other.versionList.count())
        if (countComparison != 0) return countComparison

        for ((index, value) in versionList.withIndex()){
            val valueComparison = value.compareTo(other.versionList[index])
            if (valueComparison != 0) return valueComparison
        }
        return 0
    }
}

sealed class VersionServiceResult {
    object NoUpdate: VersionServiceResult()
    class UpdateAvailable(val uri: Uri, val version: String): VersionServiceResult()
    class UpdateRequired(val uri: Uri, val version: String): VersionServiceResult()
    class Erred(val message: String?): VersionServiceResult()
}

class VersionService(context: Context, apiKey: String, projectID: String, applicationID: String) {

    val fireStore: FirebaseFirestore

    companion object {
        val firebaseInstanceName = "VersionService"
    }

    init {
        val options = FirebaseOptions.Builder()
            .setApiKey(apiKey)
            .setApplicationId(applicationID)
            .setProjectId(projectID)
            .build()

        Firebase.initialize(context, options, firebaseInstanceName)
        val versionService = FirebaseApp.getInstance(firebaseInstanceName)
        fireStore = FirebaseFirestore.getInstance(versionService)
    }

    fun validateAppVersion(bundleID: String, version: String, completion: (VersionServiceResult) -> Unit) {
        fireStore.collection(FirebaseCollection.APP.path).get().addOnSuccessListener { result ->
            val targetApp = result.firstOrNull { it.id == bundleID }
            val minVersion = targetApp?.getString(FirebaseKey.MIN_VERSION.path)
            if (minVersion == null) {
                completion(VersionServiceResult.Erred("Bad data from parse"))
            }else if (AppVersion(version) < AppVersion(minVersion)) {
                val uri = Uri.parse("https://play.google.com/store/apps/details?id=${bundleID}")
                completion(VersionServiceResult.UpdateRequired(uri, minVersion))
            }else{
                completion(VersionServiceResult.NoUpdate)
            }
        }.addOnFailureListener { error ->
            completion(VersionServiceResult.Erred(error.localizedMessage))
        }
    }

}