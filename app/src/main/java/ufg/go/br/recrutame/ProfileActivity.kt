package ufg.go.br.recrutame

import android.os.Bundle
import android.app.Activity
import android.arch.persistence.room.Room
import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import rec.protelas.User
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import ufg.go.br.recrutame.Util.Mask
import kotlinx.android.synthetic.main.activity_profile.*
import ufg.go.br.recrutame.dao.AppDb
import ufg.go.br.recrutame.dao.UserDao
import kotlin.concurrent.thread

private lateinit var userDao: UserDao

//private var userDb: AppDb? = null

class ProfileActivity : Activity() , View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

      //  userDb = AppDb.getInstance(this)

      // provideAppDatabase(this)

       val database =  Room.databaseBuilder(applicationContext, AppDb::class.java, "userDb")
               .allowMainThreadQueries()
               .fallbackToDestructiveMigration()
               .build()

        userDao = database.userDao()

        inicializeControls()

        /// trocar o campo de data para formato data do android
        // olhar o programa do likedin para ver como ele separa essas informacoes

        val cpf = this!!.findViewById<EditText>(R.id.Cpf)

        cpf.addTextChangedListener(Mask.mask("###.###.###-##", cpf))

    }

  //  fun provideAppDatabase(applicationContext: Context): AppDb {
 //       return Room.databaseBuilder(applicationContext, AppDb::class.java, "banco.db").build()
   // }


    fun showSnackFeedback(message : String, isValid : Boolean){
        val snackbar : Snackbar = Snackbar.make(profileactivity!!, message, Snackbar.LENGTH_SHORT)
        var v : View = snackbar.view
        if (isValid)
            v.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        else
            v.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))

        snackbar.show()
    }


    private fun inicializeControls() {
        //db = DataBaseHandle(this)

        val result = this!!.findViewById<TextView>(R.id.Result)
        result.setOnClickListener(this)

        val btnSave = this!!.findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener(this)

        val btnRead = this!!.findViewById<Button>(R.id.btnRead)
        btnRead.setOnClickListener(this)

        val btnDeleta = this!!.findViewById<Button>(R.id.btnDeleta)
        btnDeleta.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnDeleta -> handleDelete()
            R.id.btnSave  -> handleSave()
            R.id.btnUpdate -> handleUpdate()
            R.id.btnRead -> handleResult()
        }
    }

    private fun handleUpdate(){

        try {
            val user = User(1,Nome.text.toString(),
                    DataNascimento.text.toString().toInt(),
                    Cpf.text.toString(),
                    Sexo.text.toString(),
                    Nacionalidade.text.toString(),
                    Integer.parseInt(Telefonefixo.text.toString()),
                    Telefonecelular.text.toString().toInt(),
                    Email.text.toString(),
                    Area_Atuacao.text.toString(),
                    Periodo.text.toString(),
                    Instituicao.text.toString(),
                    Empresas.text.toString(),
                    Cargo.text.toString(),
                    Periodocargo.text.toString(),
                    Atividades_Desenvolvidas.text.toString(),
                    Idioma.text.toString(),
                    "")

            userDao.update(user)
            val cpf = this!!.findViewById<EditText>(R.id.Cpf)
            cpf.addTextChangedListener(Mask.mask("###.###.###-##", cpf))
            showSnackFeedback("Atualizado com sucesso!",false)

        }catch (e: Exception){
            showSnackFeedback("Erro ao atualizar",false)
        }

    }

    private fun handleDelete(){

        userDao.delete()
    }

    private fun handleResult() {

        Result.text = ""

      //  Result.append(userDb?.userDao()?.all().toString())

       // Result.append(userDb?.userDao()?.loadAllUsers().toString())

      //  Result.append(userDb?.userDao()?.findUserEmail("bakural3000@gmail.com").toString())


        Toast.makeText(this, ""+ userDao.getById(1)?.nome.toString(), Toast.LENGTH_LONG).show()
        //  userDb?.userDao()?.loadAllUsers()?.forEach { e -> Log.e("", e.nome.toString()) }
        try {
            Nome.setText(userDao.getById(1)?.nome.toString())
            DataNascimento.setText(userDao.getById(1)?.dataNascimento.toString())
            Sexo.setText(userDao.getById(1)?.sexo.toString())
            Nacionalidade.setText(userDao.getById(1)?.nacionalidade.toString())
            Cpf.setText(userDao.getById(1)?.cpf.toString())
            Telefonefixo.setText(userDao.getById(1)?.telefonefixo.toString())
            Telefonecelular.setText(userDao.getById(1)?.telefonecelular.toString())
            Email.setText(userDao.getById(1)?.email.toString())
            Area_Atuacao.setText(userDao.getById(1)?.areaatuacao.toString())
            Periodo.setText(userDao.getById(1)?.periodoatuacao.toString())
            Instituicao.setText(userDao.getById(1)?.instituicao.toString())
            Empresas.setText(userDao.getById(1)?.empresa.toString())
            Cargo.setText(userDao.getById(1)?.cargo.toString())
            Periodocargo.setText(userDao.getById(1)?.periodocargo.toString())
            Atividades_Desenvolvidas.setText(userDao.getById(1)?.atividadesdesenvolvidas.toString())
            Idioma.setText(userDao.getById(1)?.idioma.toString())

            }catch (e: Exception){
            showSnackFeedback("Não existe cadastro",false)
            }
    }

    private fun handleSave() {
        try {
        val user = User(1,Nome.text.toString(),
                DataNascimento.text.toString().toInt(),
                Cpf.text.toString(),
                Sexo.text.toString(),
                Nacionalidade.text.toString(),
                Integer.parseInt(Telefonefixo.text.toString()),
                Telefonecelular.text.toString().toInt(),
                Email.text.toString(),
                Area_Atuacao.text.toString(),
                Periodo.text.toString(),
                Instituicao.text.toString(),
                Empresas.text.toString(),
                Cargo.text.toString(),
                Periodocargo.text.toString(),
                Atividades_Desenvolvidas.text.toString(),
                Idioma.text.toString(),
                "")

            userDao.add(user)

        Toast.makeText(this, ""+user.toString(), Toast.LENGTH_LONG).show()
        Log.d("Inserirdo", user.toString());

        }catch (e: Exception){
            Toast.makeText(this, ""+e.message, Toast.LENGTH_LONG).show()
            Log.d("Erro ao buscar", e.toString());
        }


    }


    private fun insertUserDb(user: User) {
       // val task = Runnable { userDb?.userDao()?.add(user)
      //  val task = Runnable { userDao.add(user)}
        userDao.add(user)
    }

/*
    private fun handleUpdate() {
        val data = db.readData()
        Result.text = ""
    /*    for (i in 0..(data.size -1)){
            Result.append(data[i].id.toString() + "\n" +
                    ""+ data[i].nome + "\n"+
                    ""+ data[i].dataNascimento + "\n"+
                    ""+ data[i].sexo + "\n"+
                    ""+ data[i].nacionalidade + "\n"+
                    ""+ data[i].telefonefixo + "\n"+
                    ""+ data[i].telefonecelular + "\n"+
                    ""+ data[i].email + "\n"+
                    ""+ data[i].areaatuacao + "\n"+
                    ""+ data[i].periodoatuacao + "\n"+
                    ""+ data[i].instituicao + "\n"+
                    ""+ data[i].empresa + "\n"+
                    ""+ data[i].cargo + "\n"+
                    ""+ data[i].periodocargo + "\n"+
                    ""+ data[i].atividadesdesenvolvidas + "\n"+
                    ""+ data[i].idioma + "\n"+
                    ""+ data[i].nivel_idioma + "\n"
            )
        }

       val user = User(1, data[0].nome, data[0].dataNascimento, data[0].cpf, data[0].sexo, data[0].nacionalidade,
                data[0].telefonefixo, data[0].telefonecelular, data[0].email, data[0].areaatuacao,
                data[0].periodoatuacao, data[0].instituicao, data[0].empresa, data[0].cargo,
                data[0].periodocargo, data[0].atividadesdesenvolvidas, data[0].idioma, data[0].nivel_idioma )

        Nome.setText(data[0].nome)
        DataNascimento.setText(data[0].dataNascimento.toString())
        Sexo.setText(data[0].sexo)
        Nacionalidade.setText(data[0].nacionalidade)

        */

*/

}
