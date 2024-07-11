package com.guyuuan.app.find_author.core.ui.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection

/**
 * @author: chunjinchen
 * @createTime: 7/11/24 16:55
 * @description:
 **/
@Composable
operator fun PaddingValues.plus(other: PaddingValues) = PaddingValues(
    start = this.calculateStartPadding(LocalLayoutDirection.current) + other.calculateStartPadding(LocalLayoutDirection.current),
    top = this.calculateTopPadding() + other.calculateTopPadding(),
    end = this.calculateEndPadding(LocalLayoutDirection.current) + other.calculateEndPadding(LocalLayoutDirection.current),
    bottom = this.calculateBottomPadding() + other.calculateBottomPadding()
)