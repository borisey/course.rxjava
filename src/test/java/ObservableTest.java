import org.junit.jupiter.api.Test;
import org.rxjava.Observable;
import org.rxjava.*;

import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;

import org.rxjava.Observer;
import org.rxjava.SingleThreadScheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObservableTest {

    @Test
    public void testMapOperator() {
        Observable<Integer> observable = Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onNext(3);
            emitter.onComplete();
        });

        List<Integer> result = new ArrayList<>();
        observable.map(item -> item * 2)
                .subscribe(result::add);

        assertEquals(Arrays.asList(2, 4, 6), result);
    }

    @Test
    public void testFilterOperator() {
        Observable<Integer> observable = Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onNext(3);
            emitter.onComplete();
        });

        // применяем оператор filter
        List<Integer> result = new ArrayList<>();
        observable.filter(item -> item % 2 == 0)
                .subscribe(result::add);

        assertEquals(List.of(2), result);
    }

    @Test
    public void testFlatMapOperator() {
        Observable<Integer> observable = Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onComplete();
        });

        List<Integer> result = new ArrayList<>();
        observable.flatMap(item -> Observable.just(item, item + 1))
                .subscribe(result::add);

        // Проверяю, что результат правильный
        assertEquals(Arrays.asList(1, 2, 2, 3), result);
    }

    @Test
    public void testSchedulers() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);  // Синхронизирую потоки

        Observable<Object> observable = Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onComplete();
        }).subscribeOn(new SingleThreadScheduler());

        List<Integer> result = new ArrayList<>();
        observable.subscribe(item -> {
            result.add((Integer) item);
            latch.countDown();
        });

        latch.await();

        assertEquals(List.of(1), result);
    }

    @Test
    public void testErrorHandling() {
        List<String> received = new ArrayList<>();
        List<Throwable> errors = new ArrayList<>();

        Observable<String> observable = Observable.create(emitter -> {
            emitter.onNext("Hello");
            emitter.onError(new RuntimeException("Test error"));
        });

        observable.subscribe(new Observer<String>() {
            @Override
            public void onNext(String item) {
                received.add(item);
            }

            @Override
            public void onError(Throwable t) {
                errors.add(t);
            }

            @Override
            public void onComplete() {

            }
        });

        assertEquals(1, received.size());
        assertEquals("Hello", received.getFirst());

        assertEquals(1, errors.size());
        assertEquals("Test error", errors.getFirst().getMessage());
    }
}