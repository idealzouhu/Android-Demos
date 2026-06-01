package com.zouhu.affirmations.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * 创建一个Affirmation类，包含一个字符串资源ID和一张图片资源ID
 */
data class Affirmation(
    @StringRes val stringResourceId: Int,
    @DrawableRes val imageResourceId: Int
)
