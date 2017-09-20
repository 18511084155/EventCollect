package com.woodys.eventcollect.callback;

/**
 * 发送数据的回调接口
 * @param <T>
 * @param <K>
 * @param <E>
 */

public interface SendActionCallback<T,K,E> {
    void sendAction(T item,Action<K,E> action);
}
