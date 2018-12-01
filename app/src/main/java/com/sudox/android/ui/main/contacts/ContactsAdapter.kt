package com.sudox.android.ui.main.contacts

//class ContactsAdapter @Inject constructor(val context: Context,
//                                          private val contactsRepository: ContactsRepository) : RecyclerView.Adapter<ContactsAdapter.Holder>() {

//    var items: List<User> = arrayListOf()
//
//    // Кэллбэки
//    lateinit var clickCallback: (User) -> (Unit)
//    lateinit var menuInflater: MenuInflater
//
//    override fun getItemViewType(position: Int): Int {
//        return if (position == items.size) R.layout.item_contacts_amount else R.layout.item_contact
//    }
//
//    override fun getItemCount(): Int {
//        return if (items.isNotEmpty()) items.size + 1 else 0
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
//        return Holder(LayoutInflater
//                .from(context)
//                .inflate(viewType, parent, false))
//    }
//
//    override fun onBindViewHolder(holder: Holder, position: Int) {
//        if (position != items.size) {
//            val contact = items[position]
//
//            // Set listeners
//            holder.itemView.apply {
//                setOnClickListener { clickCallback(contact) }
//                setOnLongClickListener { showContextMenu() }
//                setOnCreateContextMenuListener { menu, _, _ ->
//                    menuInflater.inflate(R.menu.menu_contact_options, menu)
//
//                    // Свой способ :)
//                    menu.setOnItemClickListener {
//                        when (it.itemId) {
//                            R.id.remove_contact -> contactsRepository.removeContact(contact.uid)
//                        }
//
//                        // Все норм
//                        return@setOnItemClickListener true
//                    }
//                }
//            }
//
//            holder.bindUser(contact)
//        } else {
//            holder.bindAmount(items.size)
//        }
//    }
//
//    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
//
//        fun bindAmount(amount: Int) {
//            if (amount > 0) {
//                view.contactsAmount.text = view.resources.getQuantityString(R.plurals.contacts_amount, amount, amount)
//            } else {
//                view.contactsAmount.visibility = View.GONE
//            }
//        }
//
//        fun bindUser(user: User) {
//            bindAvatar(user)
//
//            // Bind others data ...
//            view.dialogRecipientName.text = user.name
//            view.nickname.text = user.nickname
//        }
//
//        fun bindAvatar(user: User) {
//            val avatarInfo = AvatarInfo.parse(user.avatar)
//
//            if (avatarInfo is ColorAvatarInfo) {
//                drawCircleBitmap(view.context, drawAvatar(
//                        text = user.name.getTwoFirstLetters(),
//                        firstColor = avatarInfo.firstColor,
//                        secondColor = avatarInfo.secondColor), view.dialogRecipientAvatar!!)
//            }
//        }
//    }
//}