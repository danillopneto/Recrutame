package ufg.go.br.recrutame.fragment

import android.content.Context
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.HttpsCallableResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ufg.go.br.recrutame.JobCard
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.adapter.MatchAdapter
import ufg.go.br.recrutame.model.JobModel
import ufg.go.br.recrutame.model.Match
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import ufg.go.br.recrutame.model.MatchItemList

class ChatFragment : BaseFragment() {
    private lateinit var mChatAdapter: MatchAdapter
    private lateinit var mChatRv: RecyclerView
    private lateinit var mFunctions: FirebaseFunctions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inicializeApis()
        mFunctions = FirebaseFunctions.getInstance()

        val data = HashMap<String, kotlin.Any>()
        data.put("userId", mAuth.currentUser?.uid.orEmpty())

        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        mFunctions
                .getHttpsCallable("getMatches")
                .call(data)
                .continueWith(object: Continuation<HttpsCallableResult, String> {
                    @Throws(Exception::class)
                    override fun then(@NonNull task: Task<HttpsCallableResult>):String {
                        val result = task.getResult()
                        var data = result?.getData() as String

                        return data
                    }
                })
                .addOnCompleteListener(object: OnCompleteListener<String> {
                    override fun onComplete(@NonNull task: Task<String>) {
                        fillMatches(view, task)
                    }
                });

        return view
    }

    fun fillMatches(view: View, task: Task<String>){

        mChatRv = view.findViewById(R.id.mMessagesRv)

        if (!task.isSuccessful()) {
            var e = task.getException();
            if (e is FirebaseFunctionsException) {
            }
        } else{
            if(task.result != null){
                var matches = Gson().fromJson<List<MatchItemList>>(task.result, object : TypeToken<List<MatchItemList>>() {

                }.type)

                if (matches.isEmpty()
                        || activity == null) {
                    return
                }

                mChatAdapter = MatchAdapter(matches)

                val mLayoutManager = LinearLayoutManager(context)
                mChatRv.layoutManager = mLayoutManager
                mChatRv.itemAnimator = DefaultItemAnimator()
                mChatRv.adapter = mChatAdapter

                mChatAdapter.notifyDataSetChanged()
            }
        }
    }
}
