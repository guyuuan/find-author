package com.guyuuan.app.find_author.core.data.model

/**
 * @author: chunjinchen
 * @createTime: 7/11/24 10:42
 * @description:
 **/
sealed class PagingUiMode<T> {
    abstract val data: T?

    data class Header<T>(val timeString: String, override val data: T?=null) : PagingUiMode<T>()

    data class Item<T>(override val data: T,val realIndex:Int) : PagingUiMode<T>()
}