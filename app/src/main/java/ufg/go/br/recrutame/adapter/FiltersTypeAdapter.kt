package ufg.go.br.recrutame.adapter

import android.support.annotation.Nullable
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import net.orange_box.storebox.adapters.base.BaseStringTypeAdapter

class FiltersTypeAdapter : BaseStringTypeAdapter<List<String>>() {
    companion object {
        private val GSON = Gson()
    }

    @Nullable
    override fun adaptForPreferences(@Nullable value: List<String>?): String? {
        return GSON.toJson(value)
    }

    @Nullable
    override fun adaptFromPreferences(@Nullable value: String?): List<String>? {
        return GSON.fromJson<List<String>>(value, object : TypeToken<List<String>>() {

        }.type)
    }
}