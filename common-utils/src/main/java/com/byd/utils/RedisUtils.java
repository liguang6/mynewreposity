/**
 * Copyright 2018 cscc
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.byd.utils;

import com.alibaba.fastjson.JSON;
import redis.clients.jedis.Jedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author develop01
 * @email 
 * @date 2017-07-17 21:12
 */
@Component
public class RedisUtils {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Resource(name="redisTemplate")
    private ValueOperations<String, String> valueOperations;
    @Resource(name="redisTemplate")
    private HashOperations<String, String, Object> hashOperations;
    @Resource(name="redisTemplate")
    private ListOperations<String, Object> listOperations;
    @Resource(name="redisTemplate")
    private SetOperations<String, Object> setOperations;
    @Resource(name="redisTemplate")
    private ZSetOperations<String, Object> zSetOperations;
    /**  默认过期时长，单位：秒 */
    public final static long DEFAULT_EXPIRE = 60 * 60 * 24;
    /**  不设置过期时长 */
    public final static long NOT_EXPIRE = -1;
    
    /**  锁默认过期时长，单位：毫秒 */
    public final static long DEFAULT_EXPIRE_LOCK = 180000;
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final String LUA_UNLOCK_SCRIPT = "if redis.call(\"get\", KEYS[1]) == ARGV[1] " +
            "then " +
            "return redis.call(\"del\", KEYS[1]) " +
            "else " +
            "return 0 " +
            "end";
    
    @Value("${spring.redis.host}")
    private String host;
    
    @Value("${spring.redis.port}")
    private int port;
    
    @Value("${spring.redis.password}")
    private String password;
    
    public void expire(String key,long expire) {
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    public void set(String key, Object value, long expire){
        valueOperations.set(key, toJson(value));
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    public void set(String key, Object value){
        set(key, value, DEFAULT_EXPIRE);
    }

    public <T> T get(String key, Class<T> clazz, long expire) {
        String value = valueOperations.get(key);
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value == null ? null : fromJson(value, clazz);
    }

    public <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, NOT_EXPIRE);
    }

    public String get(String key, long expire) {
        String value = valueOperations.get(key);
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value;
    }

    public String get(String key) {
        return get(key, NOT_EXPIRE);
    }
    
    public List getList(String key) {
    	String value = valueOperations.get(key);
    	return value == null ? null : fromJson(value, List.class);
    }
    
    public Set getSet(String key) {
    	String value = valueOperations.get(key);
    	return value == null ? null : fromJson(value, Set.class);
    }
    
    public Map getMap(String key) {
    	String value = valueOperations.get(key);
    	return value == null ? null : fromJson(value, Map.class);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * Object转成JSON数据
     */
    private String toJson(Object object){
        if(object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String){
            return String.valueOf(object);
        }
        return JSON.toJSONString(object);
    }

    /**
     * JSON数据，转成Object
     */
    private <T> T fromJson(String json, Class<T> clazz){
        return JSON.parseObject(json, clazz);
    }
    

    /**
     * @param lockKey   加锁键
     * @param clientId  加锁客户端唯一标识(采用UUID)
     * @return
     */
    public boolean tryLock(String lockKey, String clientId) {
    	Jedis jedis=new Jedis(host,port);
    	jedis.auth(password);
    	String result = redisTemplate.execute((RedisCallback<String>) connection -> {
            return jedis.set(lockKey, clientId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, DEFAULT_EXPIRE_LOCK);
        });
    	if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }
    
    public boolean unlock(String lockKey, String clientId) {
    	List<String> keylist = new ArrayList<String>();
    	List<String> clientlist = new ArrayList<String>();
    	keylist.add(lockKey);
    	clientlist.add(clientId);
    	return deleteKey(keylist,clientlist);
    }
    
    /**
     * <p>解锁
     * <p>使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而 redis 锁自动过期失效的时候误删其他线程的锁
     */
    public boolean deleteKey(List<String> keys, List<String> args) {
    	Jedis jedis=new Jedis(host,port);
    	jedis.auth(password);
        Object result = redisTemplate.execute((RedisCallback<Object>) connection -> {
        	return jedis.eval(LUA_UNLOCK_SCRIPT, keys, args);
        });

        return result != null && Long.parseLong(result.toString()) > 0;
    }
    
}
