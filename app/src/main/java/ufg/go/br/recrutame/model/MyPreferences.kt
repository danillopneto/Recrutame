package ufg.go.br.recrutame.model

import net.orange_box.storebox.annotations.method.DefaultValue
import net.orange_box.storebox.annotations.method.KeyByString
import ufg.go.br.recrutame.R
import net.orange_box.storebox.annotations.method.ClearMethod

interface MyPreferences {
    @ClearMethod
    fun clear()

    @KeyByString("key_user_email")
    @DefaultValue(R.string.empty)
    fun getUserEmail(): String

    @KeyByString("key_user_email")
    fun setUserEmail(email: String)

    @KeyByString("key_maximum_distance")
    @DefaultValue(R.string.default_maximum_distance)
    fun getMaximumDistance(): String

    @KeyByString("key_maximum_distance")
    fun setMaximumDistance(distance: String)
}