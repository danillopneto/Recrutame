package ufg.go.br.recrutame.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import ufg.go.br.recrutame.R

class ProfileFragment : BaseFragment() {
    private lateinit var mProfileImage: CircleImageView
    private lateinit var mFullUsername: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inicializeApis()
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        inicializeControls(view)
        return view
    }

    private fun inicializeControls(view: View) {
        mProfileImage = view.findViewById(R.id.mProfileImage)
        mFullUsername = view.findViewById(R.id.mFullUsername)

        val user = mAuth.currentUser!!
        if (user.photoUrl != null) {
            Picasso.get().load(user.photoUrl).into(mProfileImage)
        }

        if (user.displayName != null) {
            mFullUsername.text = user.displayName
        }

      /*  if (user.displayName != null) {
            mFullUsername.setText(user.displayName)
        }else
            mFullUsername.setText(" não encontrado")
        */
    }
}