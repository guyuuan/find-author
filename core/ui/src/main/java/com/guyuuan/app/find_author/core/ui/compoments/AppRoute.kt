package com.guyuuan.app.find_author.core.ui.compoments

/**
 * @author: guyuuan
 * @createTime: 6/11/24 10:47
 * @description:
 **/

enum class AppRoute(val route: String,  val title: String?=null,val showAppBar:Boolean=false) {
    HOME("/" ),
    CHOOSE_BUCKETS("/choose_buckets" ),
    BUCKET("/bu")
}