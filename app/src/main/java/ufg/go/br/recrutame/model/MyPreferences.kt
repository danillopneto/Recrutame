package ufg.go.br.recrutame.model

import net.orange_box.storebox.annotations.method.ClearMethod
import net.orange_box.storebox.annotations.method.DefaultValue
import net.orange_box.storebox.annotations.method.KeyByString
import net.orange_box.storebox.annotations.method.TypeAdapter
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.adapter.FiltersTypeAdapter
import ufg.go.br.recrutame.adapter.IdiomTypeAdapter
import ufg.go.br.recrutame.adapter.SkillTypeAdapter

interface MyPreferences {
    @ClearMethod
    fun clear()

    @KeyByString("key_is_li_user")
    fun getIsLIUser(): Boolean

    @KeyByString("key_is_li_user")
    fun setIsLIUser(value: Boolean)

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

    @KeyByString("key_is_new_user")
    fun getIsNewUser() : Boolean

    @KeyByString("key_is_new_user")
    fun setIsNewUser(value: Boolean)

    @KeyByString("key_filters")
    @TypeAdapter(FiltersTypeAdapter:: class)
    fun getFilters(): List<String>

    @KeyByString("key_filters")
    @TypeAdapter(FiltersTypeAdapter:: class)
    fun setFilters(filters: List<String>)

    @KeyByString("key_skills")
    @TypeAdapter(SkillTypeAdapter:: class)
    fun getSkills(): List<String>

    @KeyByString("key_skills")
    @TypeAdapter(SkillTypeAdapter:: class)
    fun setSkills(skills: List<String>)

    @KeyByString("key_idioms")
    @TypeAdapter(IdiomTypeAdapter:: class)
    fun getIdioms(): List<String>

    @KeyByString("key_idioms")
    @TypeAdapter(IdiomTypeAdapter:: class)
    fun setIdioms(skills: List<String>)

    @KeyByString("key_user_country")
    @DefaultValue(R.string.default_country)
    fun getCountry(): String

    @KeyByString("key_user_language")
    @DefaultValue(R.string.default_language)
    fun getLanguage(): String

    @KeyByString("key_first_time_asking_permission")
    fun getPermissionsHasBeenAsked(): Boolean

    @KeyByString("key_first_time_asking_permission")
    fun setPermissionsHasBeenAsked(firstTime: Boolean)
}