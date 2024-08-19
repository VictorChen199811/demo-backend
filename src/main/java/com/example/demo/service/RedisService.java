package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService<T> {

  @Autowired
  private RedisTemplate<String, T> redisTemplate;

  // 保存列表到 Redis
  public void saveList(String key, List<T> list) {
    redisTemplate.opsForList().rightPushAll(key, list);
  }

  // 从 Redis 中获取列表
  public List<T> getList(String key) {
    return redisTemplate.opsForList().range(key, 0, -1);
  }

  // 保存列表到 Redis 并设置过期时间
  public void saveListWithExpiration(String key, List<T> list, long timeout, TimeUnit unit) {
    redisTemplate.opsForList().rightPushAll(key, list);
    redisTemplate.expire(key, timeout, unit);
  }
}
