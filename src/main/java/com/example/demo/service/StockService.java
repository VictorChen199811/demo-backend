package com.example.demo.service;

import com.example.demo.dto.StockData;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.example.demo.dto.TaiwanTotalIndex;
import com.example.demo.utils.DateUtils;
import com.example.demo.utils.StringFormat;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StockService {

  private final RedisService<StockData> stockDataRedisService;
  private final String todayStockData = "TODAY_STOCK_DATA";
  private final CloseableHttpClient httpClient = HttpClients.createDefault();

  public StockService(RedisService stockDataRedisService) {
    this.stockDataRedisService = stockDataRedisService;
  }

  public List<StockData> getStockDataAll() throws Exception {
//    if (!stockDataRedisService.getList(todayStockData).isEmpty()) {
//      return stockDataRedisService.getList(todayStockData);
//    }
    return getStockOpenData();
  }

  public List<StockData> findSingleStockData(LocalDate date, String code) throws Exception {
    LocalDate searchDate = date != null ? date : LocalDate.now();
    String url = StringFormat.format("https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&date={}&stockNo={}", DateUtils.getDateFormatYyyyMmDdSlash(searchDate), code);
    HttpGet request = new HttpGet(url);
    try (CloseableHttpResponse response = httpClient.execute(request)) {
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        // 獲取 Json 數據
        String jsonData = EntityUtils.toString(entity);
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
        JsonArray data = jsonObject.getAsJsonArray("data");

        List<StockData> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy/MM/dd");

        for (int i = 0; i < data.size(); i++) {
          JsonArray row = data.get(i).getAsJsonArray();
          log.info(row.toString());
          StockData stockData = new StockData();
          stockData.setDate(LocalDate.parse(row.get(0).getAsString(), formatter));
          stockData.setTradeVolume(row.get(1).getAsString());
          stockData.setTradeValue(row.get(2).getAsString());
          stockData.setOpeningPrice(row.get(3).getAsString());
          stockData.setHighestPrice(row.get(4).getAsString());
          stockData.setLowestPrice(row.get(5).getAsString());
          stockData.setClosingPrice(row.get(6).getAsString());
          stockData.setChange(row.get(7).getAsString());
          stockData.setTransaction(row.get(8).getAsString());

          result.add(stockData);
        }
        return result;
      }
    }
    return new ArrayList<>();
  }

  private List<StockData> getStockOpenData() throws Exception {
    HttpGet request = new HttpGet("https://www.twse.com.tw/exchangeReport/STOCK_DAY_ALL?response=open_data");

    try (CloseableHttpResponse response = httpClient.execute(request)) {
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        // 獲取 CSV 數據
        String csvData = EntityUtils.toString(entity);
        // 解析並轉換為 StockData 列表
        return parseCsvToStockData(csvData).orElseGet(ArrayList::new);
      }
    }
    return new ArrayList<>();
  }

  private Optional<List<StockData>> parseCsvToStockData(String csvData) {
    List<StockData> stockDataList = new ArrayList<>();

    try (CSVParser csvParser = CSVFormat.DEFAULT
            .withFirstRecordAsHeader()
            .parse(new StringReader(csvData))) {

      // 並行流處理 CSV 記錄
      stockDataList = csvParser.getRecords().stream()
              .parallel() // 使用並行流
              .map(this::mapToStockData) // 將每個 CSVRecord 映射為 StockData
              .collect(Collectors.toList()); // 收集結果到 List
    } catch (Exception e) {
      log.error("取得 今日收盤價 失敗", e);
    }
    stockDataRedisService.saveListWithExpiration(todayStockData, stockDataList, 1, TimeUnit.HOURS);

    return Optional.of(stockDataList);
  }

  private StockData mapToStockData(CSVRecord csvRecord) {
    StockData stockData = new StockData();
    stockData.setCode(csvRecord.get("證券代號"));
    stockData.setName(csvRecord.get("證券名稱"));
    stockData.setTradeVolume(csvRecord.get("成交股數"));
    stockData.setTradeValue(csvRecord.get("成交金額"));
    stockData.setOpeningPrice(csvRecord.get("開盤價"));
    stockData.setHighestPrice(csvRecord.get("最高價"));
    stockData.setLowestPrice(csvRecord.get("最低價"));
    stockData.setClosingPrice(csvRecord.get("收盤價"));
    stockData.setChange(csvRecord.get("漲跌價差"));
    stockData.setTransaction(csvRecord.get("成交筆數"));

    return stockData;
  }

  public List<TaiwanTotalIndex> getTotalIndex(LocalDate date) throws Exception {
    LocalDate searchDate = date != null ? date : LocalDate.now();
    HttpGet request = new HttpGet(StringFormat.format("https://www.twse.com.tw/en/indicesReport/MI_5MINS_HIST?response=json&date={}", DateUtils.getDateFormatYyyyMmDdSlash(searchDate)));
    try (CloseableHttpResponse response = httpClient.execute(request)) {
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        // 獲取 Json 數據
        String jsonData = EntityUtils.toString(entity);
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
        JsonArray data = jsonObject.getAsJsonArray("data");

        List<TaiwanTotalIndex> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        for (int i = 0; i < data.size(); i++) {
          JsonArray row = data.get(i).getAsJsonArray();

          TaiwanTotalIndex index = new TaiwanTotalIndex();
          index.setDate(LocalDate.parse(row.get(0).getAsString(), formatter));
          index.setOpeningIndex(row.get(1).getAsString());
          index.setHighestIndex(row.get(2).getAsString());
          index.setLowestIndex(row.get(3).getAsString());
          index.setClosingIndex(row.get(4).getAsString());

          result.add(index);
        }
        return result;
      }

    }
    return new ArrayList<>();
  }
}
