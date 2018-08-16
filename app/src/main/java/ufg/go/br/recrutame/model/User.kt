package rec.protelas

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

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
        var periodocargo: String? = ""
)



