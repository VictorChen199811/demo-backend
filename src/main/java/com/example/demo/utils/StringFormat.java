package com.example.demo.utils;

public class StringFormat {
  public static String format(String template, Object... args) {
    for (Object arg : args) {
      template = template.replaceFirst("\\{\\}", arg.toString());
    }
    return template;
  }
}
