package com.coderfeng.tooyue.ext

import com.coderfeng.tooyue.AppContext

fun getVersionName(): String {
    return AppContext.packageManager.getPackageInfo(AppContext.packageName, 0).versionName
}