package ufg.go.br.recrutame.fragment

import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.jaredrummler.materialspinner.MaterialSpinner
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.*
import rec.protelas.User
import ufg.go.br.recrutame.BuildConfig
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.Util.Mask
import ufg.go.br.recrutame.dao.AppDb
import ufg.go.br.recrutame.dao.UserDao


private lateinit var userDao: UserDao

class ProfileFragment : BaseFragment(), View.OnClickListener  {
    private lateinit var mProfileImage: CircleImageView
    private lateinit var mFullUsername: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inicializeApis()
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        inicializeControls(view)

        //  userDb = AppDb.getInstance(this)

        // provideAppDatabase(this)

        spinnerProfissional(view)
        spinnerNivelIdioma(view)
        spinnerSexo(view)



     //  val prefs = application.getSharedPreferences(
     //           BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

      //  val email =  prefs.getString("oauth.accesstoken", "")
    //    val email2 =  prefs.getString("OAUTH_LOGGEDIN", "")



      //  Toast.makeText(this, "Login: "+email+""+email2+""+ FirebaseAuth.getInstance().currentUser.toString(), Toast.LENGTH_LONG).show()



        // Toast.makeText(this, "Email: "+email+""+email2, Toast.LENGTH_LONG).show()



        val database =  Room.databaseBuilder(this.getActivity()!!, AppDb::class.java, "userDb")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()

        userDao = database.userDao()






        return view
    }

    private fun inicializeControls(view: View) {
        mProfileImage = view.findViewById(R.id.mProfileImage)
        mFullUsername = view.findViewById(R.id.mFullUsername)

        val user = mAuth.currentUser!!
        if (user.photoUrl != null) {
            Picasso.get().load(user.photoUrl).into(mProfileImage)
        }

        if (user.displayName != null) {
            mFullUsername.text = user.displayName
        }

      /*  if (user.displayName != null) {
            mFullUsername.setText(user.displayName)
        }else
            mFullUsername.setText(" não encontrado")
        */

        val cpf = view.findViewById<EditText>(R.id.Cpf)
        cpf.addTextChangedListener(Mask.mask("###.###.###-##", cpf))

        val btnSave = view.findViewById<TextView>(R.id.btnSave)
        btnSave.setOnClickListener(this)

        val btnRead = view.findViewById<TextView>(R.id.btnRead)
        btnRead.setOnClickListener(this)

        val btnDeleta = view.findViewById<TextView>(R.id.btnDeleta)
        btnDeleta.setOnClickListener(this)

    }



    fun getSharedPreferences(): SharedPreferences {
        return this.getActivity()!!.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
    }

    fun showSnackFeedback(message : String, isValid : Boolean){
        val snackbar : Snackbar = Snackbar.make(getActivity()!!.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
        var v : View = snackbar.view
        if (isValid)
            v.setBackgroundColor(ContextCompat.getColor(this!!.context!! , android.R.color.holo_green_dark))
        else
            v.setBackgroundColor(ContextCompat.getColor(this!!.context!!, android.R.color.holo_red_dark))

        snackbar.show()
    }

    private fun spinnerProfissional(view: View){
        val spinner = view.findViewById<View>(R.id.Nivel_Idioma) as MaterialSpinner
        spinner.setItems("Profissional" , "Superior" , "Técnico" , "Sem Escolaridade")
        spinner.setOnItemSelectedListener { view , position , id , item -> Snackbar.make(view , "Selecionado " + item , Snackbar.LENGTH_LONG).show() }
    }


    private fun spinnerNivelIdioma(view: View){
        val spinner = view.findViewById<View>(R.id.Nivel_Idioma) as MaterialSpinner
        spinner.setItems("Básico" , "Intermediário" , "Avançado" , "Fluente")
        spinner.setOnItemSelectedListener { view , position , id , item -> Snackbar.make(view , "Selecionado " + item , Snackbar.LENGTH_LONG).show() }
    }

    private fun spinnerSexo(view: View){
        val spinner = view.findViewById<View>(R.id.Sexo) as MaterialSpinner
        spinner.setItems("Masculino" , "Feminino")
        spinner.setOnItemSelectedListener { view , position , id , item -> Snackbar.make(view , "Selecionado " + item , Snackbar.LENGTH_LONG).show() }
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

            userDao.update(user = user)
          //  val cpf = this!!.findViewById<EditText>(R.id.Cpf)
            Cpf.addTextChangedListener(Mask.mask("###.###.###-##", Cpf))
            showSnackFeedback("Atualizado com sucesso!",false)

        }catch (e: Exception){
            showSnackFeedback("Erro ao atualizar",false)
        }

    }

    private fun handleDelete(){

        userDao.delete()
    }

    private fun handleResult() {

     //   val cpf = view.findViewById<EditText>(R.id.Cpf)

      //  cpf.addTextChangedListener(Mask.mask("###.###.###-##", cpf))

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

            try {
                userDao.add(user)
            }catch (e: Exception){
                userDao.update(user = user)
            }





            Toast.makeText( context , ""+user.toString(), Toast.LENGTH_LONG).show()
            Log.d("Inserirdo", user.toString());

        }catch (e: Exception){
            Toast.makeText(context, ""+e.message, Toast.LENGTH_LONG).show()
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
