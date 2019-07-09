package com.sudox.design.shadows

import android.widget.TextView
import org.robolectric.annotation.Implements
import org.robolectric.shadows.ShadowTextView

@Implements(TextView::class)
class LayoutDirectionTextViewShadow : LayoutDirectionViewShadow()