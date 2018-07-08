package ufg.go.br.recrutame.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ufg.go.br.recrutame.R
import android.widget.Toast
import rec.protelas.DataBaseHandle
import rec.protelas.User
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.android.synthetic.main.fragment_profile.*
import android.content.Context
import android.widget.Button
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import ufg.go.br.recrutame.API_BASE_URL
import ufg.go.br.recrutame.BuildConfig
import ufg.go.br.recrutame.OAUTH_ACCESSTOKEN
import ufg.go.br.recrutame.api.model.LIProfileInfo
import ufg.go.br.recrutame.api.service.LIService
import ufg.go.br.recrutame.api.service.ServiceGenerator


class ProfileFragment : Fragment(), View.OnClickListener {
    lateinit var db: DataBaseHandle
    lateinit var liProfileInfo: LIProfileInfo

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        inicializeControls(view)
     //   val helper = DataBaseHandle(getContext()!!)

      //  val btn_update = findViewById(R.id.btnUpdate) as Button

        fillLinkedinInfo()
        return view
    }

    private fun inicializeControls(view: View?) {
        db = DataBaseHandle(context!!.applicationContext)

        val result = view!!.findViewById<TextView>(R.id.Result)
        result.setOnClickListener(this)

        val btnSave = view.findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener(this)

        val btnRead = view.findViewById<Button>(R.id.btnRead)
        btnRead.setOnClickListener(this)

        val btnDeleta = view.findViewById<Button>(R.id.btnDeleta)
        btnDeleta.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnDeleta -> handleDelete()
            R.id.btnSave -> handleSave()
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
                    ""+ data[i].nacionalidade + "\n"
            )
        }

        val user = User(1,Nome.text.toString(),
                DataNascimento.text.toString().toInt(),
                Sexo.text.toString(),
                Nacionalidade.text.toString())

        Toast.makeText(context, "Falha ao inserir"+user, Toast.LENGTH_LONG).show()


        db.updateUser(user)
        // btn_read.performClick()
    }

    private fun handleSave() {
        val user = User(1,Nome.text.toString(),
                DataNascimento.text.toString().toInt(),
                Sexo.text.toString(),
                Nacionalidade.text.toString())

        db.insertData(user)

        Toast.makeText(context, "You clicked me.", Toast.LENGTH_SHORT).show()
    }

    private fun handleUpdate() {
        val data = db.readData()

        Result.text = ""
        for (i in 0..(data.size -1)){
            Result.append(data[i].id.toString() + "\n" +
                    ""+ data[i].nome + "\n"+
                    ""+ data[i].dataNascimento + "\n"+
                    ""+ data[i].sexo + "\n"+
                    ""+ data[i].nacionalidade + "\n"
            )
        }

        val user = User(1, data[0].nome, data[0].dataNascimento, data[0].sexo, data[0].nacionalidade )



        Nome.setText(data[0].nome)
        DataNascimento.setText(data[0].dataNascimento.toString())
        Sexo.setText(data[0].sexo)
        Nacionalidade.setText(data[0].nacionalidade)
    }

    private fun fillLinkedinInfo() {
        val prefs = context!!.getSharedPreferences(
                BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        if (prefs != null
                && prefs.contains(OAUTH_ACCESSTOKEN)) {
            val token = prefs.getString(OAUTH_ACCESSTOKEN, "")
            if (token != "") {
                val client = ServiceGenerator(API_BASE_URL).createService(LIService::class.java)
                val map = HashMap<String, String>()
                map["Authorization"] = "Bearer $token"
                val call = client.getUserInfo(map)
                call.enqueue(object : Callback<LIProfileInfo> {
                    override fun onResponse(call: Call<LIProfileInfo>, response: Response<LIProfileInfo>) {
                        val statusCode = response.code()
                        if (statusCode == 200) {
                            liProfileInfo = response.body()!!
                            Picasso.get().load(liProfileInfo.pictureUrl).into(profile_image)
                            Nome.setText("${liProfileInfo.firstName} ${liProfileInfo.lastName}")
                            Email.setText("${liProfileInfo.emailAddress}")
                        } else {
                            Toast.makeText(context!!, "Failed in", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LIProfileInfo>, t: Throwable) {
                        Toast.makeText(context!!, "Failed in", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}
