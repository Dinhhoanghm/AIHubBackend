package ongoing.backend.config.rxTemplate;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.log4j.Log4j2;
import ongoing.backend.config.annotation.SupplierThrowable;

@Log4j2
public class RxTemplate {
  private RxTemplate() {
  }

  public static <T> Single<T> rxSchedulerIo(SupplierThrowable<T> supplier) {
    return Single.just("io")
      .subscribeOn(Schedulers.io())
      .flatMap(s -> rxBlockingAsync(supplier));
  }

  public static <T> Single<T> rxSchedulerNewThread(SupplierThrowable<T> supplier) {
    return Single.just("new-thread")
      .subscribeOn(Schedulers.newThread())
      .flatMap(s -> rxBlockingAsync(supplier));
  }

  public static <T> Single<T> rxSchedulerComputing(SupplierThrowable<T> supplier) {
    return Single.just("computing")
      .subscribeOn(Schedulers.computation())
      .flatMap(s -> rxBlockingAsync(supplier));
  }

  private static <T> Single<T> rxBlockingAsync(SupplierThrowable<T> supplier) {
    return Single.create(emitter -> {
      try {
        log.info("[THREAD] {}, supplier: {}", Thread.currentThread().getName(), supplier.getClass().getName());
        final T value = supplier.get();
        emitter.onSuccess(value);
      } catch (Exception exception) {
        log.error("[RX-BLOCKING-ASYNC] cause", exception);
        emitter.onError(exception);
      }
    });
  }

  public static <T> void rxSchedulerNewThreadSubscribe(SupplierThrowable<T> supplier) {
    Single.just("new-thread")
      .subscribeOn(Schedulers.newThread())
      .flatMap(s -> rxBlockingAsync(supplier))
      .subscribe(
        result -> log.info("[rx-subscribe] execute job {}", result),
        throwable -> log.error("[rx-subscribe] fail to execute job", throwable));
  }

  public static <T> void rxSubscribe(Single<T> single) {
    single.subscribe(
      result -> log.info("[rx-subscribe] execute job {}", result),
      throwable -> log.error("[rx-subscribe] fail to execute job", throwable));
  }

  public static <T> void rxSubscribe(Single<T> single, String message) {
    single.subscribe(
      result -> log.info("[rx-subscribe] {}", message),
      throwable -> log.error("[rx-subscribe] fail to execute job", throwable));
  }

  public static <T> void rxSubscribe(Single<T> single1, Single<T> single2) {
    Single
      .zip(single1, single2, (t, t2) -> true)
      .subscribe(
        result -> log.info("[rx-subscribe] execute job {}", result),
        throwable -> log.error("[rx-subscribe] fail to execute job", throwable));
  }
}
