package ufg.go.br.recrutame.activity

import android.app.ActionBar
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.*
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.Util.ImageUtils
import ufg.go.br.recrutame.adapter.MessageListAdapter
import ufg.go.br.recrutame.model.*
import ufg.go.br.recrutame.model.chat.ChatMessage
import ufg.go.br.recrutame.model.chat.ChatUser
import ufg.go.br.recrutame.util.Utils
import java.util.*

class MessageListActivity : BaseActivity() {
    private lateinit var mMessageRecycler: RecyclerView
    private lateinit var mMessageAdapter: MessageListAdapter
    private lateinit var userId: String
    private var jobId: Long = 0
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mMessageList: List<ChatMessage>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)

        var intent = getIntent()
        userId = intent.getStringExtra("userId")
        jobId = intent.getLongExtra("jobId",0)

        InitializeFields()
    }

    private fun InitializeFields(){
        mDatabase = FirebaseDatabase.getInstance().reference
        mMessageList = ArrayList<ChatMessage>()
        setOnClickSendMessage()
        setOnChangeMessage()
    }

    private fun setOnChangeMessage(){
        var ref = mDatabase.child("matches/$userId/$jobId")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(postSnapshot: DatabaseError?) {
            }

            override fun onDataChange(postSnapshot: DataSnapshot?) {
                val matchItemResult:MatchItemResult? = postSnapshot?.getValue(MatchItemResult::class.java)
                inflateMatchAdapter(matchItemResult)
                setCustomActionBar(matchItemResult)
            }
        })
    }

    private fun setOnClickSendMessage(){
        var button = findViewById(R.id.button_chatbox_send) as ImageView
        var mMessageTextView = findViewById(R.id.edittext_chatbox) as EditText


        button.setOnClickListener{
            var text = mMessageTextView.text.toString()
            if(!Utils.isNullOrWhiteSpace(text)){
                var newMessage = mDatabase.ref.child("/matches/$userId/$jobId/messages").push()

                newMessage.setValue(getMessage(mMessageTextView.text.toString()))

                mMessageTextView.setText("")
            }
        }
    }

    private fun getMessage(text: String) : Message{
        var currentDate = Utils.getCurrentDate()
        var messageText = text
        var sendByUser = true;

        return Message(currentDate, messageText, sendByUser)
    }

    private fun setCustomActionBar(match: MatchItemResult?){
        var view = LayoutInflater.from(this).inflate(R.layout.chat_custom_bar, null)
        var companyNameTxt = view.findViewById(R.id.custom_profile_name) as TextView
        var companyImg = view.findViewById(R.id.custom_profile_image) as ImageView

        companyNameTxt.text = match?.companyName
        ImageUtils.displayRoundImageFromUrl(match?.companyImg!!, companyImg)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.customView = view

    }

    private fun inflateMatchAdapter(match: MatchItemResult?){
        mMessageList = getMessages(match)

        mMessageAdapter = MessageListAdapter(this, mMessageList, userId)

        mMessageRecycler = findViewById(R.id.reyclerview_message_list)
        mMessageRecycler.layoutManager = LinearLayoutManager(this)
        mMessageRecycler.adapter = mMessageAdapter
        mMessageRecycler.scrollToPosition(mMessageList.size - 1)

        mMessageAdapter.notifyDataSetChanged()

        mMessageRecycler.addOnLayoutChangeListener(object: View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                mMessageRecycler.scrollToPosition(mMessageList.size-1);
            }

        })
    }

    private fun getMessages(match: MatchItemResult?):List<ChatMessage>{
        var chatMessageList = ArrayList<ChatMessage>()

        var messageList = match?.messages!!.values.toList()
        var newMessageList = messageList.sortedWith(compareBy(Message::date))

        for(message in newMessageList){

            var chatUser = if(message.sendByUser){
                ChatUser(message.sendByUser, "", "")
            } else{
                ChatUser(message.sendByUser, match.companyName, match.companyImg)
            }

            var chatMessage = ChatMessage(message.message, chatUser, message.date)
            chatMessageList.add(chatMessage)

        }

        return chatMessageList
    }
}