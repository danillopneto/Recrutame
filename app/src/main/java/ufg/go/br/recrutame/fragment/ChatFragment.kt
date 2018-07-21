package ufg.go.br.recrutame.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ufg.go.br.recrutame.R

class ChatFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inicializeApis()
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        return view
    }
}
