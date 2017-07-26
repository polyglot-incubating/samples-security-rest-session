package org.chiwooplatform.tests;

import org.chiwooplatform.context.support.DateUtils;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateUtilsTest {
  @Test 
  public void getFormattedString() throws Exception {
    String val = DateUtils.getFormattedString(DateUtils.toLocalTime(1501033800000L));
    log.info("1501033800000L: {}", val);
    val = DateUtils.getFormattedString(DateUtils.toLocalTime(1501033560000L));
    log.info("1501033560000L: {}", val);
  }
}
