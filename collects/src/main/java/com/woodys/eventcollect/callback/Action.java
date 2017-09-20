
package com.woodys.eventcollect.callback;

/**
 * 返回对象事件
 * @param <K>
 * @param <E>
 */
public interface Action<K,E> {
    K call(E item);
}