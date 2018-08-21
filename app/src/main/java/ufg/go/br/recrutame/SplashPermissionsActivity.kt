package ufg.go.br.recrutame

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import java.util.*
import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.provider.Settings
import android.widget.ImageView

abstract class SplashPermissionsActivity : BaseActivity() {
    private val timeoutMillis = 500
    private val PERMISSIONS_REQUEST = 1234

    private val random = Random()
    private val textViewID = View.generateViewId()
    private var startTimeMillis: Long = 0
    private var textView: TextView? = null

    open fun getNextActivityClass(): Class<*> {
        return MainActivity::class.java
    }

    open fun getRequiredPermissions(): Array<String> {
        var permissions: Array<String>? = null
        try {
            permissions = packageManager.getPackageInfo(packageName,
                    PackageManager.GET_PERMISSIONS).requestedPermissions
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
        }

        return permissions?.clone() ?: arrayOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        /** Default creation code.  */
        super.onCreate(savedInstanceState)

        /** Create the layout that will hold the TextView.  */
        val mainLayout = LinearLayout(this)
        mainLayout.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        /** Add a TextView and set the initial text.  */
        textView = TextView(this)
        textView!!.textSize = 50f
        textView!!.id = textViewID
        textView!!.text = getString(R.string.waiting_permissions)
        mainLayout.addView(textView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

        /** Set the background color.  */
        val off = 128
        val rest = 256 - off
        val color = Color.argb(255, off + random.nextInt(rest), off + random.nextInt(rest), off + random.nextInt(rest))
        mainLayout.setBackgroundColor(color)

        /** Set the mainLayout as the content view  */
        setContentView(mainLayout)

        /**
         * Save the start time of this Activity, which will be used to determine
         * when the splash screen should timeout.
         */
        startTimeMillis = System.currentTimeMillis()
    }

    override fun onResume() {
        super.onResume()
        /**
         * On a post-Android 6.0 devices, check if the required permissions have
         * been granted.
         */
        checkPermissions()
    }

    /**
     * See if we now have all of the required dangerous permissions. Otherwise,
     * tell the user that they cannot continue without granting the permissions,
     * and then request the permissions again.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST) {
            checkPermissions()
        }
    }

    /*
     * ---------------------------------------------
     *
     * Other Methods
     *
     * ---------------------------------------------
     */
    /**
     * After the timeout, start the [Activity] as specified by
     * [.getNextActivityClass], and remove the splash screen from the
     * backstack. Also, we can change the message shown to the user to tell them
     * we now have the requisite permissions.
     */
    private fun startNextActivity() {
        runOnUiThread { textView!!.text = getString(R.string.permissions_granted) }
        var delayMillis = timeoutMillis - (System.currentTimeMillis() - startTimeMillis)
        if (delayMillis < 0) {
            delayMillis = 0
        }
        Handler().postDelayed({
            startActivity(Intent(this@SplashPermissionsActivity, getNextActivityClass()))
            finish()
        }, delayMillis)
    }

    /**
     * Check if the required permissions have been granted, and
     * [.startNextActivity] if they have. Otherwise
     * [.requestPermissions].
     */
    private fun checkPermissions() {
        val ungrantedPermissions = requiredPermissionsStillNeeded()
        if (ungrantedPermissions.isEmpty()) {
            startNextActivity()
        } else {
            val preferences = getMyPreferences()
            if (!preferences.getPermissionsHasBeenAsked()) {
                requestPermissions(ungrantedPermissions, PERMISSIONS_REQUEST)
                preferences.setPermissionsHasBeenAsked(true)
            } else {
                if (!shouldShowRequestPermissionRationale(ungrantedPermissions[0])) {
                    showMessageOKCancel(getString(R.string.permissions_needed),
                            DialogInterface.OnClickListener { dialog, which ->
                                startAppSettingsConfigActivity()
                            }, DialogInterface.OnClickListener { dialog, which ->
                        finishAffinity()
                    })
                } else {
                    requestPermissions(ungrantedPermissions, PERMISSIONS_REQUEST)
                }
            }
        }
    }

    /**
     * Convert the array of required permissions to a [Set] to remove
     * redundant elements. Then remove already granted permissions, and return
     * an array of ungranted permissions.
     */
    fun requiredPermissionsStillNeeded(): Array<String> {

        val permissions = HashSet<String>()
        for (permission in getRequiredPermissions()) {
            permissions.add(permission)
        }
        val i = permissions.iterator()
        while (i.hasNext()) {
            val permission = i.next()
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                Log.d(SplashPermissionsActivity::class.java.simpleName,
                        "Permission: $permission already granted.")
                i.remove()
            } else {
                Log.d(SplashPermissionsActivity::class.java.simpleName,
                        "Permission: $permission not yet granted.")
            }
        }
        return permissions.toTypedArray()
    }

    private fun showMessageOKCancel(message: String, okListener:
                                    DialogInterface.OnClickListener,
                                    cancelListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.yes), okListener)
                .setNegativeButton(getString(R.string.no), cancelListener)
                .create()
                .show()
    }

    /**
     * start the App Settings Activity so that the user can change
     * settings related to the application such as permissions.
     */
    private fun startAppSettingsConfigActivity() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}