package com.woodys.demo.eventcollect.mouble

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by woodys on 2017/9/19.
 */
class ActionItem(val token:Int, val type: Type, val clazzName:String?, val value: String?, var arg:Any?=null) {
    companion object {
        val formatter= SimpleDateFormat("yy-MM-dd HH:mm:ss")
    }
    val ct: Long = System.currentTimeMillis()

    override fun toString(): String ="${ActionItem.Companion.formatter.format(Date(ct))} $type $clazzName $value ${if(null!=arg) arg else ""}"
}
