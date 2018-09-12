package ufg.go.br.recrutame.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.model.chat.ChatMessage
import android.view.LayoutInflater
import android.widget.ImageView
import ufg.go.br.recrutame.Util.ImageUtils

class MessageListAdapter(
        public var context:Context,
        public var mMessageList:List<ChatMessage>,
        public var userId: String) : RecyclerView.Adapter<MessageListAdapter.MyViewHolder>() {

    private var VIEW_TYPE_MESSAGE_SENT: Int = 1
    private var VIEW_TYPE_MESSAGE_RECEIVED: Int = 2

    open class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        init{
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = mMessageList[position]

        return if (message.sender.sendByUser) {
            VIEW_TYPE_MESSAGE_SENT
        } else {
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListAdapter.MyViewHolder {
        val view: View

        if (viewType === VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_sent, parent, false)
            return SentMessageHolder(view)
        }

        view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_received, parent, false)
        return ReceivedMessageHolder(view)
    }

    override fun getItemCount(): Int {
        return mMessageList.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message = mMessageList[position]

        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
        }
    }

    private inner class SentMessageHolder internal constructor(itemView: View) : MyViewHolder(itemView) {
        internal var messageText: TextView
        internal var timeText: TextView

        init {
            messageText = itemView.findViewById<View>(R.id.text_message_body) as TextView
            timeText = itemView.findViewById<View>(R.id.text_message_time) as TextView
        }

        internal fun bind(message: ChatMessage) {
            messageText.setText(message.message)

            timeText.setText(message.createdAt)
        }
    }

    private inner class ReceivedMessageHolder internal constructor(itemView: View) : MyViewHolder(itemView) {
        internal var messageText: TextView
        internal var timeText: TextView

        init {
            messageText = itemView.findViewById<View>(R.id.text_message_body) as TextView
            timeText = itemView.findViewById<View>(R.id.text_message_time) as TextView
        }

        internal fun bind(message: ChatMessage) {
            messageText.setText(message.message)
            timeText.setText(message.createdAt)
        }
    }
}
