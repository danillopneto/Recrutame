package ufg.go.br.recrutame.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import net.orange_box.storebox.StoreBox
import ufg.go.br.recrutame.model.MyPreferences

abstract class BaseFragment : Fragment() {
    lateinit var mAuth: FirebaseAuth
    lateinit var preferences: MyPreferences

    fun inicializeApis() {
        mAuth = FirebaseAuth.getInstance()
        preferences = StoreBox.create(context, MyPreferences :: class.java)
    }
}
