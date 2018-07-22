package ufg.go.br.recrutame.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.ramotion.fluidslider.FluidSlider
import ufg.go.br.recrutame.*
import com.google.firebase.auth.EmailAuthProvider
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.*

class SettingsFragment : BaseFragment(), View.OnClickListener {
    private lateinit var maximumDistanceSlider: FluidSlider
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: ItemFilterAdapter
    private lateinit var newFilterTxt: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        inicializeApis()
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        inicializeControls(view)
        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addFilterBtn -> handleAddFilter()
            R.id.deleteAccountBtn -> handleDeleteAccount()
            R.id.logoutBtn -> handleLogout()
        }
    }

    private fun deleteAccount(password: String) {
        val user = mAuth.currentUser
        if (user != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, password)
            user.reauthenticate(credential).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(context, getString(R.string.invalid_credentials), Toast.LENGTH_LONG).show()
                    } catch (e: FirebaseNetworkException) {
                        Toast.makeText(context, getString(R.string.no_connection), Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, getString(R.string.delete_account_failed), Toast.LENGTH_LONG).show()
                        Log.e(TAG, e.message)
                    }
                } else {
                    user.delete().addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            try {
                                throw task.exception!!
                            } catch (e: FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(context, getString(R.string.invalid_credentials), Toast.LENGTH_LONG).show()
                            } catch (e: FirebaseNetworkException) {
                                Toast.makeText(context, getString(R.string.no_connection), Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, getString(R.string.delete_account_failed), Toast.LENGTH_LONG).show()
                                Log.e(TAG, e.message)
                            }
                        } else {
                            Toast.makeText(context, getString(R.string.delete_user_success), Toast.LENGTH_LONG).show()
                            logout()
                        }
                    }
                }
            }
        } else {
            Toast.makeText(context, getString(R.string.no_user_found), Toast.LENGTH_LONG).show()
        }
    }

    private fun handleAddFilter() {
        mAdapter.updateList(newFilterTxt.text.toString())
        getMyPreferences().setFilters(mAdapter.getItens())
        newFilterTxt.text = ""
    }

    private fun handleDeleteAccount() {
        val isLIUser = getMyPreferences().getIsLIUser()
        if (isLIUser) {
            handleDeleteLIUserAccount()
        } else {
            handleDeleteEmailUserAccount()
        }
    }

    private fun handleDeleteEmailUserAccount() {
        val li = LayoutInflater.from(context)
        val promptsView = li.inflate(R.layout.dialog_password, null)

        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setView(promptsView)
        val userPassword = promptsView.findViewById(R.id.passwordDialogTxt) as EditText

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK"
                ) { dialog, id ->
                    deleteAccount(userPassword.text.toString())
                }
                .setNegativeButton(getString(R.string.cancelar)
                ) { dialog, id -> dialog.cancel() }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.setTitle(R.string.deleteAccount)
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (userPassword.text.isEmpty()) {
                Toast.makeText(context, getString(R.string.confirmPassword), Toast.LENGTH_LONG).show()
            } else {
                deleteAccount(userPassword.text.toString())
                alertDialog.dismiss()
            }
        }
    }

    private fun handleDeleteLIUserAccount() {
        handleYesNoDialog(R.string.really_delete_account) { deleteAccount(LINKEDIN_PASSWORD) }
    }

    private fun handleLogout() {
        handleYesNoDialog(R.string.really_logout) { logout() }
    }

    private fun inicializeControls(view: View) {
        newFilterTxt = view.findViewById(R.id.newFilterTxt)

        setupRecycler(view)

        maximumDistanceSlider = view.findViewById(R.id.maximumDistanceSlider)
        val distance = getMyPreferences().getMaximumDistance().toFloat()
        maximumDistanceSlider.position = distance
        maximumDistanceSlider.positionListener = { p -> getMyPreferences().setMaximumDistance(p.toString()) }

        view.findViewById<ImageButton>(R.id.addFilterBtn).setOnClickListener(this)
        view.findViewById<Button>(R.id.deleteAccountBtn).setOnClickListener(this)
        view.findViewById<Button>(R.id.logoutBtn).setOnClickListener(this)
    }

    private fun logout() {
        getMyPreferences().clear()
        mAuth.signOut()
        activity!!.finishAffinity()
        val intent = Intent(activity!!.application, MainActivity::class.java)
        startActivity(intent)
    }

    private fun setupRecycler(view: View) {
        mRecyclerView = view.findViewById(R.id.recyclerFilterItems)

        val layoutManager = LinearLayoutManager(context)
        mRecyclerView.layoutManager = layoutManager

        val list: MutableList<String> = mutableListOf()
        val currentFilters = getMyPreferences().getFilters()
        if (currentFilters != null && currentFilters.isNotEmpty()) {
            list.addAll(currentFilters)
        }

        mAdapter = ItemFilterAdapter(list)
        mRecyclerView.adapter = mAdapter

        mRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }
}
