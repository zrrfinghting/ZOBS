package com.zero.logic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * token生成校验接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/7/11
 */
@RestController
@RequestMapping("token")
public class TokenController {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

}
