package com.pizza.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * @author Kyle
 * 线程池相关工具类
 */
public class ThreadPoolTool {

    private Handler handler;
    private ExecutorService threadPool;
    private ScheduledExecutorService timerPool;
    private int corePoolSize = 0;
    private int timePoolSize = 1;
    private Type type;

    /**
     * 初始化固定线程数量的线程池
     *
     * @param corePoolSize 运行时线程池大小
     */
    public ThreadPoolTool fixedThreadPool(int corePoolSize) {
        if (corePoolSize > 1) {
            this.type = Type.FIXED_THREAD;
            this.corePoolSize = corePoolSize;
        } else {
            singleThreadPool();
        }
        return this;
    }

    /**
     * 初始化一个只支持一个线程的线程池,相当于newFixedThreadPool(1)
     */
    public ThreadPoolTool singleThreadPool() {
        this.type = Type.SINGLE_THREAD;
        this.corePoolSize = 1;
        return this;
    }

    /**
     * 初始化一个缓冲功能的线程池
     */
    public ThreadPoolTool cacheThreadPool(int corePoolSize) {
        this.type = Type.CACHED_THREAD;
        this.corePoolSize = corePoolSize;
        return this;
    }

    /**
     * 构造一个默认的线程池,线程池大小无限制
     *
     * @param corePoolSize 运行时线程池大小
     */
    public ThreadPoolTool normalThreadPool(int corePoolSize) {
        if (corePoolSize > 0) {
            this.type = Type.OTHER;
            this.corePoolSize = corePoolSize;
        } else {
            cacheThreadPool(5);
        }
        return this;
    }

    public void initThreadPool() {
        switch (type) {
            case CACHED_THREAD:
                threadPool = new ThreadPoolExecutor(corePoolSize,
                        Integer.MAX_VALUE,
                        20L,
                        TimeUnit.SECONDS,
                        new SynchronousQueue<>());
                break;
            case OTHER:
                threadPool = new ThreadPoolExecutor(corePoolSize,
                        Integer.MAX_VALUE,
                        20L,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<>());
                break;
            case FIXED_THREAD:
                threadPool = new ThreadPoolExecutor(corePoolSize,
                        corePoolSize,
                        20L,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<>());
                break;
            case SINGLE_THREAD:
                threadPool =
                        new ThreadPoolExecutor(corePoolSize,
                                1,
                                20L,
                                TimeUnit.SECONDS,
                                new LinkedBlockingQueue<>());
                break;
            default:
                this.type = Type.CACHED_THREAD;
                this.corePoolSize = 5;
                threadPool = new ThreadPoolExecutor(corePoolSize,
                        10,
                        20L,
                        TimeUnit.SECONDS,
                        new SynchronousQueue<>());
                break;
        }
    }

    public ThreadPoolTool initTimer(int timePoolSize) {
        this.timePoolSize = timePoolSize;
        timerPool = new ScheduledThreadPoolExecutor(timePoolSize,
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((ScheduledThreadPoolExecutor) timerPool).setRemoveOnCancelPolicy(true);
        }
        return this;
    }

    public static Handler getBackgroundHandler() {
        HandlerThread thread = new HandlerThread("background");
        thread.start();
        return new Handler(thread.getLooper());
    }

    /**
     * 在未来某个时间执行给定的命令
     * <p>该命令可能在新的线程、已入池的线程或者正调用的线程中执行，这由 Executor 实现决定。</p>
     *
     * @param command 命令
     */
    public void execute(Runnable command) {
        threadPoolOrNull();
        threadPool.execute(command);
    }

    /**
     * 在未来某个时间执行给定的命令链表
     * <p>该命令可能在新的线程、已入池的线程或者正调用的线程中执行，这由 Executor 实现决定。</p>
     *
     * @param taskList 任务队列
     */
    public void execute(List<Runnable> taskList) {
        if (threadPool == null) {
            initThreadPool();
        }
        for (Runnable command : taskList) {
            threadPool.execute(command);
        }
    }

    /**
     * 试图停止所有正在执行的活动任务
     * 试图停止所有正在执行的活动任务，暂停处理正在等待的任务，并返回等待执行的任务列表。
     * 无法保证能够停止正在处理的活动执行任务，但是会尽力尝试
     *
     * @return 等待执行的任务的列表
     */
    public List<Runnable> shutDownNow() {
        if (threadPool != null) {
            return threadPool.shutdownNow();
        }
        return new ArrayList<>();
    }

    /**
     * 判断线程池是否已关闭
     *
     * @return {@code true}: 是{@code false}: 否
     */
    public boolean isShutDown() {
        if (threadPool != null) {
            return threadPool.isShutdown();
        }
        return false;
    }

    /**
     * 关闭线程池后判断所有任务是否都已完成
     * 注意，除非首先调用 shutdown 或 shutdownNow，否则 isTerminated 永不为 true。
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public boolean isTerminated() {
        threadPoolOrNull();
        return false;
    }

    /**
     * 请求关闭、发生超时或者当前线程中断
     * 无论哪一个首先发生之后，都将导致阻塞，直到所有任务完成执行。
     *
     * @param timeout 最长等待时间
     * @param unit    时间单位
     *
     * @return {@code true}: 请求成功<br>{@code false}: 请求超时
     *
     * @throws InterruptedException 终端异常
     */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        threadPoolOrNull();
        return threadPool.awaitTermination(timeout, unit);
    }

    /**
     * 提交一个Callable任务用于执行
     * 如果想立即阻塞任务的等待，则可以使用{@code result = exec.submit(aCallable).get();}形式的构造。
     *
     * @param task 任务
     * @param <T>  泛型
     *
     * @return 表示任务等待完成的Future, 该Future的{@code get}方法在成功完成时将会返回该任务的结果。
     */
    public <T> Future<T> submit(Callable<T> task) {
        threadPoolOrNull();
        return threadPool.submit(task);
    }

    /**
     * 提交一个Runnable任务用于执行
     *
     * @param task   任务
     * @param result 返回的结果
     * @param <T>    泛型
     *
     * @return 表示任务等待完成的Future, 该Future的{@code get}方法在成功完成时将会返回该任务的结果。
     */
    public <T> Future<T> submit(Runnable task, T result) {
        threadPoolOrNull();
        return threadPool.submit(task, result);
    }

    /**
     * 提交一个Runnable任务用于执行
     *
     * @param task 任务
     *
     * @return 表示任务等待完成的Future, 该Future的{@code get}方法在成功完成时将会返回null结果。
     */
    public Future<?> submit(Runnable task) {
        threadPoolOrNull();
        return threadPool.submit(task);
    }

    /**
     * 执行给定的任务
     * <p>当所有任务完成时，返回保持任务状态和结果的Future列表。
     * 返回列表的所有元素的{@link Future#isDone}为{@code true}。
     * 注意，可以正常地或通过抛出异常来终止已完成任务。
     * 如果正在进行此操作时修改了给定的 collection，则此方法的结果是不确定的。</p>
     *
     * @param tasks 任务集合
     * @param <T>   泛型
     *
     * @return 表示任务的 Future 列表，列表顺序与给定任务列表的迭代器所生成的顺序相同，每个任务都已完成。
     *
     * @throws InterruptedException 如果等待时发生中断，在这种情况下取消尚未完成的任务。
     */
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        threadPoolOrNull();
        return threadPool.invokeAll(tasks);
    }

    /**
     * 执行给定的任务
     * <p>当所有任务完成或超时期满时(无论哪个首先发生)，返回保持任务状态和结果的Future列表。
     * 返回列表的所有元素的{@link Future#isDone}为{@code true}。
     * 一旦返回后，即取消尚未完成的任务。
     * 注意，可以正常地或通过抛出异常来终止已完成任务。
     * 如果此操作正在进行时修改了给定的 collection，则此方法的结果是不确定的。</p>
     *
     * @param tasks   任务集合
     * @param timeout 最长等待时间
     * @param unit    时间单位
     * @param <T>     泛型
     *
     * @return 表示任务的 Future 列表，列表顺序与给定任务列表的迭代器所生成的顺序相同。如果操作未超时，则已完成所有任务。如果确实超时了，则某些任务尚未完成。
     *
     * @throws InterruptedException 如果等待时发生中断，在这种情况下取消尚未完成的任务
     */
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws
            InterruptedException {
        threadPoolOrNull();
        return threadPool.invokeAll(tasks, timeout, unit);
    }

    /**
     * 执行给定的任务
     * <p>如果某个任务已成功完成（也就是未抛出异常），则返回其结果。
     * 一旦正常或异常返回后，则取消尚未完成的任务。
     * 如果此操作正在进行时修改了给定的collection，则此方法的结果是不确定的。</p>
     *
     * @param tasks 任务集合
     * @param <T>   泛型
     *
     * @return 某个任务返回的结果
     *
     * @throws InterruptedException 如果等待时发生中断
     * @throws ExecutionException   如果没有任务成功完成
     */
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        threadPoolOrNull();
        return threadPool.invokeAny(tasks);
    }

    /**
     * 执行给定的任务
     * <p>如果在给定的超时期满前某个任务已成功完成（也就是未抛出异常），则返回其结果。
     * 一旦正常或异常返回后，则取消尚未完成的任务。
     * 如果此操作正在进行时修改了给定的collection，则此方法的结果是不确定的。</p>
     *
     * @param tasks   任务集合
     * @param timeout 最长等待时间
     * @param unit    时间单位
     * @param <T>     泛型
     *
     * @return 某个任务返回的结果
     *
     * @throws InterruptedException 如果等待时发生中断
     * @throws ExecutionException   如果没有任务成功完成
     * @throws TimeoutException     如果在所有任务成功完成之前给定的超时期满
     */
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws
            InterruptedException, ExecutionException, TimeoutException {
        threadPoolOrNull();
        return threadPool.invokeAny(tasks, timeout, unit);
    }

    private void threadPoolOrNull() {
        if (threadPool == null) {
            initThreadPool();
        }
    }

    private void timerPoolOrNull() {
        if (timerPool == null) {
            initTimer(timePoolSize);
        }
    }

    public void runOnUi(Runnable runnable) {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        handler.post(runnable);
    }

    /**
     * 延迟执行Callable命令
     *
     * @param callable 命令
     * @param delay    延迟时间
     * @param unit     时间单位
     * @param <V>      泛型
     *
     * @return 可用于提取结果或取消的ScheduledFuture
     */
    public <V> ScheduledFuture<V> scheduleTimer(Callable<V> callable, long delay, TimeUnit unit) {
        timerPoolOrNull();
        return timerPool.schedule(callable, delay, unit);
    }

    /**
     * 延迟执行Runnable命令
     *
     * @param command 命令
     * @param delay   延迟时间
     * @param unit    单位
     *
     * @return 表示挂起任务完成的ScheduledFuture，并且其{@code get()}方法在完成后将返回{@code null}
     */
    public ScheduledFuture<?> scheduleTimer(Runnable command, long delay, TimeUnit unit) {
        timerPoolOrNull();
        return timerPool.schedule(command, delay, unit);
    }

    /**
     * 延迟并循环执行命令
     *
     * @param command      命令
     * @param initialDelay 首次执行的延迟时间
     * @param period       连续执行之间的周期
     * @param unit         时间单位
     *
     * @return 表示挂起任务完成的ScheduledFuture，并且其{@code get()}方法在取消后将抛出异常
     */
    public ScheduledFuture<?> scheduleWithFixedRateTimer(Runnable command, long initialDelay, long period,
                                                         TimeUnit unit) {
        timerPoolOrNull();
        return timerPool.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    /**
     * 延迟并以固定休息时间循环执行命令
     *
     * @param command      命令
     * @param initialDelay 首次执行的延迟时间
     * @param delay        每一次执行终止和下一次执行开始之间的延迟
     * @param unit         时间单位
     *
     * @return 表示挂起任务完成的ScheduledFuture，并且其{@code get()}方法在取消后将抛出异常
     */
    public ScheduledFuture<?> scheduleWithFixedDelayTimer(Runnable command, long initialDelay, long delay,
                                                          TimeUnit unit) {
        timerPoolOrNull();
        return timerPool.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    public void onDestroy() {
        handler = null;
        shutdownTimer();
        shutDown();
    }

    /**
     * 一般情况下不用调这个方法，因为shutdown会导致后续任务无法添加
     */
    public void shutdownTimer() {
        if (timerPool != null) {
            timerPool.shutdown();
            timerPool = null;
        }
    }

    /**
     * 待以前提交的任务执行完毕后关闭线程池
     * 启动一次顺序关闭，执行以前提交的任务，但不接受新任务。
     * 如果已经关闭，则调用没有作用
     */
    public void shutDown() {
        if (threadPool != null) {
            threadPool.shutdown();
            threadPool = null;
        }
    }

    public enum Type {
        FIXED_THREAD,
        CACHED_THREAD,
        SINGLE_THREAD,
        OTHER
    }
}