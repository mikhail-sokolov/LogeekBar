package logeek.domain;


import com.google.common.base.Optional;

/**
 * Created by msokolov on 8/14/2016.
 */
public interface Storage<T> {
    Optional<T> get();
}
