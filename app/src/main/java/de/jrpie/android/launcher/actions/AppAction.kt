package de.jrpie.android.launcher.actions

import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.LauncherApps
import android.graphics.Rect
import android.graphics.drawable.Drawable
import de.jrpie.android.launcher.INVALID_USER
import de.jrpie.android.launcher.R
import de.jrpie.android.launcher.getIntent
import de.jrpie.android.launcher.getLauncherActivityInfo
import de.jrpie.android.launcher.openAppSettings

class AppAction(private var appInfo: AppInfo) : Action {

    override fun invoke(context: Context, rect: Rect?): Boolean {
        val packageName = appInfo.packageName.toString()
        val user = appInfo.user
        if (user != null && user != INVALID_USER) {
            val launcherApps =
                context.getSystemService(Service.LAUNCHER_APPS_SERVICE) as LauncherApps
            getLauncherActivityInfo(packageName, user, context)?.let { app ->
                launcherApps.startMainActivity(app.componentName, app.user, rect, null)
                return true
            }
        }

        val intent = getIntent(packageName, context)

        if (intent != null) {
            context.startActivity(intent)
            return true
        }

        if (AppInfo(packageName).isInstalled(context)) {
            AlertDialog.Builder(
                context,
                R.style.AlertDialogCustom
            )
                .setTitle(context.getString(R.string.alert_cant_open_title))
                .setMessage(context.getString(R.string.alert_cant_open_message))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    openAppSettings(appInfo, context)
                }
                .setNegativeButton(android.R.string.cancel, null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show()
            return true
        }
        return false
    }

    override fun label(context: Context): String {
        return appInfo.label.toString()
    }

    override fun getIcon(context: Context): Drawable? {
        var icon: Drawable? = null
        try {
            icon = appInfo.getAppIcon(context)
        } catch (e: Exception) {
            // probably the app was uninstalled
        }
        return icon
    }

    override fun isAvailable(context: Context): Boolean {
        return appInfo.isInstalled(context)
    }

    override fun bindToGesture(editor: SharedPreferences.Editor, id: String) {
        val u = appInfo.user ?: INVALID_USER
        editor
            .putString("$id.app", appInfo.packageName.toString())
            .putInt("$id.user", u)
    }

    override fun writeToIntent(intent: Intent) {
        intent.putExtra("action_id", appInfo.packageName)
        appInfo.user?.let { intent.putExtra("user", it) }
    }
}