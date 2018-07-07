package ufg.go.br.recrutame

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import rec.protelas.DataBaseHandle
import rec.protelas.User
import kotlinx.android.synthetic.main.fragment_profile.*
import android.content.Context

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false);

     //   val helper = DataBaseHandle(getContext()!!)

        val context = container!!.getContext()//getContext()!!
        var db = DataBaseHandle(context!!)


        btnSave.setOnClickListener {

            var user = User(1,Nome.text.toString(),
                    DataNascimento.text.toString().toInt(),
                    Sexo.text.toString(),
                    Nacionalidade.text.toString())

            db.insertData(user)

            Toast.makeText(context, "You clicked me.", Toast.LENGTH_SHORT).show()
        }


        btnRead.setOnClickListener {
            var data = db.readData()

            Result.text = ""
            for (i in 0..(data.size -1)){
                Result.append(data.get(i).id.toString() + "\n" +
                        ""+ data.get(i).nome + "\n"+
                        ""+ data.get(i).dataNascimento + "\n"+
                        ""+ data.get(i).sexo + "\n"+
                        ""+ data.get(i).nacionalidade + "\n"
                )
            }

            var user = User(1,data.get(0).nome,data.get(0).dataNascimento, data.get(0).sexo, data.get(0).nacionalidade )



            Nome.setText(data.get(0).nome)
            DataNascimento.setText(data.get(0).dataNascimento.toString())
            Sexo.setText(data.get(0).sexo)
            Nacionalidade.setText(data.get(0).nacionalidade)



        }

      //  val btn_update = findViewById(R.id.btnUpdate) as Button

        Result.setOnClickListener {
            var data = db.readData()

            Result.text = ""
            for (i in 0..(data.size -1)){
                Result.append(data.get(i).id.toString() + "\n" +
                        ""+ data.get(i).nome + "\n"+
                        ""+ data.get(i).dataNascimento + "\n"+
                        ""+ data.get(i).sexo + "\n"+
                        ""+ data.get(i).nacionalidade + "\n"
                )
            }

            var user = User(1,Nome.text.toString(),
                    DataNascimento.text.toString().toInt(),
                    Sexo.text.toString(),
                    Nacionalidade.text.toString())

            Toast.makeText(context, "Falha ao inserir"+user, Toast.LENGTH_LONG).show()


            db.updateUser(user)
            // btn_read.performClick()

        }


        btnDeleta.setOnClickListener {

            var data = db.readData()

            db.deletaData(data.get(0))
            btnRead.performClick()

        }



    }
}
