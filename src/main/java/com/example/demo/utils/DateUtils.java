package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class DateUtils {
  public static final String DATE_FORMAT_YYYY_MM_DD = "yyyyMMdd";

  public static final String DATE_FORMAT_YYYY_MM_DD_SLASH ="yyyy/MM/dd";

  public static String getDateFormatYyyyMmDdSlash(LocalDate localDate){
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_YYYY_MM_DD);
    return localDate.format(formatter);
  }
}
