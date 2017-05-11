package com.yanzhenjie.nohttp.rx;

import com.yanzhenjie.nohttp.rest.IProtocolRequest;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SyncRequestExecutor;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by Yan Zhenjie on 2017/5/9.
 */
public class RxNoHttp {

    public <T> Observable<T> get(final IProtocolRequest<T> request) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> e) throws Exception {
                Response<T> response = SyncRequestExecutor.INSTANCE.execute(request);
                if (response.isSucceed()) {
                    e.onNext(response.get());
                } else {
                    e.onError(response.getException());
                }
                e.onComplete();
            }
        });
    }

}
