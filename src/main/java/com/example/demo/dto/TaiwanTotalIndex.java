package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaiwanTotalIndex {
  private LocalDate date;
  private String OpeningIndex;
  private String HighestIndex;
  private String LowestIndex;
  private String ClosingIndex;
}
