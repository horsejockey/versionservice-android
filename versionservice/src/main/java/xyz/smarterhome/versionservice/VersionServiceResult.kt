package xyz.smarterhome.versionservice

import android.net.Uri

sealed class VersionServiceResult {
    object NoUpdate: VersionServiceResult()
    class UpdateAvailable(val uri: Uri, val currentVersion: String): VersionServiceResult()
    class UpdateRequired(val uri: Uri, val minVersion: String, val currentVersion: String?): VersionServiceResult()
    class Erred(val message: String?): VersionServiceResult()
}