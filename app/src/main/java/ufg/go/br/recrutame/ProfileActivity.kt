package ufg.go.br.recrutame

import android.os.Bundle
import android.app.Activity
import ufg.go.br.recrutame.R
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import rec.protelas.DataBaseHandle
import rec.protelas.User
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import ufg.go.br.recrutame.Util.CPFUtil
import ufg.go.br.recrutame.Util.Mask
import kotlinx.android.synthetic.main.activity_profile.*
import ufg.go.br.recrutame.R.id.activity_chooser_view_content
import ufg.go.br.recrutame.R.id.container

lateinit var db: DataBaseHandle

class ProfileActivity : Activity() , View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


      //  val view = inflater.inflate(R.layout.activity_profile, container, false)

        inicializeControls()
        //   val helper = DataBaseHandle(getContext()!!)

        //  val btn_update = findViewById(R.id.btnUpdate) as Button

        /// trocar o campo de data para formato data do android
        // olhar o programa do likedin para ver como ele separa essas informacoes

        val cpf = this!!.findViewById<EditText>(R.id.Cpf)

        cpf.addTextChangedListener(Mask.mask("###.###.###-##", cpf))

    }


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
        db = DataBaseHandle(this)

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
            R.id.Result -> handleResult()
        }
    }

    private fun handleDelete() {
        val data = db.readData()
        db.deletaData(data[0])
        btnRead.performClick()
    }

    private fun handleResult() {
        val data = db.readData()

        Result.text = ""
        for (i in 0..(data.size -1)){
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
                    ""+ data[i].idioma + "\n"
            )
        }

        val user = User(1,Nome.text.toString(),
                DataNascimento.text.toString().toInt(),
                Cpf.text.toString(),
                Sexo.text.toString(),
                Nacionalidade.text.toString(),
                Telefonefixo.text.toString().toInt(),
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

        Toast.makeText(this, "Falha ao inserir"+user, Toast.LENGTH_LONG).show()


        db.updateUser(user)
        // btn_read.performClick()
    }

    private fun handleSave() {

        val cpf = this!!.findViewById<EditText>(R.id.Cpf)

        if (CPFUtil.myValidateCPF(cpf.text.toString()))
            showSnackFeedback("CPF valid", true)
        else
            showSnackFeedback("CPF Invalid", false)


        val user = User(1,Nome.text.toString(),
                DataNascimento.text.toString().toInt(),
                Cpf.text.toString(),
                Sexo.text.toString(),
                Nacionalidade.text.toString(),
                Telefonefixo.text.toString().toInt(),
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

        db.insertData(user)

        Toast.makeText(this, "You clicked me.", Toast.LENGTH_SHORT).show()
    }

    private fun handleUpdate() {
        val data = db.readData()

        Result.text = ""
        for (i in 0..(data.size -1)){
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
    }
}
