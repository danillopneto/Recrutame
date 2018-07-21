package ufg.go.br.recrutame.fragment

import android.support.v4.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import net.orange_box.storebox.StoreBox
import ufg.go.br.recrutame.model.MyPreferences

abstract class BaseFragment : Fragment() {
    lateinit var mAuth: FirebaseAuth

    fun inicializeApis() {
        mAuth = FirebaseAuth.getInstance()
    }

    fun getMyPreferences(): MyPreferences {
        return StoreBox.create(context, MyPreferences :: class.java)
    }
}
