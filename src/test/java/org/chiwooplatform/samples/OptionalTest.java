package org.chiwooplatform.samples;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OptionalTest {


  private Optional<String> optional;

  @Before
  public final void setUp() throws Exception {}

  @Test
  public final void ut1001() {
    optional = Optional.ofNullable(null);
    log.info("optinal.isPresent(): {}", optional.isPresent());
    log.info("optinal.orElse('none'): {}", optional.orElse("none"));
    log.info("optinal.orElseGet(() -> 'none'): {}", optional.orElseGet(() -> "none"));
    log.info("optinal.map(s -> s + changed).orElse('none'): {}",
        optional.map(s -> s + " changed").orElse("none"));

  }

  @Test
  public final void ut1002() {
    optional = Optional.ofNullable("apple");
    log.info("optinal.isPresent(): {}", optional.isPresent());
    log.info("optinal.orElse('none'): {}", optional.orElse("none"));
    log.info("optinal.orElseGet(() -> 'none'): {}", optional.orElseGet(() -> "none"));
    log.info("optinal.map(s -> s + changed).orElse('none'): {}",
        optional.map(s -> s + " changed").orElse("none"));
    optional.ifPresent((s) -> System.out
        .println("optional.ifPresent(): " + s.charAt(0) + "-" + s.charAt(s.length() - 1)));
  }


  @Test
  public final void ut1003_list() {
    String[] arr = new String[] {"apple", "banana", "melon"};
    // arr = null;
    if (arr != null) {
      List<?> list = Arrays.asList(arr).stream().map(v -> "<" + v + ">").collect(Collectors.toList());
      for (Object val : list) {
        System.out.println(val);
      }
    }



    // Optional optional = Optional.ofNullable(arr);
    // optional.map((v) -> Arrays.asList(v).stream().map(v2 -> "<"+ v + ">"));
  }
}
