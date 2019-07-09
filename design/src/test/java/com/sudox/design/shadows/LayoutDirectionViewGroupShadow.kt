package com.sudox.design.shadows

import android.view.ViewGroup
import org.robolectric.annotation.Implements

@Implements(ViewGroup::class)
class LayoutDirectionViewGroupShadow : LayoutDirectionViewShadow()