package com.rxjava.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.rxjava.test.entity.UserEntity;
import com.rxjava.test.net.ReqMultiple;
import com.rxjava.test.student.Student;
import com.rxjava.test.student.StudentManager;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "RXJAVATEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.interval).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable<Long> a1 = Observable.interval(1, TimeUnit.SECONDS).take(5);
                a1.subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.i(TAG ,"interval accept aLong = " + aLong);
                    }
                });
            }
        });


        findViewById(R.id.dispose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        Log.i(TAG, "ObservableEmitter 1");
                        try {
                            Thread.sleep(2000);
                        }catch (Exception t)
                        {

                        }
                        Log.i(TAG, "ObservableEmitter 2");
                        if(e.isDisposed())
                        {
                            Log.i(TAG, "ObservableEmitter is Disposed");
                            return;
                        }

                        e.onNext("1111");
                        try {
                            Thread.sleep(1000);
                        }catch (Exception tt)
                        {

                        }
                        e.onNext("2222");
                    }
                }).subscribeOn(Schedulers.newThread());

                Disposable disposable = observable.subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "s = " + s);
                    }
                });

                try {
                    Thread.sleep(500);
                }catch (Exception e)
                {

                }
                disposable.dispose();
            }
        });

        //connect之后才发射数据
        findViewById(R.id.connectObserve).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ConnectableObservable<String> connectableObservable = Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        Log.i(TAG, "connectableObservable start");
                        e.onNext("uuii");
                        Log.i(TAG, "connectableObservable end");
                    }
                }).publish();


                connectableObservable.subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "connectableObservable s = " + s);
                    }
                });

                connectableObservable.subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "connectableObservable1 s = " + s);
                    }
                });
                Observable<String> stringObservable = connectableObservable.refCount().subscribeOn(Schedulers.newThread());
                stringObservable.subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "stringObservable1 s = " + s);
                    }
                });
                stringObservable.subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "stringObservable2 s = " + s);
                    }
                });

                stringObservable.subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "stringObservable3 s = " + s);
                    }
                });

                connectableObservable.connect();
            }
        });

        //发送所有的数据，会缓存之前的数据
        findViewById(R.id.ReplaySubject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaySubject<String> replaySubject = ReplaySubject.create();
                replaySubject.subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "ReplaySubject s = " + s);
                    }
                });

                replaySubject.onNext("111");
                replaySubject.onNext("222");
                replaySubject.onNext("333");

                replaySubject.subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "ReplaySubject1 s = " + s);
                    }
                });
            }
        });

        //只发送最后一条数据
        findViewById(R.id.AsyncSubject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncSubject<String> asyncSubject = AsyncSubject.create();
                asyncSubject.subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "asyncSubject s = " + s);
                    }
                });

                asyncSubject.onNext("111");
                asyncSubject.onNext("222");
                asyncSubject.onNext("333");
                asyncSubject.onComplete();
            }
        });

        //后面的订阅会收到之前的最后发射的消息
        findViewById(R.id.BehaviorSubject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BehaviorSubject<String> behaviorSubject = BehaviorSubject.create();

                behaviorSubject.subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "behaviorSubject s = " + s);
                    }
                });

                behaviorSubject.onNext("kkkk");

                behaviorSubject.onNext("0000");
                behaviorSubject.subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "behaviorSubject1 s = " + s);
                    }
                });


                behaviorSubject.onNext("jjjj");
            }
        });

        //订阅之后才发送消息
        findViewById(R.id.PublishSubject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublishSubject<String> publishSubject = PublishSubject.create();
                publishSubject.subscribeOn(Schedulers.io()).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG,"publishSubject s = " + s);
                    }
                });
                publishSubject.onNext("llll");
                publishSubject.onNext("2222");
                try {
                    Thread.sleep(3000);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                publishSubject.onNext("3333");
            }
        });

        //先到就先订阅哪个
        findViewById(R.id.ambWith).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable<Long> a1 = Observable.interval(5, TimeUnit.SECONDS).take(5);
                Observable<Long> a2 = Observable.interval(2, TimeUnit.SECONDS).take(5).map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return aLong * 2 + 100;
                    }
                });

                a2.ambWith(a1).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.i(TAG, "s = " + aLong);
                    }
                });
            }
        });

        //以withLatestFrom的调用者为准，去匹配后面的数据
        findViewById(R.id.withLatestFrom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable<Long> a1 = Observable.interval(1, TimeUnit.SECONDS).take(5);
                Observable<Long> a2 = Observable.interval(3, TimeUnit.SECONDS).take(5).map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return aLong * 2 + 100;
                    }
                });
                a2.withLatestFrom(a1, new BiFunction<Long, Long, String>() {
                    @Override
                    public String apply(Long aLong, Long aLong2) throws Exception {
                        return aLong + "-" + aLong2;
                    }
                }).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "s = " + s);
                    }
                });
            }
        });

        //以最新的数据匹配
        findViewById(R.id.combineLatest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable<Long> a1 = Observable.interval(1, TimeUnit.SECONDS).take(5);
                Observable<Long> a2 = Observable.interval(2, TimeUnit.SECONDS).take(5).map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return aLong * 2 + 100;
                    }
                });

                Observable.combineLatest(a1, a2, new BiFunction<Long, Long, String>() {
                    @Override
                    public String apply(Long aLong, Long aLong2) throws Exception {
                        return aLong + "-" + aLong2;
                    }
                }).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, " s = " + s);
                    }
                });
            }
        });

        findViewById(R.id.zip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable<Long> a1 = Observable.interval(1, TimeUnit.SECONDS).take(6);
                Observable<Long> a2 = Observable.interval(2, TimeUnit.SECONDS).take(5).map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return aLong * 2 + 100;
                    }
                });

                Observable.zip(a1, a2, new BiFunction<Long, Long, String>() {
                    @Override
                    public String apply(Long aLong, Long aLong2) throws Exception {
                        return aLong + "-" + aLong2;
                    }
                }).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "subscribe s = " + s);
                    }
                });
            }
        });

        findViewById(R.id.mergeWith).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable<Long> a1 = Observable.interval(1, TimeUnit.SECONDS).take(5);
                Observable<Long> a2 = Observable.interval(2, TimeUnit.SECONDS).take(5).map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return aLong * 2 + 100;
                    }
                });
                a2.mergeWith(a1).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.i(TAG, "subscribe aLong = " + aLong);
                }
            });
            }
        });

        findViewById(R.id.buffer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable.range(1, 5).buffer(2).subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        Log.i(TAG, "integers = " + integers );
                    }
                });

                Observable.range(1, 5).buffer(3, 1).subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        Log.i(TAG, "integers = " + integers );
                    }
                });

                Observable.interval(1, TimeUnit.SECONDS).take(10).buffer(3, TimeUnit.SECONDS).subscribe(new Consumer<List<Long>>() {
                    @Override
                    public void accept(List<Long> longs) throws Exception {
                        Log.i(TAG, "longs = " + longs );
                    }
                });
            }
        });

        findViewById(R.id.window).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable.interval(1, TimeUnit.SECONDS).take(10).window(3, TimeUnit.SECONDS)
                        .observeOn(Schedulers.io()).subscribe(new Consumer<Observable<Long>>() {
                    @Override
                    public void accept(Observable<Long> longObservable) throws Exception {
                        logMessage("aLong1 = " );

                        Iterable<Long> value = longObservable.blockingIterable();
                        Iterator<Long> iterator = value.iterator();
                        while (iterator.hasNext())
                        {
                            logMessage("tmep = " + iterator.next());
                        }
//                        longObservable.subscribe(new Consumer<Long>() {
//                            @Override
//                            public void accept(Long aLong) throws Exception {
//                                logMessage("aLong = " + aLong );
//                            }
//                        });
                    }
                });
            }
        });

        //数据变化后，debound设置的时间后才执行操作
        findViewById(R.id.debound).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> subscriber) throws Exception {
                        if (subscriber.isDisposed()) return;
                        try {
                            for (int i = 0; i < 10; i++) {
                                logMessage("accept i = " + i);
                                subscriber.onNext(i);
                                try {
                                    Thread.currentThread().sleep(i * 1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            subscriber.onComplete();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.newThread()).debounce(5, TimeUnit.SECONDS).subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        logMessage("accept integer = " + integer);
                    }
                });
            }
        });

        findViewById(R.id.exception).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Observable.create(new ObservableOnSubscribe<Integer>() {
//                    @Override
//                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
//                        e.onNext(1 / 0);
//                    }
//                }).subscribe(new Consumer<Integer>() {
//                    @Override
//                    public void accept(Integer integer) throws Exception {
//                        logMessage("exception integer = " + integer);
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        logMessage("exception throwable = " + throwable.getMessage());
//                    }
//                });

                Observable.just(0, 10).map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return 10 / integer +"";
                    }
                }).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        logMessage("s = " + s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        logMessage("throwable = " + throwable.getMessage());
                    }
                });
            }
        });

        //rxjava中observable不支持背压，flowable才支持背压。对于再同一线程及消费的速度比生产的速度快时，不会产生背压操作，应使用
        //observalbe, 不适用flowable，应为flowable效率低些

        findViewById(R.id.backpressure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Flowable.create(new FlowableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                        int i = 0;
                        while (!e.isCancelled())
                        {
                            long request = e.requested();
                            if(request == 0) continue;
                            logMessage("emitter request = " + request + " i = " + i);
                            e.onNext(i++);
                        }
                    }
                }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.newThread()).observeOn(Schedulers.newThread()).subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        subscription = s;
                        s.request(1);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        logMessage("onNext integer = " + integer);
                        try {
                            Thread.sleep(1000);
                        }catch (Exception e)
                        {

                        }
                        subscription.request(1);

                    }

                    @Override
                    public void onError(Throwable t) {
                        logMessage("onError t = " + t.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        });

        findViewById(R.id.netReq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String DEFUALT_TIMEOUT_MINUTE = "1";
                String DEFUALT_ROLE_CODE = "organizer";
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("loginName", "10@163.com");
                params.put("password", "111111");
                params.put("deviceUID", UUID.randomUUID().toString());
                params.put("timeout", DEFUALT_TIMEOUT_MINUTE);//测试数据
                params.put("serviceType", 0 + "");
                params.put("roleCode", DEFUALT_ROLE_CODE);


                ReqMultiple.reqLogin("qa100.gensee.com", params).subscribe(new Consumer<UserEntity>() {
                    @Override
                    public void accept(UserEntity userEntity) throws Exception {
                        Log.i(TAG, "reqLogin userEntity = " + userEntity.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(TAG, "reqLogin throwable = " + throwable.getMessage());
                    }
                });
            }
        });
    }
    Subscription subscription = null;
    private void logMessage(String value)
    {
        Log.i(TAG, value + " " + "theadName = " + Thread.currentThread().getName());
    }
}
