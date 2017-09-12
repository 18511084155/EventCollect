package com.woodys.eventcollect.callback;

/**
 * Created by woodys on 2017/9/8.
 *
 * 发送数据的回调接口
 */

public interface SendActionCallback<T,K,E> {
    void sendAction(T item,Action<K,E> action);
}
