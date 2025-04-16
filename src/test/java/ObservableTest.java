import org.junit.jupiter.api.Test;
import org.rxjava.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.*;

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
        observable.filter(item -> item % 2 == 0)  // Оставляем только четные числа
                .subscribe(result::add);

        assertEquals(List.of(2), result);
    }
}