<h1>Отчет по проекту: RxJava, реализация аналогичной RxJava-библиотеки</h1>

<h2>1. Архитектура реализованной системы</h2>
<p>Архитектура системы построена на принципах <strong>реактивного программирования</strong> и включает в себя основные компоненты, необходимые для работы с реактивными потоками данных, такими как <code>Observable</code>, <code>Observer</code>, <code>Scheduler</code> и операторы преобразования данных. Мы реализовали базовые компоненты библиотеки, а также ключевые возможности RxJava, включая поддержку многопоточности и обработку ошибок.</p>

<h3>Компоненты архитектуры:</h3>
<ul>
    <li><strong>Observer (Наблюдатель)</strong>: Интерфейс <code>Observer&lt;T&gt;</code> содержит три ключевых метода:
        <ul>
            <li><code>onNext(T item)</code> — вызывается при получении нового элемента.</li>
            <li><code>onError(Throwable t)</code> — вызывается в случае ошибки.</li>
            <li><code>onComplete()</code> — вызывается, когда поток завершился.</li>
        </ul>
    </li>
    <li><strong>Observable (Наблюдаемое)</strong>: Класс <code>Observable&lt;T&gt;</code> представляет поток данных. Он поддерживает подписку на события с использованием метода <code>subscribe()</code>.</li>
    <li><strong>Emitter (Эмиттер)</strong>: Интерфейс <code>Emitter&lt;T&gt;</code> используется для отправки данных и ошибок в поток. Он включает методы <code>onNext(T item)</code>, <code>onError(Throwable t)</code> и <code>onComplete()</code>.</li>
    <li><strong>Scheduler (Планировщик)</strong>: Интерфейс <code>Scheduler</code> представляет абстракцию для работы с потоками. У нас реализованы три типа планировщиков:
        <ul>
            <li><code>IOThreadScheduler</code> — для работы с потоками ввода-вывода.</li>
            <li><code>ComputationScheduler</code> — для вычислительных задач.</li>
            <li><code>SingleThreadScheduler</code> — для работы с одним потоком.</li>
        </ul>
    </li>
    <li><strong>Операторы преобразования данных</strong>: Реализованы операторы <code>map</code>, <code>filter</code>, <code>flatMap</code>, которые позволяют преобразовывать и фильтровать данные в потоке.</li>
    <li><strong>Disposable (Отмена подписки)</strong>: Интерфейс <code>Disposable</code> реализует логику отмены подписки.</li>
</ul>

<h2>2. Принципы работы Schedulers, их различия и области применения</h2>
<p><strong>Schedulers</strong> являются ключевым элементом для управления потоками в реактивных приложениях. В нашей реализации есть три основных типа планировщиков, каждый из которых предназначен для определенной цели.</p>

<h3>Типы планировщиков:</h3>
<ul>
    <li><strong>IOThreadScheduler</strong>: Использует пул потоков с кэшированием (<code>CachedThreadPool</code>). Используется для операций с внешними ресурсами, такими как работа с файловой системой, сетью или базой данных.</li>
    <li><strong>ComputationScheduler</strong>: Использует фиксированное количество потоков (<code>FixedThreadPool</code>). Применяется для вычислительных задач.</li>
    <li><strong>SingleThreadScheduler</strong>: Работает с одним потоком. Используется для задач, которые должны быть выполнены в одном потоке.</li>
</ul>

<h3>Основные принципы работы Schedulers:</h3>
<ul>
    <li><strong>subscribeOn()</strong> — задает поток, на котором будет выполняться подписка на <code>Observable</code>.</li>
    <li><strong>observeOn()</strong> — задает поток, на котором будут обрабатываться данные и производиться подписки на наблюдателя.</li>
</ul>

<h2>3. Процесс тестирования и основные сценарии</h2>
<p>Тестирование системы включает в себя проверку корректности работы всех ключевых компонентов и операторов, а также правильную обработку ошибок и работу с многозадачностью.</p>

<h3>Процесс тестирования:</h3>
<ul>
    <li><strong>Тестирование компонентов</strong>: Проверка работы <code>Observable</code>, <code>Observer</code>, <code>Emitter</code>, <code>Scheduler</code> и <code>Disposable</code>.</li>
    <li><strong>Тестирование обработки ошибок</strong>: Проверка правильности передачи ошибок в метод <code>onError()</code>.</li>
    <li><strong>Тестирование многозадачности</strong>: Использование разных планировщиков для тестирования многозадачности.</li>
    <li><strong>Тестирование операторов преобразования</strong>: Проверка корректности работы операторов <code>map</code>, <code>filter</code>, <code>flatMap</code>.</li>
</ul>

<h3>Пример теста для обработки ошибок:</h3>
<pre>
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
</pre>

<h2>4. Примеры использования реализованной библиотеки</h2>

<h3>Пример 1: Простой поток данных</h3>
<pre>
    Observable<String> observable = Observable.create(emitter -> {
        emitter.onNext("First message");
        emitter.onNext("Second message");
        emitter.onComplete();
    });

    observable.subscribe(new Observer<String>() {
        @Override
        public void onNext(String item) {
            System.out.println(item);
        }

        @Override
        public void onError(Throwable t) {
            System.err.println("Error: " + t.getMessage());
        }

        @Override
        public void onComplete() {
            System.out.println("Completed!");
        }
    });
</pre>
<p><strong>Вывод:</strong></p>
<pre>
    First message
    Second message
    Completed!
</pre>

<h3>Пример 2: Применение оператора <code>map</code></h3>
<pre>
    Observable<Integer> observable = Observable.create(emitter -> {
        emitter.onNext(1);
        emitter.onNext(2);
        emitter.onNext(3);
        emitter.onComplete();
    });

    observable.map(item -> item * 2)
        .subscribe(new Observer<Integer>() {
            @Override
            public void onNext(Integer item) {
                System.out.println(item); // Выведет 2, 4, 6
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error: " + t.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("Completed!");
            }
        });
</pre>
<p><strong>Вывод:</strong></p>
<pre>
    2
    4
    6
    Completed!
</pre>

<h3>Пример 3: Использование <code>Schedulers</code></h3>
<pre>
    Observable<String> observable = Observable.create(emitter -> {
        emitter.onNext("Hello from background thread");
        emitter.onComplete();
    });

    observable.subscribeOn(new IOThreadScheduler())
        .observeOn(new SingleThreadScheduler())
        .subscribe(new Observer<String>() {
            @Override
            public void onNext(String item) {
                System.out.println(item);
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error: " + t.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("Completed!");
            }
        });
</pre>
