package com.woodys.eventcollect.mouble;

/**
 * Created by woodys on 2017/9/7.
 */

public enum EventType {
    APP_OPEN("app_open"), ACTIVITY_OPEN("activity_open"), CLICK("click"),LIST_CLICK("list_click"), ACTIVITY_CLOSE("activity_close"), APP_CLOSE("app_close");
    public String value;
    EventType(String value) {
        this.value=value;
    }
    @Override
    public String toString() {
        return value;
    }
}
