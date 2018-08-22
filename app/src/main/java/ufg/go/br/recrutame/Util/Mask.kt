package ufg.go.br.recrutame.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * Created by Vinicius on 12/07/2018.
 */
class Mask{
    companion object {
        private fun replaceChars(cpfFull : String) : String{
            return cpfFull.replace(".", "").replace("-", "")
                    .replace("(", "").replace(")", "")
                    .replace("/", "").replace(" ", "")
                    .replace("*", "")
        }


        fun mask(mask : String, etCpf : EditText) : TextWatcher {

            val textWatcher : TextWatcher = object : TextWatcher {
                var isUpdating : Boolean = false
                var oldString : String = ""
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val str = replaceChars(s.toString())
                    var cpfWithMask = ""

                    if (count == 0)
                        isUpdating = true

                    if (isUpdating){
                        oldString = str
                        isUpdating = false
                        return
                    }

                    var i = 0
                    for (m : Char in mask.toCharArray()){
                        if (m != '#' && str.length > oldString.length){
                            cpfWithMask += m
                            continue
                        }
                        try {
                            cpfWithMask += str.get(i)
                        }catch (e : Exception){
                            break
                        }
                        i++
                    }

                    isUpdating = true
                    etCpf.setText(cpfWithMask)
                    etCpf.setSelection(cpfWithMask.length)

                }

                override fun afterTextChanged(editable: Editable) {

                }
            }

            return textWatcher
        }

        fun date(mask: String , et: EditText): TextWatcher {
            return object : TextWatcher {
                internal var isUpdating: Boolean = false
                internal var oldTxt = ""
                override fun onTextChanged(
                        s: CharSequence , start: Int , before: Int , count: Int) {
                    val str = Mask.unmask(s.toString())
                    var maskCurrent = ""
                    if (isUpdating) {
                        oldTxt = str
                        isUpdating = false
                        return
                    }
                    var i = 0
                    for (m in mask.toCharArray()) {
                        if (m != '#' && str.length > oldTxt.length) {
                            maskCurrent += m
                            continue
                        }
                        try {
                            maskCurrent += str[i]
                        } catch (e: Exception) {
                            break
                        }

                        i++
                    }
                    isUpdating = true
                    et.setText(maskCurrent)
                    et.setSelection(maskCurrent.length)
                }

                override fun beforeTextChanged(
                        s: CharSequence , start: Int , count: Int , after: Int) {
                }

                override fun afterTextChanged(s: Editable) {}
            }
        }

        private fun unmask(s: String): String {
            return s.replace("[.]".toRegex() , "").replace("[-]".toRegex() , "")
                    .replace("[/]".toRegex() , "").replace("[(]".toRegex() , "")
                    .replace("[)]".toRegex() , "")
        }


        fun textMask(textoAFormatar: String , mask: String): String {
            var formatado = ""
            var i = 0
            // vamos iterar a mascara, para descobrir quais caracteres vamos adicionar e quando...
            for (m in mask.toCharArray()) {
                if (m != '#') { // se não for um #, vamos colocar o caracter informado na máscara
                    formatado += m
                    continue
                }
                // Senão colocamos o valor que será formatado
                try {
                    formatado += textoAFormatar[i]
                } catch (e: Exception) {
                    break
                }

                i++
            }
            return formatado
        }



    }




}