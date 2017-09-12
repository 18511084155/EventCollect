
package com.woodys.eventcollect.callback;

/**
 * 返回对象事件
 *
 * @param <E>
 * @author woodys
 * @Date 2016/3/4
 */
public interface Action<K,E> {
    K call(E item);
}