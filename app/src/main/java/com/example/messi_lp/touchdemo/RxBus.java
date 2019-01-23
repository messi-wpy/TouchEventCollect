package com.example.messi_lp.touchdemo;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by ybao (ybaovv@gmail.com)
 * Date: 17/2/26
 */

public class RxBus {

    private static volatile RxBus defaultInstance;

    private final Subject<Object> bus;

    public RxBus() {
        bus = PublishSubject.create().toSerialized();
    }

    public static RxBus getDefault() {
        if (defaultInstance == null) {
            synchronized (RxBus.class) {
                if (defaultInstance == null) {
                    defaultInstance = new RxBus();
                }
            }
        }
        return defaultInstance ;
    }
    public void send (Object o) {
        bus.onNext(o);
    }

    public <T> Observable<T> toObservable (Class<T> eventType) {
        return bus.ofType(eventType);
    }
}
