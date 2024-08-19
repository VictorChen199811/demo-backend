package com.example.demo.contorller;

import com.example.demo.dto.StockData;
import com.example.demo.dto.TaiwanTotalIndex;
import com.example.demo.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/home")
public class StockController {
  @Autowired
  private StockService stockService;

  @GetMapping("/getTodayStockData")
  @ResponseBody
  @Operation(summary = "取得今日上市股票收盤資訊", description = "取得今日上市股票收盤資訊")
  public ResponseEntity<List<StockData>> getTodayStockData() {
    try {
      return new ResponseEntity<>(stockService.getStockDataAll(), HttpStatus.OK);
    } catch (Exception e) {
      log.info(e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/getSingleStockData")
  @ResponseBody
  @Operation(summary = "取得個股收盤資訊", description = "取得個股收盤資訊")
  public ResponseEntity<List<StockData>> getTodaySingleStockData(@RequestParam(name = "date", required = false) LocalDate date, @RequestParam(name = "code", required = false) String code) {
    try {
      return new ResponseEntity<>(stockService.findSingleStockData(date, code), HttpStatus.OK);
    } catch (Exception e) {
      log.info(e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/getTaiwanTotalIndex")
  @ResponseBody
  @Operation(summary = "取得加權指數", description = "取得加權指數")
  public ResponseEntity<List<TaiwanTotalIndex>> getTaiwanTotalIndex(@RequestParam(name = "日期", required = false) LocalDate date) {
    try {
      return new ResponseEntity<>(stockService.getTotalIndex(date), HttpStatus.OK);
    } catch (Exception e) {
      log.info(e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
