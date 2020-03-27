package com.sudox.messenger.android.messages

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.tabs.TabsChildFragment
import com.sudox.messenger.android.messages.vos.BaseMessagesDialogVO
import kotlinx.android.synthetic.main.fragment_rooms.dialogsRoomRecyclerView
import java.util.Calendar
import kotlin.random.Random

class TalksTabFragment : CoreFragment(), TabsChildFragment, ApplicationBarListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_rooms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialogsRoomRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = DialogsAdapter(context!!).apply {
                dialogs.apply {
                    add(generateDialog())
                    add(generateDialog())
                    add(generateDialog())
                }

                addDialogCallback = {
                    dialogs.add(generateDialog())
                }
            }
        }

        dialogsRoomRecyclerView.setHasFixedSize(true)
    }

    fun generateDialog(): BaseMessagesDialogVO {
        val photos = listOf(AppCompatResources.getDrawable(context!!, R.drawable.drawable_photo_1)!!, AppCompatResources.getDrawable(context!!, R.drawable.drawable_photo_2)!!, AppCompatResources.getDrawable(context!!, R.drawable.drawable_photo_3)!!)
        val names = listOf("Ярослав", "Макс", "Никита", "Андрей", "Ярик", "Антон", "Дима")
        val messages = listOf(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut tellus elementum sagittis vitae et leo duis ut. Tortor at risus viverra adipiscing at in. In nulla posuere sollicitudin aliquam ultrices sagittis orci a scelerisque. Elit duis tristique sollicitudin nibh sit amet commodo. Ornare arcu dui vivamus arcu felis bibendum ut tristique et. Scelerisque varius morbi enim nunc faucibus a pellentesque. Proin nibh nisl condimentum id venenatis. Etiam erat velit scelerisque in. Arcu vitae elementum curabitur vitae nunc sed velit dignissim sodales. Sit amet dictum sit amet justo donec enim. Pulvinar sapien et ligula ullamcorper malesuada proin libero nunc consequat. Faucibus scelerisque eleifend donec pretium vulputate sapien nec sagittis. Adipiscing elit duis tristique sollicitudin nibh sit amet commodo. Nec tincidunt praesent semper feugiat nibh sed.",
                "Aenean vel elit scelerisque mauris. Nec tincidunt praesent semper feugiat nibh sed pulvinar. Sagittis nisl rhoncus mattis rhoncus urna neque viverra. Nunc mi ipsum faucibus vitae. A lacus vestibulum sed arcu. Volutpat commodo sed egestas egestas. Eget est lorem ipsum dolor sit. At auctor urna nunc id cursus metus. Consectetur libero id faucibus nisl tincidunt. Vestibulum rhoncus est pellentesque elit ullamcorper dignissim cras tincidunt lobortis. A lacus vestibulum sed arcu non odio euismod lacinia. Molestie nunc non blandit massa enim nec. Pharetra magna ac placerat vestibulum lectus mauris ultrices. Ut pharetra sit amet aliquam id diam maecenas. Vel pretium lectus quam id leo. Vitae purus faucibus ornare suspendisse sed nisi lacus. Ultricies tristique nulla aliquet enim tortor at auctor urna nunc.",
                "Eu mi bibendum neque egestas congue quisque egestas diam. Pharetra et ultrices neque ornare aenean euismod elementum nisi. Nisi scelerisque eu ultrices vitae auctor eu augue ut. Nec ullamcorper sit amet risus nullam eget felis eget. Nunc non blandit massa enim. Velit aliquet sagittis id consectetur. Arcu cursus vitae congue mauris rhoncus aenean vel elit scelerisque. Orci phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor. Sem viverra aliquet eget sit amet tellus cras adipiscing. A diam sollicitudin tempor id eu nisl nunc. Sit amet aliquam id diam maecenas ultricies. Blandit cursus risus at ultrices mi tempus imperdiet.",
                "ЧЕЛ Я РЕАЛЬНО ГОВОРЮ")
        val dates = listOf("пн.", "30 дек.", "5ч")

        val messageViewed = Random.nextInt(2) == 1
        val tmp = Random.nextInt(2) == 1
        var lastMessage = false
        var lastViewed = false
        if (messageViewed && tmp) {
            lastMessage = true
            if (Random.nextInt(2) == 1) {
                lastViewed = true
            }
        }
        return BaseMessagesDialogVO(
                dialogId = Random.nextInt(),
                isUserOnline = false,
                isMuted = Random.nextInt(2) == 1,
                isViewed = messageViewed,
                dialogPhoto = photos[Random.nextInt(photos.size)],
                dialogName = names[Random.nextInt(names.size)],
                previewMessage = messages[Random.nextInt(messages.size)],
                dateView = dates[Random.nextInt(dates.size)],
                date = Calendar.getInstance().timeInMillis + Random.nextLong(1000000),
                messagesCount = if (!messageViewed) Random.nextInt(10, 50) else 0,
                isLastMessageByMe = lastMessage,
                isSentMessageDelivered = lastMessage,
                isSentMessageViewed = lastViewed
        )
    }

    override fun getTitle(context: Context): String {
        return context.getString(R.string.rooms)
    }

    override fun onButtonClicked(tag: Int) {
    }
}