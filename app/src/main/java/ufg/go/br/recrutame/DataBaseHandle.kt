package rec.protelas

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteTransactionListener
import android.widget.Toast

/**
 * Created by Vinicius on 24/06/2018.
 */
val DATABASE_NAME = "RecrutameDB"
val TABLE_NAME = "Users"
val COL_ID = "Id"
val COL_Nome = "Nome"
val COL_DATANASCIMENTO = "Nascimento"
val COL_CPF = "Cpf"
val COL_SEXO = "Sexo"
val COL_NASCIONALIDADE = "Nascionalidade"
val COL_TELEFONE_FIXO = "TelefoneFixo"
val COL_TELEFONE_CELULAR = "TelefoneCelular"
val COL_EMAIL = "Email"
val COL_AREA_ATUACAO = "AreaAtuacao"
val COL_PERIODO_ATUACAO = "PeriodoAtuacao"
val COL_INSTITUICAO = "Instituicao"
val COL_EMPRESA = "Empresa"
val COL_CARGO = "Cargo"
val COL_PERIODO_CARGO = "PeriodoCargo"
val COL_ATIVIDADESDESENVOLVIDAS = "AtividadesDesenvolvidas"
val COL_IDIOMA = "Idioma"
val COL_NIVEL_IDIOMA = "NivelIdioma"


class DataBaseHandle(var context: Context) :SQLiteOpenHelper(context, DATABASE_NAME,null,1){
    override fun onCreate(db: SQLiteDatabase?) {

        val createTable = "CREATE TABLE "+ TABLE_NAME + " ("+
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COL_Nome + " VARCHAR(256), "+
                COL_DATANASCIMENTO + " INTEGER, "+
                COL_CPF + " INTEGER, "+
                COL_SEXO + " VARCHAR(40), "+
                COL_NASCIONALIDADE + " VARCHAR(40) )"+
                COL_TELEFONE_FIXO + " VARCHAR(40) )"+
                COL_TELEFONE_CELULAR + " VARCHAR(40) )"+
                COL_EMAIL + " VARCHAR(40) )"+
                COL_AREA_ATUACAO + " VARCHAR(40) )"+
                COL_PERIODO_ATUACAO + " VARCHAR(40) )"+
                COL_INSTITUICAO + " VARCHAR(40) )"+
                COL_EMPRESA + " VARCHAR(40) )"+
                COL_CARGO + " VARCHAR(40) )"+
                COL_PERIODO_CARGO + " VARCHAR(40) )"+
                COL_ATIVIDADESDESENVOLVIDAS + " VARCHAR(40) )"+
                COL_IDIOMA + " VARCHAR(40) )"+
                COL_NIVEL_IDIOMA + " VARCHAR(40) )"

    db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun insertData(user: User){
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_Nome, user.nome)
        cv.put(COL_DATANASCIMENTO, user.dataNascimento)
        cv.put(COL_CPF, user.cpf)
        cv.put(COL_SEXO, user.sexo)
        cv.put(COL_NASCIONALIDADE, user.nacionalidade)
        cv.put(COL_TELEFONE_FIXO, user.telefonefixo)
        cv.put(COL_TELEFONE_CELULAR, user.telefonecelular)
        cv.put(COL_EMAIL, user.email)
        cv.put(COL_AREA_ATUACAO, user.areaatuacao)
        cv.put(COL_PERIODO_ATUACAO, user.periodoatuacao)
        cv.put(COL_INSTITUICAO, user.instituicao)
        cv.put(COL_EMPRESA, user.empresa)
        cv.put(COL_CARGO, user.cargo)
        cv.put(COL_PERIODO_CARGO, user.periodocargo)
        cv.put(COL_ATIVIDADESDESENVOLVIDAS, user.atividadesdesenvolvidas)
        cv.put(COL_IDIOMA, user.idioma)
        cv.put(COL_NIVEL_IDIOMA, user.nivel_idioma)


        var result = db.insert(TABLE_NAME, null, cv)

        if (result == -1.toLong() )
            Toast.makeText(context, "Falha ao inserir", Toast.LENGTH_LONG).show()
        else
            Toast.makeText(context, "Inserido com sucesso", Toast.LENGTH_LONG).show()
    }

    fun readData() : MutableList<User>{

        val list : MutableList<User> = ArrayList()

        val db = this.readableDatabase
        val query = "SELECT * FROM " + TABLE_NAME
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()){
            do {
                var user = User(0,"",0,0,"","",0,
                        0,"","","","",
                "", "", "","","","")

                user.id = result.getString(result.getColumnIndex(COL_ID)).toInt()
                user.nome = result.getString(result.getColumnIndex(COL_Nome))
                user.dataNascimento = result.getString(result.getColumnIndex(COL_DATANASCIMENTO)).toInt()
                user.cpf = result.getString(result.getColumnIndex(COL_CPF)).toInt()
                user.sexo = result.getString(result.getColumnIndex(COL_SEXO))
                user.nacionalidade = result.getString(result.getColumnIndex(COL_NASCIONALIDADE))
                user.telefonefixo = result.getString(result.getColumnIndex(COL_TELEFONE_FIXO)).toInt()
                user.telefonecelular = result.getString(result.getColumnIndex(COL_TELEFONE_CELULAR)).toInt()
                user.email = result.getString(result.getColumnIndex(COL_EMAIL))
                user.areaatuacao = result.getString(result.getColumnIndex(COL_AREA_ATUACAO))
                user.periodoatuacao = result.getString(result.getColumnIndex(COL_PERIODO_ATUACAO))
                user.instituicao = result.getString(result.getColumnIndex(COL_INSTITUICAO))
                user.empresa = result.getString(result.getColumnIndex(COL_EMPRESA))
                user.cargo = result.getString(result.getColumnIndex(COL_CARGO))
                user.periodocargo = result.getString(result.getColumnIndex(COL_PERIODO_CARGO))
                user.atividadesdesenvolvidas = result.getString(result.getColumnIndex(COL_ATIVIDADESDESENVOLVIDAS))
                user.idioma = result.getString(result.getColumnIndex(COL_IDIOMA))
                user.nivel_idioma = result.getString(result.getColumnIndex(COL_NIVEL_IDIOMA))


                list.add(user)
            }while (result.moveToNext())
        }

        result.close()
        db.close()
        return list
    }


    fun updatesData(){
        val db = this.writableDatabase
        val query = "SELECT * FROM " + TABLE_NAME
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()){
            do {
                var cv = ContentValues()
                cv.put(COL_ID, result.getInt(result.getColumnIndex(COL_ID)))
                cv.put(COL_DATANASCIMENTO, result.getInt(result.getColumnIndex(COL_DATANASCIMENTO)))
                cv.put(COL_CPF, result.getInt(result.getColumnIndex(COL_CPF)))


                val update = db.update(
                        TABLE_NAME,
                        cv,
                        COL_Nome + "=? AND " + COL_SEXO + " +? AND "+ COL_NASCIONALIDADE + " +?",
                        arrayOf(result.getString(result.getColumnIndex(COL_Nome)),
                                result.getString(result.getColumnIndex(COL_SEXO)),
                                        result.getString(result.getColumnIndex(COL_NASCIONALIDADE))))
            }while (result.moveToNext())

        }
        result.close()
        db.close()
    }


    fun updatesData2(){
        val db = this.writableDatabase
        val query = "SELECT * FROM " + TABLE_NAME
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()){
            do {
                var cv = ContentValues()
                cv.put(COL_ID, result.getInt(result.getColumnIndex(COL_ID)))
                cv.put(COL_DATANASCIMENTO, result.getInt(result.getColumnIndex(COL_DATANASCIMENTO)))
                cv.put(COL_CPF, result.getInt(result.getColumnIndex(COL_CPF)))
                val update = db.update(
                        TABLE_NAME,
                        cv,
                        COL_Nome + "=? AND " + COL_SEXO + " +? AND "+ COL_NASCIONALIDADE + " +?",
                        arrayOf(result.getString(result.getColumnIndex(COL_Nome)),
                                result.getString(result.getColumnIndex(COL_SEXO)),
                                result.getString(result.getColumnIndex(COL_NASCIONALIDADE))))
            }while (result.moveToNext())
        }
        result.close()
        db.close()
    }


    fun updateUser(user: User) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_Nome, user.nome)
        cv.put(COL_DATANASCIMENTO, user.dataNascimento)
        cv.put(COL_CPF, user.cpf)
        cv.put(COL_SEXO, user.sexo)
        cv.put(COL_NASCIONALIDADE, user.nacionalidade)
        cv.put(COL_TELEFONE_FIXO, user.telefonefixo)
        cv.put(COL_TELEFONE_CELULAR, user.telefonecelular)
        cv.put(COL_EMAIL, user.email)
        cv.put(COL_AREA_ATUACAO, user.areaatuacao)
        cv.put(COL_PERIODO_ATUACAO, user.periodoatuacao)
        cv.put(COL_INSTITUICAO, user.instituicao)
        cv.put(COL_EMPRESA, user.empresa)
        cv.put(COL_CARGO, user.cargo)
        cv.put(COL_PERIODO_CARGO, user.periodocargo)
        cv.put(COL_ATIVIDADESDESENVOLVIDAS, user.atividadesdesenvolvidas)
        cv.put(COL_IDIOMA, user.idioma)
        cv.put(COL_NIVEL_IDIOMA, user.nivel_idioma)


        var result = db.update(TABLE_NAME, cv,COL_ID+" = " + user.id, null)

        if (result == (-1).toInt())
            Toast.makeText(context, "Falha ao inserir", Toast.LENGTH_LONG).show()
        else
            Toast.makeText(context, "Inserido com sucesso", Toast.LENGTH_LONG).show()


        db.close()

    }


    fun deleteData(){
        val db = this.writableDatabase
        //excluir somente o ultimo
        val query = "SELECT max("+ COL_ID +") FROM " + TABLE_NAME
        val result = db.rawQuery(query, null)
       // db.delete(TABLE_NAME, COL_ID+"=?", arrayOf(result.toString()))
        db.delete(TABLE_NAME, COL_ID+" = "+result.count, null)

        db.close()
    }


    fun deletaData(user: User){
        val db = this.writableDatabase
        db.delete(TABLE_NAME, COL_ID+"= "+user.id, null)
        db.close()
    }



}