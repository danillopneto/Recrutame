package ufg.go.br.recrutame.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by Vinicius on 29/07/2018.
 */
@Entity
data class Idiom(
        @PrimaryKey(autoGenerate = true)
        var id : Long? =  0,
        @ColumnInfo(name = "email")
        var email: String? = "",
        @ColumnInfo(name = "idioms")
        var idioms: String? = ""
)