package xyz.smarterhome.versionservice

internal class AppVersion(appVersion: String): Comparable<AppVersion> {
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