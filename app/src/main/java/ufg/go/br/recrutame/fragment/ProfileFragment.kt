package ufg.go.br.recrutame.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.arch.persistence.room.Room
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.jaredrummler.materialspinner.MaterialSpinner
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import rec.protelas.User
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.Util.Mask
import ufg.go.br.recrutame.Util.Utils
import ufg.go.br.recrutame.adapter.ItemIdiomaAdapter
import ufg.go.br.recrutame.adapter.ItemProfileAdapter
import ufg.go.br.recrutame.dao.*
import ufg.go.br.recrutame.model.Idiom
import ufg.go.br.recrutame.model.Skill
import java.text.SimpleDateFormat
import java.util.*


private lateinit var userDao: UserDao
private lateinit var skillDao: SkillDao
private lateinit var idiomDao: IdiomDao
private lateinit var mRecyclerView: RecyclerView
private lateinit var mAdapterAtividade: ItemProfileAdapter
private lateinit var newAtividadesTxt: TextView
private lateinit var mAdapterIdioma: ItemIdiomaAdapter
private lateinit var newIdiomaTxt: TextView

class ProfileFragment : BaseFragment(), View.OnClickListener  {
    private lateinit var mProfileImage: CircleImageView

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onImageEvent(image: Uri?) {
        Picasso.get().load(image).into(mProfileImage)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inicializeApis()
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        view.findViewById<FloatingActionButton>(R.id.mChangePictureBtn).setOnClickListener(this)

        spinnerSexo(view)


        val database =  Room.databaseBuilder(this.getActivity()!!, AppDb::class.java, "userDb")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()

        userDao = database.userDao()


        val databaseSkill =  Room.databaseBuilder(this.getActivity()!!, AppDbSkill::class.java, "skillDb")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()

        skillDao = databaseSkill.skillDao()



        val databaseIdiom =  Room.databaseBuilder(this.getActivity()!!, AppDbIdiom::class.java, "idiomDb")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()

        idiomDao = databaseIdiom.idiomDao()

        inicializeControls(view)

        return view
    }

    private fun inicializeControls(view: View) {
        mProfileImage = view.findViewById(R.id.mProfileImage)
        newAtividadesTxt = view.findViewById(R.id.Atividades_Desenvolvidas)
        newIdiomaTxt = view.findViewById(R.id.Idioma)

        val user = mAuth.currentUser!!
        mStorageRef.child(getUserPhotoUrl()).downloadUrl.addOnSuccessListener { task ->
            EventBus.getDefault().post(task)
        }.addOnFailureListener{
            EventBus.getDefault().post(R.drawable.user_logo)
        }

        if (user.displayName != null) {

            val nome = view.findViewById<EditText>(R.id.Nome)
            val email = view.findViewById<EditText>(R.id.Email)

            nome.setText(user.displayName)
            email.setText(user.email)
            handlePerfil(user.email.toString(), view)

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
            val sexo = view.findViewById<MaterialSpinner>(R.id.Sexo)

            try {
                dataNascimento.setInputType(0);
                dateNascimento(view)
                dataNascimento.setInputType(0);
            }catch (e: Exception){
                showSnackFeedback("Erro dateNascimento", false)
            }

/*
            var users = userDao.getUserEmail(user.email!!)
            if (users == null) {
                users = User()
            }

            nome.setText(users.nome)
            email.setText(users.email)
            dataNascimento.setText( Mask.textMask(users.dataNascimento.toString(), "##/##/####"))
            nacionalidade.setText(users.nacionalidade)
            cpf.setText(users.cpf)
            sexo.setText(users.sexo)
            telefonefixo.setText(users.telefonefixo.toString())
            telefonecelular.setText(users.telefonecelular.toString())
            area_Atuacao.setText(users.areaatuacao)
            periodo.setText(users.periodoatuacao)
            instituicao.setText(users.instituicao)
            empresas.setText(users.empresa)
            cargo.setText(users.cargo)
            periodocargo.setText(users.periodocargo)
*/
            atividadesRecycler(view)
            idiomaRecycler(view)
        }

        val cpf = view.findViewById<EditText>(R.id.Cpf)
        cpf.addTextChangedListener(Mask.mask("###.###.###-##", cpf))

        view.findViewById<TextView>(R.id.btnSave).setOnClickListener(this)
        view.findViewById<TextView>(R.id.btnDeleta).setOnClickListener(this)
        view.findViewById<ImageButton>(R.id.btnAddAtividade).setOnClickListener(this)
        view.findViewById<ImageButton>(R.id.btnAddIdioma).setOnClickListener(this)

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
            nascimento.setInputType(0);
            var cal = Calendar.getInstance()
            val dpd = DatePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_MinWidth , DatePickerDialog.OnDateSetListener{view, mYear, mMonth, mDay ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, mMonth)
                cal.set(Calendar.DAY_OF_MONTH, mDay)
                val myFormat = "dd/MM/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)

                nascimento.setText(sdf.format(cal.time))
                nascimento.setInputType(0);
               // nascimento.addTextChangedListener(Mask.date("##/##/####", nascimento))
            },year, month, day )
            dpd.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dpd.show()
            nascimento.setInputType(0);
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAddAtividade -> handleAddAtividades()
            R.id.btnAddIdioma -> handleAddIdioma()
            R.id.btnDeleta -> handleDelete()
            R.id.btnSave  -> handleSave()
            R.id.mChangePictureBtn -> chooseImage()
        }
    }

    private fun handleAddAtividades(){
        if (Utils.isNullOrWhiteSpace(Atividades_Desenvolvidas.text.toString())) {
            Toast.makeText(context, getString(R.string.insert_filter_term), Toast.LENGTH_SHORT).show()
        } else {
            mAdapterAtividade.updateList(Atividades_Desenvolvidas.text.toString().trim())
            Atividades_Desenvolvidas.setText("")
        }
    }


    private fun atividadesRecycler(view: View) {
        mRecyclerView = view.findViewById(R.id.recyclerAtividadesItems)

        val layoutManager = LinearLayoutManager(context)
        mRecyclerView.layoutManager = layoutManager


        val user = mAuth.currentUser!!
        mStorageRef.child(getUserPhotoUrl()).downloadUrl.addOnSuccessListener { task ->
            EventBus.getDefault().post(task)
        }.addOnFailureListener{
            EventBus.getDefault().post(R.drawable.user_logo)
        }

        var users = userDao.getUserEmail(user.email!!)
        if (users == null) {
            users = User()
        }

        Log.i("Email Do usuario logado: ", ""+users.email )

        val list: MutableList<String> = mutableListOf()
        // a lista de habilidades vem daqui
        val currentFilters = getMyPreferences().getSkills()

        skillDao.all(users.email!!).forEach{ skills ->  Log.i("String de skilldao: ", ""+list.add(""+skills.skill))  }

        skillDao.deleteWithFriends(Skill() , skillDao.all(users.email!!))

        mAdapterAtividade = ItemProfileAdapter(list)
        mRecyclerView.adapter = mAdapterAtividade
        mAdapterAtividade.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                getMyPreferences().setSkills(mAdapterAtividade.getItens())
                Log.d("Log do getItens", ""+mAdapterAtividade.getItens())
            }
        })

        mRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))


        if (currentFilters != null && currentFilters.isNotEmpty()) {

            currentFilters.forEach { skill ->

                try {
                    if(skillDao.getSkillReplace(""+users.email, skill.toString())==null) {
                        val sl = Skill((skillDao.getNumberOfRows().toLong() + 1) , users.email , skill.toString())
                        skillDao.add(sl)
                        Log.i("Habilidades em banco: " , "" + sl.skill)
                    }
                }catch (e: Exception){
                    if(skillDao.getSkillReplace(""+users.email, skill.toString())==null) {
                        val sl = Skill((skillDao.getNumberOfRows().toLong() + 1) , users.email , skill.toString())
                        skillDao.update(sl)
                        Log.i("Habilidades em banco: " , "" + sl.skill)
                    }
                }
            }
        }
    }


    private fun handleAddIdioma(){

        var idiomaSelect = ""
        val user = mAuth.currentUser!!

        var users = userDao.getUserEmail(user.email!!)
        if (users == null) {
            users = User()
        }

        val currentFilters = getMyPreferences().getIdioms()

        val list: MutableList<String> = mutableListOf()

        idiomDao.all(users.email!!).forEach{ idioms ->  Log.i("String de skilldao: ", ""+list.add(""+idioms.idioms))  }

        idiomDao.deleteWithFriends(Idiom() , idiomDao.all(users.email!!))

        if (currentFilters != null && currentFilters.isNotEmpty()) {

            currentFilters.forEach { idiom ->

                try {
                    if(idiomDao.getIdiomReplace(""+users.email, idiom.toString())==null) {
                        val sl = Idiom((idiomDao.getNumberOfRows().toLong() + 1) , users.email , idiom.toString())
                        idiomDao.add(sl)
                        Log.i("Habilidades em banco: " , "" + sl.idioms)
                    }
                }catch (e: Exception){
                    if(idiomDao.getIdiomReplace(""+users.email, idiom.toString())==null) {
                        val sl = Idiom((idiomDao.getNumberOfRows().toLong() + 1) , users.email , idiom.toString())
                        idiomDao.update(sl)
                        Log.i("Habilidades em banco: " , "" + sl.idioms)
                    }
                }
            }
        }

        if (Utils.isNullOrWhiteSpace(newIdiomaTxt.text.toString())) {
            Toast.makeText(context, getString(R.string.insert_filter_term), Toast.LENGTH_SHORT).show()
        } else {

            val builder =  AlertDialog.Builder(getActivity()!!)
            val view = LayoutInflater.from(getActivity()!!).inflate(R.layout.dialog_idiom, null)
            val spinner = view.findViewById<View>(R.id.Nivel_Idioma) as MaterialSpinner

            spinner.setOnItemSelectedListener { view , position , id , item ->
                idiomaSelect = ""+item }

            val adapter = ArrayAdapter.createFromResource(getActivity()!!, R.array.nivel_idioma,
                    android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(adapter);



            builder.setView(view);

            builder.setTitle("Nível")
            builder.setMessage("Qual a fluência no idioma: ")
            builder.setPositiveButton("Salvar", { dialog, whichButton ->

                spinner.setInputType(0);
                val sl = Idiom((idiomDao.getNumberOfRows().toLong() + 1) , users.email , newIdiomaTxt.text.toString().trim()+" - "+idiomaSelect )
                idiomDao.add(sl)
                mAdapterIdioma.updateList(newIdiomaTxt.text.toString().trim()+" - "+idiomaSelect)
                newIdiomaTxt.setText("")
            })
            builder.setNegativeButton("Cancelar", { dialog, whichButton ->
                //pass
            })
            val b = builder.create()
            b.show()

        }
    }

    private fun idiomaRecycler(view: View) {
        mRecyclerView = view.findViewById(R.id.recyclerIdiomaItems)

        val layoutManager = LinearLayoutManager(context)
        mRecyclerView.layoutManager = layoutManager

        val user = mAuth.currentUser!!
        mStorageRef.child(getUserPhotoUrl()).downloadUrl.addOnSuccessListener { task ->
            EventBus.getDefault().post(task)
        }.addOnFailureListener{
            EventBus.getDefault().post(R.drawable.user_logo)
        }

        var users = userDao.getUserEmail(user.email!!)
        if (users == null) {
            users = User()
        }

        Log.i("Email Do usuario logado: ", ""+users.email )

        val list: MutableList<String> = mutableListOf()
        // a lista de habilidades vem daqui
        val currentFilters = getMyPreferences().getIdioms()

        idiomDao.all(users.email!!).forEach{ idioms ->  Log.i("String de skilldao: ", ""+list.add(""+idioms.idioms))  }

        idiomDao.deleteWithFriends(Idiom() , idiomDao.all(users.email!!))

        mAdapterIdioma = ItemIdiomaAdapter(list)
        mRecyclerView.adapter = mAdapterIdioma
        mAdapterIdioma.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                getMyPreferences().setIdioms(mAdapterIdioma.getItens())
                Log.d("Log do getItens", ""+mAdapterIdioma.getItens())
            }
        })

        mRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))


        if (currentFilters != null && currentFilters.isNotEmpty()) {

            currentFilters.forEach { idiom ->

                try {
                    if(idiomDao.getIdiomReplace(""+users.email, idiom.toString())==null) {
                        val sl = Idiom((idiomDao.getNumberOfRows().toLong() + 1) , users.email , idiom.toString())
                        idiomDao.add(sl)
                        Log.i("Habilidades em banco: " , "" + sl.idioms)
                    }
                }catch (e: Exception){
                    if(idiomDao.getIdiomReplace(""+users.email, idiom.toString())==null) {
                        val sl = Idiom((idiomDao.getNumberOfRows().toLong() + 1) , users.email , idiom.toString())
                        idiomDao.update(sl)
                        Log.i("Habilidades em banco: " , "" + sl.idioms)
                    }
                }
            }
        }


    }

    private fun handleDelete(){
        userDao.delete()
    }

    private fun handlePerfil(emails: String, view: View) {

        try {
            val nome = view.findViewById<EditText>(R.id.Nome)
            val email = view.findViewById<EditText>(R.id.Email)
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
            val sexo = view.findViewById<MaterialSpinner>(R.id.Sexo)

            var users = userDao.getUserEmail(emails)
            if (users == null) {
                users = User()
            }

            nome.setText(users.nome)
            email.setText(users.email)
            dataNascimento.setText( Mask.textMask(users.dataNascimento.toString(), "##/##/####"))
            nacionalidade.setText(users.nacionalidade)
            cpf.setText(users.cpf)
            sexo.setText(users.sexo)
            telefonefixo.setText(users.telefonefixo.toString())
            telefonecelular.setText(users.telefonecelular.toString())
            area_Atuacao.setText(users.areaatuacao)
            periodo.setText(users.periodoatuacao)
            instituicao.setText(users.instituicao)
            empresas.setText(users.empresa)
            cargo.setText(users.cargo)
            periodocargo.setText(users.periodocargo)

        }catch (e: Exception){
            showSnackFeedback("Não existe cadastro",false)
        }
    }

    private fun handleSave() {


        if (verificaCampovazio(Nome.text.toString())) {
            Nome.validate("\\d+", "Campo obrigatório*")
            Nome.requestLayout()
            showSnackFeedback("Preencha todos os campos obrigatórios", false)
        } else if (!emailValidar( Email.text.toString())) {
            Email.validate("\\d+", "Campo obrigatório*")
            Email.requestLayout()
            showSnackFeedback("Preencha todos os campos obrigatórios", false)
        } else if (verificaCampovazio(DataNascimento.text.toString())) {
            DataNascimento.validate("\\d+", "Campo obrigatório*")
            DataNascimento.requestLayout()
            showSnackFeedback("Preencha todos os campos obrigatórios", false)
        } else if (verificaCampovazio(Nacionalidade.text.toString())) {
            Nacionalidade.validate("\\d+", "Campo obrigatório*")
            Nacionalidade.requestLayout()
            showSnackFeedback("Preencha todos os campos obrigatórios", false)
        } else if (verificaCampovazio(Cpf.text.toString())) {
            Cpf.validate("\\d+", "Campo obrigatório*")
            Cpf.requestLayout()
            showSnackFeedback("Preencha todos os campos obrigatórios", false)
        } else if (verificaCampovazio(Telefonefixo.text.toString())) {
            Telefonefixo.validate("\\d+", "Campo obrigatório*")
            Telefonefixo.requestLayout()
            showSnackFeedback("Preencha todos os campos obrigatórios", false)
        } else if (verificaCampovazio(Telefonecelular.text.toString())) {
            Telefonecelular.validate("\\d+", "Campo obrigatório*")
            Telefonecelular.requestLayout()
            showSnackFeedback("Preencha todos os campos obrigatórios", false)
        } else if (verificaCampovazio(Area_Atuacao.text.toString())) {
            Area_Atuacao.validate("\\d+", "Campo obrigatório*")
            Area_Atuacao.requestLayout()
            showSnackFeedback("Preencha todos os campos obrigatórios", false)
        } else if (verificaCampovazio(Periodo.text.toString())) {
            Periodo.validate("\\d+", "Campo obrigatório*")
            Periodo.requestLayout()
            showSnackFeedback("Preencha todos os campos obrigatórios", false)
        } else if (verificaCampovazio(Instituicao.text.toString())) {
            Instituicao.validate("\\d+", "Campo obrigatório*")
            Instituicao.requestLayout()
            showSnackFeedback("Preencha todos os campos obrigatórios", false)
        } else if (verificaCampovazio(Empresas.text.toString())) {
            Empresas.validate("\\d+", "Campo obrigatório*")
            Empresas.requestLayout()
            showSnackFeedback("Preencha todos os campos obrigatórios", false)
        } else if (verificaCampovazio(Cargo.text.toString())) {
            Cargo.validate("\\d+", "Campo obrigatório*")
            Cargo.requestLayout()
            showSnackFeedback("Preencha todos os campos obrigatórios", false)
        } else if (verificaCampovazio(Periodocargo.text.toString())) {
            Periodocargo.validate("\\d+", "Campo obrigatório*")
            Periodocargo.requestLayout()
            showSnackFeedback("Preencha todos os campos obrigatórios", false)
        } else if (verificaCampovazio(Sexo.text.toString())) {
            Sexo.requestLayout()
            showSnackFeedback("Preencha todos os campos obrigatórios", false)
        }



        try {
           /* val userz = mAuth.currentUser!!

            var users = userDao.getUserEmail(userz.email!!)

            val cont = if (userDao.getNumberOfRowsEmail(users.email.toString())==0) {
                userDao.getNumberOfRows()+1
            }else {
                userDao.getReturnID(users.email.toString())
            }
*/
           val cont = if (userDao.getNumberOfRowsEmail(Email.text.toString())==0) {
                userDao.getNumberOfRows()+1
            }else{
                userDao.getReturnID(Email.text.toString())
            }

            val cont2 = cont.toString()

            val user = User(cont2.toLong(),
                    Nome.text.toString(),
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
                    Periodocargo.text.toString())

            try {
                userDao.add(user)
            }catch (e: Exception){
                userDao.update(user = user)
            }

       //     Toast.makeText( context , "Erro: "+user.toString(), Toast.LENGTH_LONG).show()
       //     Log.d("Inserirdo", user.toString());

        }catch (e: Exception){
            Toast.makeText(context, "Erro: "+e.message, Toast.LENGTH_LONG).show()
            Log.d("Erro ao buscar", e.toString());
        }

    }

    private fun validationform(view: View){

        try {
            val nome = view.findViewById<EditText>(R.id.Nome)
            val email = view.findViewById<EditText>(R.id.Email)
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
            val sexo = view.findViewById<MaterialSpinner>(R.id.Sexo)


               if (verificaCampovazio(nome.toString())) {
                   nome.requestFocus();
                   showSnackFeedback("Campo obrigatório", false)
               } else if (!emailValidar(email.toString())) {
                   email.requestFocus()
                   showSnackFeedback("Campo obrigatório", false)
               } else if (verificaCampovazio(dataNascimento.toString())) {
                   dataNascimento.requestFocus()
                   showSnackFeedback("Campo obrigatório", false)
               } else if (verificaCampovazio(nacionalidade.toString())) {
                   nacionalidade.requestFocus()
                   showSnackFeedback("Campo obrigatório", false)
               } else if (verificaCampovazio(cpf.toString())) {
                   cpf.requestFocus()
                   showSnackFeedback("Campo obrigatório" , false)
               } else if (verificaCampovazio(telefonefixo.toString())) {
                   telefonefixo.requestFocus()
                   showSnackFeedback("Campo obrigatório", false)
               } else if (verificaCampovazio(telefonecelular.toString())) {
                   telefonecelular.requestFocus()
                   showSnackFeedback("Campo obrigatório" , false)
               } else if (verificaCampovazio(area_Atuacao.toString())) {
                   area_Atuacao.requestFocus()
                   showSnackFeedback("Campo obrigatório", false)
               } else if (verificaCampovazio(periodo.toString())) {
                   periodo.requestFocus()
                   showSnackFeedback("Campo obrigatório" , false)
               } else if (verificaCampovazio(instituicao.toString())) {
                   instituicao.requestFocus()
                   showSnackFeedback("Campo obrigatório" , false)
               } else if (verificaCampovazio(empresas.toString())) {
                   empresas.requestFocus()
                   showSnackFeedback("Campo obrigatório", false)
               } else if (verificaCampovazio(cargo.toString())) {
                   cargo.requestFocus()
                   showSnackFeedback("Campo obrigatório" , false)
               } else if (verificaCampovazio(periodocargo.toString())) {
                   periodocargo.requestFocus()
                   showSnackFeedback("Campo obrigatório", false)
               } else if (verificaCampovazio(sexo.toString())) {
                   sexo.requestFocus()
                   showSnackFeedback("Campo obrigatório" , false)
               }

        }catch (e: Exception){
            showSnackFeedback("Não foi possivel validar os campos "+e.toString() , false)
        }
    }

     private fun verificaCampovazio(valor: String): Boolean {

           var resultado = (TextUtils.isEmpty( valor ) || valor.trim().isEmpty())

           return resultado
     }

     private fun emailValidar(email: String): Boolean {

           var resultado = (!verificaCampovazio(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())

           return resultado
     }

}