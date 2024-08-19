package com.example.demo.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StockData {
  @SerializedName("日期")
  public LocalDate date;
  @SerializedName("證券代號")
  public String code;
  @SerializedName("證券名稱")
  public String name;
  @SerializedName("成交股數")
  public String tradeVolume;
  @SerializedName("成交金額")
  public String tradeValue;
  @SerializedName("開盤價")
  public String openingPrice;
  @SerializedName("最高價")
  public String highestPrice;
  @SerializedName("最低價")
  public String lowestPrice;
  @SerializedName("收盤價")
  public String closingPrice;
  @SerializedName("漲跌價差")
  public String change;
  @SerializedName("成交筆數")
  public String transaction;

  @Override
  public String toString() {
    return "StockData{" +
            "date=" + date +
            ", code='" + code + '\'' +
            ", name='" + name + '\'' +
            ", tradeVolume='" + tradeVolume + '\'' +
            ", tradeValue='" + tradeValue + '\'' +
            ", openingPrice='" + openingPrice + '\'' +
            ", highestPrice='" + highestPrice + '\'' +
            ", lowestPrice='" + lowestPrice + '\'' +
            ", closingPrice='" + closingPrice + '\'' +
            ", change='" + change + '\'' +
            ", transaction='" + transaction + '\'' +
            '}';
  }
}
