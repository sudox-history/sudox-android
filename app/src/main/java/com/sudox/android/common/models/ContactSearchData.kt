package com.sudox.android.common.models

import com.sudox.android.common.enums.ContactSearchState
import com.sudox.android.database.model.Contact

data class ContactSearchData(val state: ContactSearchState, val contact: Contact? = null)