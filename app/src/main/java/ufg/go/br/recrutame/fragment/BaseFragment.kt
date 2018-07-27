package ufg.go.br.recrutame.fragment

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import net.orange_box.storebox.StoreBox
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.model.MyPreferences
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import android.app.ProgressDialog

abstract class BaseFragment : Fragment() {
    lateinit var mAuth: FirebaseAuth

    lateinit var mStorageRef: StorageReference
    private val PICK_IMAGE_REQUEST = 71

    fun getMyPreferences(): MyPreferences {
        return StoreBox.create(context, MyPreferences::class.java)
    }

    fun handleYesNoDialog(message: Int, yesAction: () -> Unit) {
        val li = LayoutInflater.from(context)
        val promptsView = li.inflate(R.layout.dialog_yes_no, null)
        promptsView.findViewById<TextView>(R.id.questionTxt).text = getString(message)
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setView(promptsView)
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes)
                ) { dialog, id ->
                    yesAction()
                }
                .setNegativeButton(getString(R.string.no)
                ) { dialog, id -> dialog.cancel() }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun inicializeApis() {
        mAuth = FirebaseAuth.getInstance()
        mStorageRef = FirebaseStorage.getInstance().reference
    }

    fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), PICK_IMAGE_REQUEST)
    }

    private fun uploadImage(filePath: Uri?, destinationPath: String) {
        if (filePath != null) {
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle(getString(R.string.Uploading))
            progressDialog.show()

            val ref = mStorageRef.child(destinationPath)
            ref.putFile(filePath!!)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        Toast.makeText(context, getString(R.string.uploaded), Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(context, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show()
                    }
                    .addOnProgressListener { taskSnapshot ->
                        val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                                .totalByteCount
                        progressDialog.setMessage("${progress.toInt()}%")
                    }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.data != null) {
            uploadImage(data.data, getUserPhotoUrl())
        }
    }

    fun getUserPhotoUrl(): String {
        return "users/${mAuth.currentUser?.uid}/profile_picture"
    }
}
