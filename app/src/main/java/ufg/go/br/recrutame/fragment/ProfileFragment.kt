package ufg.go.br.recrutame.fragment

import android.annotation.TargetApi
import android.app.DatePickerDialog
import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
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
import java.text.SimpleDateFormat
import java.util.*


private lateinit var userDao: UserDao

class ProfileFragment : BaseFragment(), View.OnClickListener  {
    private lateinit var mProfileImage: CircleImageView



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inicializeApis()
        val view = inflater.inflate(R.layout.fragment_profile, container, false)



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



        inicializeControls(view)



        return view
    }

    private fun inicializeControls(view: View) {
        mProfileImage = view.findViewById(R.id.mProfileImage)

        val user = mAuth.currentUser!!
        mStorageRef.child(getUserPhotoUrl()).downloadUrl.addOnSuccessListener { task ->
            Picasso.get().load(task).into(mProfileImage)
        }.addOnFailureListener{
            Picasso.get().load(R.drawable.user_logo).into(mProfileImage)
        }

        if (user.displayName != null) {

            val nome = view.findViewById<EditText>(R.id.Nome)
            val email = view.findViewById<EditText>(R.id.Email)

            // carrega perfil se existir cadastro
            nome.setText(user.displayName)
            email.setText(user.email)
            handlePerfil(user.email!!)

            val dataNascimento = view.findViewById<EditText>(R.id.DataNascimento)
            val nacionalidade = view.findViewById<EditText>(R.id.Nacionalidade)
            val cpf = view.findViewById<EditText>(R.id.Cpf)
            val telefonefixo = view.findViewById<EditText>(R.id.Telefonefixo)
            val telefonecelular = view.findViewById<EditText>(R.id.Telefonecelular)
            val area_Atuacao = view.findViewById<EditText>(R.id.Area_Atuacao)
            val periodo = view.findViewById<EditText>(R.id.Periodo)
            val instituicao = view.findViewById<EditText>(R.id.Instituicao)
            val empresas = view.findViewById<EditText>(R.id.Empresas)
            val cargo = view.findViewById<EditText>(R.id.Cargo)
            val periodocargo = view.findViewById<EditText>(R.id.Periodocargo)
            val atividades_Desenvolvidas = view.findViewById<EditText>(R.id.Atividades_Desenvolvidas)
            val idioma = view.findViewById<EditText>(R.id.Idioma)

            val nivel_idioma = view.findViewById<MaterialSpinner>(R.id.Nivel_Idioma)
            val sexo = view.findViewById<MaterialSpinner>(R.id.Sexo)

            try {
                dateNascimento(view)
            }catch (e: Exception){
                showSnackFeedback("Erro dateNascimento", false)
            }


            var users = userDao.getUserEmail(user.email!!)
            if (users == null) {
                users = User()
            }

            nome.setText(users.nome)
            dataNascimento.setText( Mask.textMask(users.dataNascimento.toString(), "##/##/####"))
            nacionalidade.setText(users.nacionalidade)
            cpf.setText(users.cpf)
            sexo.setText(users.sexo)
            telefonefixo.setText(users.telefonefixo.toString())
            telefonecelular.setText(users.telefonecelular.toString())
            email.setText(users.email)
            area_Atuacao.setText(users.areaatuacao)
            periodo.setText(users.periodoatuacao)
            instituicao.setText(users.instituicao)
            empresas.setText(users.empresa)
            cargo.setText(users.cargo)
            periodocargo.setText(users.periodocargo)
            atividades_Desenvolvidas.setText(users.atividadesdesenvolvidas)
            idioma.setText(users.idioma)
            nivel_idioma.setText(users.nivel_idioma)
        }

        val cpf = view.findViewById<EditText>(R.id.Cpf)
        cpf.addTextChangedListener(Mask.mask("###.###.###-##", cpf))

        val btnSave = view.findViewById<TextView>(R.id.btnSave)
        btnSave.setOnClickListener(this)

        val btnRead = view.findViewById<TextView>(R.id.btnRead)
        btnRead.setOnClickListener(this)

        val btnDeleta = view.findViewById<TextView>(R.id.btnDeleta)
        btnDeleta.setOnClickListener(this)

    }


/*
    fun getSharedPreferences(): SharedPreferences {
        return this.getActivity()!!.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
    }
*/
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

    private fun dateNascimento(view: View){

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val nascimento = view.findViewById<EditText>(R.id.DataNascimento)
        nascimento.setOnClickListener{
            val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener{view, mYear, mMonth, mDay ->

            //    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
             //   fmt.timeZone = TimeZone.getTimeZone("UTC")
             //   val result = fmt.parse(""+mDay+""+mMonth+""+mYear)

            nascimento.setText(""+mDay+"/0"+mMonth+"/"+mYear)
            nascimento.addTextChangedListener(Mask.date("##/##/####", nascimento))
            },year, month, day )
            dpd.show()
        }
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
                    Nivel_Idioma.text.toString())

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


    private fun handlePerfil(email: String) {

        //   val cpf = view.findViewById<EditText>(R.id.Cpf)

        //  cpf.addTextChangedListener(Mask.mask("###.###.###-##", cpf))

        try {
            Log.d("Log do perfil", ""+userDao.getUserEmail(email));

        }catch (e: Exception){

        }

        try {
            Nome.setText(userDao.getUserEmail(email)?.nome.toString())
            DataNascimento.setText(userDao.getUserEmail(email)?.dataNascimento.toString())
            Sexo.setText(userDao.getUserEmail(email)?.sexo.toString())
            Nacionalidade.setText(userDao.getUserEmail(email)?.nacionalidade.toString())
            Cpf.setText(userDao.getUserEmail(email)?.cpf.toString())
            Sexo.setText(userDao.getUserEmail(email)?.sexo.toString())
            Telefonefixo.setText(userDao.getUserEmail(email)?.telefonefixo.toString())
            Telefonecelular.setText(userDao.getUserEmail(email)?.telefonecelular.toString())
            Email.setText(userDao.getUserEmail(email)?.email.toString())
            Area_Atuacao.setText(userDao.getUserEmail(email)?.areaatuacao.toString())
            Periodo.setText(userDao.getUserEmail(email)?.periodoatuacao.toString())
            Instituicao.setText(userDao.getUserEmail(email)?.instituicao.toString())
            Empresas.setText(userDao.getUserEmail(email)?.empresa.toString())
            Cargo.setText(userDao.getUserEmail(email)?.cargo.toString())
            Periodocargo.setText(userDao.getUserEmail(email)?.periodocargo.toString())
            Atividades_Desenvolvidas.setText(userDao.getUserEmail(email)?.atividadesdesenvolvidas.toString())
            Idioma.setText(userDao.getUserEmail(email)?.idioma.toString())

        }catch (e: Exception){
           // showSnackFeedback("Não existe cadastro",false)
        }
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
            Nivel_Idioma.setText(userDao.getById(1)?.nivel_idioma.toString())

        }catch (e: Exception){
            showSnackFeedback("Não existe cadastro",false)
        }
    }

    private fun handleSave() {
        try {
            val user = User(1,Nome.text.toString(),
                    DataNascimento.text.toString().replace("/", "").toInt(),
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
                    Nivel_Idioma.text.toString())

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