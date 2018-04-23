package com.rxjava.test.student;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class StudentManager {
    private static final String TAG = "StudentManager";
    private List<Student> students = new ArrayList<Student>();
    public void init()
    {
        students.clear();

        Observable.range(0, 10).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "init index = " + integer);

                Student student = new Student();
                student.setAge(integer + 10);
                student.setName(integer + "s");
                student.setId(integer);

                students.add(student);
            }
        });
    }

    public Observable<String> queryByIds(final int id1, final int id2)
    {
        return Observable.interval(1, TimeUnit.SECONDS).take(5).flatMap(new Function<Long, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Long integer) throws Exception {
                List<String> str = new ArrayList<String>();
                str.add(integer + 1 + "");
                str.add(integer + 2 + "");
                str.add(integer + 3 + "");
                Log.i("RXJAVATEST", " thread id = " + Thread.currentThread().getId() + " : " + Thread.currentThread().getName());
                return Observable.fromIterable(str).subscribeOn(Schedulers.newThread());
            }
        });
    }

    public Observable<Student> queryById(final int id)
    {
        Observable<Student> observable = Observable.create(new ObservableOnSubscribe<Student>() {
            @Override
            public void subscribe(ObservableEmitter<Student> e) throws Exception {
                Student query = null;
                for(Student student : students)
                {
                    if(student.getId() == id)
                    {
                        query = student;
                        break;
                    }
                }

                if(null != query)
                {
                    e.onNext(query);
                    e.onComplete();
                }else
                {
                    e.onError(new Throwable("No Found This Students id = " + id ));
                }
            }
        });

        return observable;
    }


    public <T> Observable<T> reqData(final Class<T> tClass)
    {
        Observable<T> t = Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> e) throws Exception {
                T a = tClass.newInstance();
                e.onNext(a);
            }
        });
        return t;
    }
}
