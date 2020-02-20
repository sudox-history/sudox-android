package com.sudox.design.resizableImageButton

import com.sudox.design.DesignTestContainer
import com.sudox.design.DesignTestRunner
import com.sudox.design.R
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(DesignTestRunner::class)
class ResizableImageButtonTest : Assert() {

    private var container: DesignTestContainer<ResizableImageButton>? = null
    private var view: ResizableImageButton? = null

    @Before
    fun setUp() {
        container = DesignTestContainer {
            ResizableImageButton(it).apply { id = Int.MAX_VALUE - 1  }
        }

        view = container!!.fill()
    }

    @Test
    fun testStateSaving() {
        view!!.setIconDrawable(R.drawable.abc_vector_test)
        view = container!!.fill()

        assertEquals(R.drawable.abc_vector_test, view!!.iconDrawableRes)
    }
}