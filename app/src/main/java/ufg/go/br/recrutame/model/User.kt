package rec.protelas

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


/*data class User(
        var id : Int,
        var nome: String,
        var dataNascimento: Int,
        var cpf: String,
        var sexo : String,
        var nacionalidade : String,
        var telefonefixo: Int,
        var telefonecelular: Int,
        var email: String,
        var areaatuacao: String,
        var periodoatuacao: String,
        var instituicao: String,
        var empresa: String,
        var cargo: String,
        var periodocargo: String,
        var atividadesdesenvolvidas: String,
        var idioma: String,
        var nivel_idioma: String
)
*/
/*
@Entity
data class User(
        @PrimaryKey(autoGenerate = true)
        var id : Long? =  0,
        @ColumnInfo(name = "nome")
        var nome: String? = "",
        @ColumnInfo(name = "dataNascimento")
        var dataNascimento: Int? = 0,
        @ColumnInfo(name = "cpf")
        var cpf: String? = "",
        @ColumnInfo(name = "sexo")
        var sexo : String? = "",
        @ColumnInfo(name = "nacionalidade")
        var nacionalidade : String? = "",
        @ColumnInfo(name = "telefonefixo")
        var telefonefixo: Int? = 0,
        @ColumnInfo(name = "telefonecelular")
        var telefonecelular: Int? = 0,
        @ColumnInfo(name = "email")
        var email: String? = "",
        @ColumnInfo(name = "areaatuacao")
        var areaatuacao: String? = "",
        @ColumnInfo(name = "periodoatuacao")
        var periodoatuacao: String? = "",
        @ColumnInfo(name = "instituicao")
        var instituicao: String? = "",
        @ColumnInfo(name = "empresa")
        var empresa: String? = "",
        @ColumnInfo(name = "cargo")
        var cargo: String? = "",
        @ColumnInfo(name = "periodocargo")
        var periodocargo: String? = "",
        @ColumnInfo(name = "atividadesdesenvolvidas")
        var atividadesdesenvolvidas: String? = "",
        @ColumnInfo(name = "idioma")
        var idioma: String? = "",
        @ColumnInfo(name = "nivel_idioma")
        var nivel_idioma: String? = ""

)
*/

@Entity
data class User(
        @PrimaryKey(autoGenerate = true)
        var id : Long? =  0,
        @ColumnInfo(name = "nome")
        var nome: String? = "",
        @ColumnInfo(name = "dataNascimento")
        var dataNascimento: Int? = 0,
        @ColumnInfo(name = "cpf")
        var cpf: String? = "",
        @ColumnInfo(name = "sexo")
        var sexo : String? = "",
        @ColumnInfo(name = "nacionalidade")
        var nacionalidade : String? = "",
        @ColumnInfo(name = "telefonefixo")
        var telefonefixo: Int? = 0,
        @ColumnInfo(name = "telefonecelular")
        var telefonecelular: Int? = 0,
        @ColumnInfo(name = "email")
        var email: String? = "",
        @ColumnInfo(name = "areaatuacao")
        var areaatuacao: String? = "",
        @ColumnInfo(name = "periodoatuacao")
        var periodoatuacao: String? = "",
        @ColumnInfo(name = "instituicao")
        var instituicao: String? = "",
        @ColumnInfo(name = "empresa")
        var empresa: String? = "",
        @ColumnInfo(name = "cargo")
        var cargo: String? = "",
        @ColumnInfo(name = "periodocargo")
        var periodocargo: String? = "",
        @ColumnInfo(name = "atividadesdesenvolvidas")
        var atividadesdesenvolvidas: String? = "",
        @ColumnInfo(name = "idioma")
        var idioma: String? = "",
        @ColumnInfo(name = "nivel_idioma")
        var nivel_idioma: String? = ""

)



