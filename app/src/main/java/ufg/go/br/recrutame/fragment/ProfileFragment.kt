package ufg.go.br.recrutame.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.arch.persistence.room.Room
import android.content.Intent
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jaredrummler.materialspinner.MaterialSpinner
import com.rengwuxian.materialedittext.MaterialEditText
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import rec.protelas.User
import ufg.go.br.recrutame.EditGeneralInfoActivity
import ufg.go.br.recrutame.JobCard
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.TAG
import ufg.go.br.recrutame.Util.Mask
import ufg.go.br.recrutame.Util.Utils
import ufg.go.br.recrutame.adapter.ItemIdiomaAdapter
import ufg.go.br.recrutame.adapter.ItemProfileAdapter
import ufg.go.br.recrutame.dao.*
import ufg.go.br.recrutame.model.*
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : BaseFragment(), View.OnClickListener  {
    private lateinit var database: FirebaseDatabase
    private lateinit var mProfileImage: CircleImageView
    private lateinit var userDao: UserDao
    private lateinit var skillDao: SkillDao
    private lateinit var idiomDao: IdiomDao
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapterAtividade: ItemProfileAdapter
    private lateinit var newAtividadesTxt: TextView
    private lateinit var mAdapterIdioma: ItemIdiomaAdapter
    private lateinit var newIdiomaTxt: TextView
    private var userModel: UserProfile? = null

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
        database = FirebaseDatabase.getInstance()
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        database.getReference("users/" + mAuth.currentUser?.uid)
                .addValueEventListener( object: ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        userModel = dataSnapshot?.getValue(UserProfile::class.java)
                        if (userModel != null) {
                            fillGeneralInfo(view, userModel!!.generalInfo)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError?) {
                        Log.d(TAG, "" + databaseError)
                    }
                })

        spinnerSexo(view)


        /*val database =  Room.databaseBuilder(this.getActivity()!!, AppDb::class.java, "userDb")
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
        */

        inicializeControls(view)

        return view
    }

    private fun fillGeneralInfo(view: View, generalInfo: UserGeneralInfo) {
        view.findViewById<TextView>(R.id.mUsernameTxt).text = "${generalInfo.name} ${generalInfo.lastName}"

        val generalInfoTxt = view.findViewById<TextView>(R.id.mGeneralInfoTxt)
        val generalInfoData = StringBuilder()
        generalInfoData.appendln(generalInfo.getBirthDateFormated())
        generalInfoData.appendln("${generalInfo.city} - ${generalInfo.state}")

        generalInfoTxt.text = generalInfoData.toString()
    }

    private fun inicializeControls(view: View) {
        mProfileImage = view.findViewById(R.id.mProfileImage)
        newAtividadesTxt = view.findViewById(R.id.Atividades_Desenvolvidas)
        newIdiomaTxt = view.findViewById(R.id.Idioma)

        view.findViewById<FloatingActionButton>(R.id.mChangePictureBtn).setOnClickListener(this)
        view.findViewById<ImageButton>(R.id.mEditGeneralInfoBtn).setOnClickListener(this)

        mStorageRef.child(getUserPhotoUrl()).downloadUrl.addOnSuccessListener { task ->
            EventBus.getDefault().post(task)
        }.addOnFailureListener{
            EventBus.getDefault().post(R.drawable.user_logo)
        }
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
            R.id.mChangePictureBtn -> chooseImage()
            R.id.mEditGeneralInfoBtn -> editGeneralInfo()
        }
    }

    private fun editGeneralInfo() {
        val i = Intent(context, EditGeneralInfoActivity :: class.java)
        i.putExtra("userId", mAuth.currentUser?.uid)
        i.putExtra("userName", userModel?.generalInfo?.name)
        i.putExtra("userLastName", userModel?.generalInfo?.lastName)
        startActivity(i)
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

     private fun verificaCampovazio(valor: String): Boolean {

           var resultado = (TextUtils.isEmpty( valor ) || valor.trim().isEmpty())

           return resultado
     }

     private fun emailValidar(email: String): Boolean {

           var resultado = (!verificaCampovazio(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())

           return resultado
     }
}