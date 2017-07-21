package org.chiwooplatform.samples.support;

import java.util.function.Predicate;

/**
 * Created by seonbo.shim on 2017-07-07.
 */
public interface UmFunctionals {

  Predicate<String> notEmptyString = v -> v != null && !"".equals(v);
  // Predicate<Map.Entry<?, String>> notEmptyMap = e -> e.getValue() != null &&
  // !e.getValue().isEmpty();

  static boolean notNull(String value) {
    return notEmptyString.test(value);
  }
  // static boolean notNull(Map.Entry<?, String> map) {
  // return notEmptyMap.test(map);
  // }
  // static <O, T> Consumer<O> accepting(final BiConsumer<? super O, ? super T> function, final T
  // args) {
  // Objects.requireNonNull(function, "The function cannot be null.");
  // return object -> function.accept(object, args);
  // }
}
