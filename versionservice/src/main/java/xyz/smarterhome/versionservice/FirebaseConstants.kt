package xyz.smarterhome.versionservice

internal enum class FirebaseCollection(val path: String){
    APP("app")
}

internal enum class FirebaseKey(val path: String){
    MIN_VERSION("androidMinVersion"),
    CURRENT_VERSION("androidCurrentVersion");
}