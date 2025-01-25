package ongoing.backend.config.annotation;

@FunctionalInterface
public interface SupplierThrowable<T> {
    T get() throws Exception;
}
