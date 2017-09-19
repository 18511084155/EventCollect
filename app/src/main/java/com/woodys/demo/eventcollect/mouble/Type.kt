package com.woodys.demo.eventcollect.mouble

/**
 * Created by woodys on 2017/9/19.
 */
enum class Type constructor(private val value: String) {
    APP_OPEN("app_open"), ACTIVITY_OPEN("activity_open"), CLICK("click"),LIST_CLICK("list_click"), ACTIVITY_CLOSE("activity_close"), APP_CLOSE("app_close");

    override fun toString(): String {
        return value
    }
}
