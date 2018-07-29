package ufg.go.br.recrutame.fragment

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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
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
import ufg.go.br.recrutame.dao.AppDb
import ufg.go.br.recrutame.dao.AppDbSkill
import ufg.go.br.recrutame.dao.SkillDao
import ufg.go.br.recrutame.dao.UserDao
import java.util.*


private lateinit var userDao: UserDao
private lateinit var skillDao: SkillDao
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

        spinnerProfissional(view)
        spinnerNivelIdioma(view)
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



        inicializeControls(view)

        return view
    }

    private fun inicializeControls(view: View) {
        mProfileImage = view.findViewById(R.id.mProfileImage)
        newAtividadesTxt = view.findViewById(R.id.Atividades_Desenvolvidas)
        newIdiomaTxt = view.findViewById(R.id.Idioma)

        atividadesRecycler(view)
        idiomaRecycler(view)

        val user = mAuth.currentUser!!
        mStorageRef.child(getUserPhotoUrl()).downloadUrl.addOnSuccessListener { task ->
            EventBus.getDefault().post(task)
        }.addOnFailureListener{
            EventBus.getDefault().post(R.drawable.user_logo)
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
                dataNascimento.setInputType(0);
                dateNascimento(view)
                dataNascimento.setInputType(0);
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
             nascimento.setInputType(0);
            val dpd = DatePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_MinWidth , DatePickerDialog.OnDateSetListener{view, mYear, mMonth, mDay ->
                nascimento.setText(""+mDay+"/0"+mMonth+"/"+mYear)
                nascimento.setInputType(0);
                nascimento.addTextChangedListener(Mask.date("##/##/####", nascimento))
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

        val list: MutableList<String> = mutableListOf()
        // a lista de habilidades vem daqui
       // val currentFilters = getMyPreferences().getFilters()

        val numbers: MutableList<String> = mutableListOf("Desevolvedor", "Projetista", "Desing")
        val currentFilters: List<String> = numbers
       // val user = mAuth.currentUser!!

        /*var skillss = skillDao.getSkillEmail(user.email!!)
        if (skillss == null) {
            skillss = Skill()
        }
        */
        Log.d("Log do Habilidades", ""+currentFilters)


        if (currentFilters != null && currentFilters.isNotEmpty()) {
            list.addAll(currentFilters)
        }

        mAdapterAtividade = ItemProfileAdapter(list)
        mRecyclerView.adapter = mAdapterAtividade
        mAdapterAtividade.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                //getMyPreferences().setFilters(mAdapter.getItens())
                //aqui grava no banco as habilidades
                Log.d("Log do getItens", ""+mAdapterAtividade.getItens())
            }
        })

        mRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }


    private fun handleAddIdioma(){
        if (Utils.isNullOrWhiteSpace(newIdiomaTxt.text.toString())) {
            Toast.makeText(context, getString(R.string.insert_filter_term), Toast.LENGTH_SHORT).show()
        } else {
            mAdapterIdioma.updateList(newIdiomaTxt.text.toString().trim())
            newIdiomaTxt.setText("")
        }
    }

    private fun idiomaRecycler(view: View) {
        mRecyclerView = view.findViewById(R.id.recyclerIdiomaItems)

        val layoutManager = LinearLayoutManager(context)
        mRecyclerView.layoutManager = layoutManager

        val list: MutableList<String> = mutableListOf()
        // a lista de habilidades vem daqui
        // val currentFilters = getMyPreferences().getFilters()

        val numbers: MutableList<String> = mutableListOf("Ingles", "Espanhol", "Italiano")
        val currentFilters: List<String> = numbers
        // val user = mAuth.currentUser!!

        /*var skillss = skillDao.getSkillEmail(user.email!!)
        if (skillss == null) {
            skillss = Skill()
        }
        */
        Log.d("Log do Habilidades", ""+currentFilters)


        if (currentFilters != null && currentFilters.isNotEmpty()) {
            list.addAll(currentFilters)
        }

        mAdapterIdioma = ItemIdiomaAdapter(list)
        mRecyclerView.adapter = mAdapterIdioma
        mAdapterIdioma.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                //getMyPreferences().setFilters(mAdapter.getItens())
                //aqui grava no banco as habilidades
                Log.d("Log do getItens", ""+mAdapterIdioma.getItens())
            }
        })

        mRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
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

}