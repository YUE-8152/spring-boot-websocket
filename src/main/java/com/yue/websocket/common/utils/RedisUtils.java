package com.yue.websocket.common.utils;

import com.yue.websocket.config.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {
    private final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    @Resource
    private RedisConfig redisConfig;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据db获取对应的redisTemplate实例
     *
     * @param db:
     * @return: org.springframework.data.redis.core.RedisTemplate<java.io.Serializable, java.lang.Object>
     * @Author: YUE
     * @Date: 2020/9/25 9:53
     **/
    public RedisTemplate<Serializable, Object> getRedisTemplateByDb(final int db) {
        return redisConfig.setDataBase(db);
    }

    /**
     * 指定缓存失效时间
     *
     * @param key:
     * @param time:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 10:56
     **/
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            logger.error("设置key:{}缓存失效时间time:{} 异常", key, time);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key:
     * @return: long
     * @Author: YUE
     * @Date: 2020/10/29 10:57
     **/
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 10:57
     **/
    public boolean isKeyExist(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            logger.error("判断key:{}是否存在 异常", key);
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 删除缓存
     *
     * @param key: 可以传一个值 或多个
     * @return: void
     * @Author: YUE
     * @Date: 2020/10/29 11:03
     **/
    public void del(String... key) {
        try {
            if (key != null && key.length > 0) {
                if (key.length == 1) {
                    redisTemplate.delete(key[0]);
                } else {
                    redisTemplate.delete(CollectionUtils.arrayToList(key));
                }
            }
        } catch (Exception e) {
            logger.error("删除缓存key:{} 异常", key);
            e.printStackTrace();
        }
    }
// ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key:
     * @return: java.lang.Object
     * @Author: YUE
     * @Date: 2020/10/29 11:05
     **/
    public Object get(String key) {
        try {
            return key == null ? null : redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("获取key:{}的值 异常", key);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取String类型value值
     *
     * @param key:
     * @return: java.lang.String
     * @Author: YUE
     * @Date: 2020/10/29 11:05
     **/
    public String getStrValue(String key) {
        return get(key) == null ? null : get(key).toString();
    }

    /**
     * 放入key的值
     *
     * @param key:
     * @param value:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 11:09
     **/
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logger.error("设置key:{}的值 异常", key);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 存入key的值并设置过期时间
     *
     * @param key:
     * @param value:
     * @param time:  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 11:12
     **/
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            logger.error("设置将key:{}的值过期时间time:{} 异常", key, time);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 递增
     *
     * @param key:
     * @param delta:
     * @return: long
     * @Author: YUE
     * @Date: 2020/10/29 11:17
     **/
    public long incr(String key, long delta) {
        try {
            if (delta < 0) {
                throw new RuntimeException("递增因子必须大于0");
            }
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (RuntimeException e) {
            logger.error("redis中key:{}的值增加{} 异常", key, delta);
            e.printStackTrace();
            throw new RuntimeException("redis递增" + key + "的值异常");
        }
    }

    /**
     * 递减
     *
     * @param key:
     * @param delta:
     * @return: long
     * @Author: YUE
     * @Date: 2020/10/29 11:18
     **/
    public long decr(String key, long delta) {
        try {
            if (delta < 0) {
                throw new RuntimeException("递增因子必须大于0");
            }
            return redisTemplate.opsForValue().increment(key, -delta);
        } catch (RuntimeException e) {
            logger.error("redis中key:{}的值减少{} 异常", key, delta);
            e.printStackTrace();
            throw new RuntimeException("redis减少" + key + "的值异常");
        }
    }

    /** ================================Map================================= **/

    /**
     * 获取HashGet
     *
     * @param key:
     * @param item:
     * @return: java.lang.Object
     * @Author: YUE
     * @Date: 2020/10/29 11:22
     **/
    public Object hashGet(String key, String item) {
        try {
            return redisTemplate.opsForHash().get(key, item);
        } catch (Exception e) {
            logger.error("获取key:{}的Hash类型的数据key:{}的值 异常", key, item);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key:
     * @return: java.util.Map<java.lang.Object, java.lang.Object>
     * @Author: YUE
     * @Date: 2020/10/29 11:23
     **/
    public Map<Object, Object> hashGet(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            logger.error("获取key:{}的Hash类型所有key的值 异常", key);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置Hash类型的值
     *
     * @param key:
     * @param map:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 11:28
     **/
    public boolean hashSet(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            logger.error("设key:{}的Hash类型值 异常", key);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置Hash类型的值并设置过期时间
     *
     * @param key:
     * @param map:
     * @param time:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 11:28
     **/
    public boolean hashSet(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("设置key:{}的Hash类型值,过期时间time:{} 异常", key, time);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key:
     * @param item:
     * @param value:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 11:31
     **/
    public boolean hashSet(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            logger.error("设置key:{}的Hash类型值键key:{}的值 异常", key, item);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建,并设置过期时间
     *
     * @param key:
     * @param item:
     * @param value:
     * @param time:  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 11:33
     **/
    public boolean hashSet(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("设置key:{}的Hash类型值键key:{}的值及过期时间 异常", key, item);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除hash表中的值
     *
     * @param key:
     * @param item: 可以使多个 不能为null
     * @return: void
     * @Author: YUE
     * @Date: 2020/10/29 11:36
     **/
    public void hashDel(String key, Object... item) {
        try {
            redisTemplate.opsForHash().delete(key, item);
        } catch (Exception e) {
            logger.error("删除key:{}的Hash类型值键key:{}的值 异常", key, item);
            e.printStackTrace();
        }
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key:
     * @param item:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 11:38
     **/
    public boolean isHashKeyExist(String key, String item) {
        try {
            return redisTemplate.opsForHash().hasKey(key, item);
        } catch (Exception e) {
            logger.error("判断key:{}的Hash类型是否存在键key:{}的值 异常", key, item);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key:  键
     * @param item: 项
     * @param by:   要增加几(大于0)
     * @return: double
     * @Author: YUE
     * @Date: 2020/10/29 11:40
     **/
    public double hashIncr(String key, String item, double by) {
        try {
            return redisTemplate.opsForHash().increment(key, item, by);
        } catch (Exception e) {
            logger.error("将redis中key:{}的Hash类型的键key:{}的值新增异常", key, item);
            e.printStackTrace();
            throw new RuntimeException("redis新增Hash中" + key + "的值异常");
        }
    }

    /**
     * hash递减
     *
     * @param key:
     * @param item:
     * @param by:
     * @return: double
     * @Author: YUE
     * @Date: 2020/10/29 11:44
     **/
    public double hashDecr(String key, String item, double by) {
        try {
            return redisTemplate.opsForHash().increment(key, item, -by);
        } catch (Exception e) {
            logger.error("将redis中key:{}的Hash类型的键key:{}的值减少异常", key, item);
            e.printStackTrace();
            throw new RuntimeException("redis减少Hash中" + key + "的值异常");
        }
    }

// ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key:
     * @return: java.util.Set<java.lang.Object>
     * @Author: YUE
     * @Date: 2020/10/29 11:53
     **/
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            logger.error("获取key:{}的Set类型的值 异常", key);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key:
     * @param value:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 11:54
     **/
    public boolean sKeyExist(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            logger.error("查询key:{}的Set类型的值是的存在 异常", key);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将数据放入set缓存
     *
     * @param key:
     * @param values: 可以是多个
     * @return: long
     * @Author: YUE
     * @Date: 2020/10/29 11:59
     **/
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            logger.error("设置key:{}的Set类型的值 异常", key);
            e.printStackTrace();
            throw new RuntimeException("redis设置Set类型key：" + key + "的值异常");
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key:
     * @param time:   时间(秒)
     * @param values: 可以是多个
     * @return: long
     * @Author: YUE
     * @Date: 2020/10/29 12:30
     **/
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            logger.error("设置key:{}的Set类型的值 异常", key);
            e.printStackTrace();
            throw new RuntimeException("redis设置Set类型key：" + key + "的值异常");
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key:
     * @return: long
     * @Author: YUE
     * @Date: 2020/10/29 12:32
     **/
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            logger.error("获key:{}的Set类型的长度 异常", key);
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 移除值为value的
     *
     * @param key:
     * @param values:
     * @return: long
     * @Author: YUE
     * @Date: 2020/10/29 12:34
     **/
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            logger.error("删除key:{}的Set类型的值 异常", key);
            e.printStackTrace();
        }
        return 0;
    }
// ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key:
     * @param start: 开始
     * @param end:   结束 0 到 -1代表所有值
     * @return: java.util.List<java.lang.Object>
     * @Author: YUE
     * @Date: 2020/10/29 12:35
     **/
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            logger.error("获取key:{}的List类型的值 异常", key);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取list缓存的长度
     *
     * @param key:
     * @return: long
     * @Author: YUE
     * @Date: 2020/10/29 12:36
     **/
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            logger.error("获取key:{}的List类型的长度 异常", key);
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key:
     * @param index: 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return: java.lang.Object
     * @Author: YUE
     * @Date: 2020/10/29 12:37
     **/
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            logger.error("获取key:{}的List类型索引index:{}的值 异常", key, index);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将list放入缓存
     *
     * @param key:
     * @param value:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 12:41
     **/
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            logger.error("将key:{}的List类型放入redis中 异常", key);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将list放入缓存,并设置过期时间
     *
     * @param key:
     * @param value:
     * @param time:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 12:42
     **/
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("将key:{}的List类型放入redis中 异常", key);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将list放入缓存
     *
     * @param key:
     * @param value:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 12:43
     **/
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            logger.error("将key:{}的List类型放入redis中 异常", key);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将list放入缓存
     *
     * @param key:
     * @param value:
     * @param time:  时间(秒)
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 12:44
     **/
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("将key:{}的List类型放入redis中 异常", key);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key:
     * @param index:
     * @param value:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 12:47
     **/
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            logger.error("修改key:{}的List类型索引为index:{}的值 异常", key, index);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 移除N个值为value
     *
     * @param key:
     * @param count: 移除多少个
     * @param value:
     * @return: long 移除的个数
     * @Author: YUE
     * @Date: 2020/10/29 12:47
     **/
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            logger.error("移除key:{}的List类型count:{}个值 异常", key, count);
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * HyperLogLog类型添加数据
     *
     * @param key:
     * @param value:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 12:50
     **/
    public boolean pfAdd(String key, Object value) {
        try {
            Long add = redisTemplate.opsForHyperLogLog().add(key, value);
            if (add == 0) { //0已存在，1添加成功
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("设置键key:{}的HyperLogLog类型的值 异常", key);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * HyperLogLog类型统计数据
     *
     * @param key:
     * @return: long
     * @Author: YUE
     * @Date: 2020/10/29 12:51
     **/
    public long pfCount(String key) {
        try {
            Long size = redisTemplate.opsForHyperLogLog().size(key);
            return size;
        } catch (Exception e) {
            logger.error("统计键key:{}的HyperLogLog类型的值 异常", key);
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 指定缓存失效时间
     *
     * @param key:
     * @param time:
     * @param db:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 10:56
     **/
    public boolean expire(String key, long time, int db) {
        try {
            if (time > 0) {
                RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            logger.error("指定db:{}中key:{}缓存失效时间time异常", db, key);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key:
     * @param db:
     * @return: long
     * @Author: YUE
     * @Date: 2020/10/29 10:57
     **/
    public long getExpire(String key, int db) {
        RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key:
     * @param db:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 10:57
     **/
    public boolean isKeyExist(String key, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            logger.error("判断db:{}中key:{}是否存在 异常", db, key);
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 删除缓存
     *
     * @param db:
     * @param key: 可以传一个值 或多个
     * @return: void
     * @Author: YUE
     * @Date: 2020/10/29 11:03
     **/
    public void del(int db, String... key) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            if (key != null && key.length > 0) {
                if (key.length == 1) {
                    redisTemplate.delete(key[0]);
                } else {
                    redisTemplate.delete(CollectionUtils.arrayToList(key));
                }
            }
        } catch (Exception e) {
            logger.error("删除缓存db:{}中key:{} 异常", db, key);
            e.printStackTrace();
        }
    }
// ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key:
     * @param db:
     * @return: java.lang.Object
     * @Author: YUE
     * @Date: 2020/10/29 11:05
     **/
    public Object get(String key, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            return key == null ? null : redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("获取db:{}中key:{}的值 异常", db, key);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取String类型value值
     *
     * @param key:
     * @param db:
     * @return: java.lang.String
     * @Author: YUE
     * @Date: 2020/10/29 11:05
     **/
    public String getStrValue(String key, int db) {
        return get(key, db) == null ? null : get(key, db).toString();
    }

    /**
     * 放入key的值
     *
     * @param key:
     * @param value:
     * @param db:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 11:09
     **/
    public boolean set(String key, Object value, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logger.error("将key:{}的值存在db:{} 异常", key, db);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 存入key的值并设置过期时间
     *
     * @param key:
     * @param value:
     * @param time:  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 11:12
     **/
    public boolean setExpire(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            logger.error("存入key:{}的值并设置过期时间time:{} 异常", key, time);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 存入key的值并设置过期时间
     *
     * @param key:
     * @param value:
     * @param time:  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @param db:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 11:12
     **/
    public boolean set(String key, Object value, long time, int db) {
        try {
            if (time > 0) {
                RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value, db);
            }
            return true;
        } catch (Exception e) {
            logger.error("将key:{}的值存在db:{}设置过期时间time异常", key, db);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 递增
     *
     * @param key:
     * @param delta:
     * @param db:
     * @return: long
     * @Author: YUE
     * @Date: 2020/10/29 11:17
     **/
    public long incr(String key, long delta, int db) {
        try {
            if (delta < 0) {
                throw new RuntimeException("递增因子必须大于0");
            }
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (RuntimeException e) {
            logger.error("将db:{}中key:{}的值增加异常", db, key);
            e.printStackTrace();
            throw new RuntimeException("redis递增" + key + "的值异常");
        }
    }

    /**
     * 递减
     *
     * @param key:
     * @param delta:
     * @param db:
     * @return: long
     * @Author: YUE
     * @Date: 2020/10/29 11:18
     **/
    public long decr(String key, long delta, int db) {
        try {
            if (delta < 0) {
                throw new RuntimeException("递增因子必须大于0");
            }
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            return redisTemplate.opsForValue().increment(key, -delta);
        } catch (RuntimeException e) {
            logger.error("将db:{}中key:{}的值减少异常", db, key);
            e.printStackTrace();
            throw new RuntimeException("redis减少" + key + "的值异常");
        }
    }

    /** ================================Map================================= **/

    /**
     * 获取HashGet
     *
     * @param key:
     * @param item:
     * @param db:
     * @return: java.lang.Object
     * @Author: YUE
     * @Date: 2020/10/29 11:22
     **/
    public Object hashGet(String key, String item, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            return redisTemplate.opsForHash().get(key, item);
        } catch (Exception e) {
            logger.error("获取db:{}中key:{}的Hash类型的数据key的值 异常", db, key);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key:
     * @param db:
     * @return: java.util.Map<java.lang.Object, java.lang.Object>
     * @Author: YUE
     * @Date: 2020/10/29 11:23
     **/
    public Map<Object, Object> hashGet(String key, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            logger.error("获取db:{}中key:{}的Hash类型所有key的值 异常", db, key);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置Hash类型的值
     *
     * @param key:
     * @param map:
     * @param db:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 11:28
     **/
    public boolean hashSet(String key, Map<String, Object> map, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            logger.error("设置db:{}中key:{}的Hash类型值 异常", db, key);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置Hash类型的值并设置过期时间
     *
     * @param key:
     * @param map:
     * @param time:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 11:28
     **/
    public boolean hashSet(String key, Map<String, Object> map, long time, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time, db);
            }
            return true;
        } catch (Exception e) {
            logger.error("设置db:{}中key:{}的Hash类型值,过期时间异常", db, key);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key:
     * @param item:
     * @param value:
     * @param db:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 11:31
     **/
    public boolean hashSet(String key, String item, Object value, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            logger.error("设置db:{}中key:{}的Hash类型值键的值 异常", db, key);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建,并设置过期时间
     *
     * @param key:
     * @param item:
     * @param value:
     * @param time:  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @param db:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 11:33
     **/
    public boolean hashSet(String key, String item, Object value, long time, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time, db);
            }
            return true;
        } catch (Exception e) {
            logger.error("设置db:{}中key:{}的Hash类型的值及过期时间 异常", db, key);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除hash表中的值
     *
     * @param db:
     * @param key:
     * @param item: 可以使多个 不能为null
     * @return: void
     * @Author: YUE
     * @Date: 2020/10/29 11:36
     **/
    public void hashDel(int db, String key, Object... item) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            redisTemplate.opsForHash().delete(key, item);
        } catch (Exception e) {
            logger.error("删除db:{}中key:{}的Hash类型的值 异常", db, key);
            e.printStackTrace();
        }
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key:
     * @param item:
     * @param db:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 11:38
     **/
    public boolean isHashKeyExist(String key, String item, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            return redisTemplate.opsForHash().hasKey(key, item);
        } catch (Exception e) {
            logger.error("判断db:{}中key:{}的Hash类型是否存在键异常", db, key);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key:  键
     * @param item: 项
     * @param by:   要增加几(大于0)
     * @param db:
     * @return: double
     * @Author: YUE
     * @Date: 2020/10/29 11:40
     **/
    public double hashIncr(String key, String item, double by, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            return redisTemplate.opsForHash().increment(key, item, by);
        } catch (Exception e) {
            logger.error("将db:{}中key:{}的Hash类型的键的值新增异常", db, key);
            e.printStackTrace();
            throw new RuntimeException("redis新增Hash中" + key + "的值异常");
        }
    }

    /**
     * hash递减
     *
     * @param key:
     * @param item:
     * @param by:
     * @param db:
     * @return: double
     * @Author: YUE
     * @Date: 2020/10/29 11:44
     **/
    public double hashDecr(String key, String item, double by, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            return redisTemplate.opsForHash().increment(key, item, -by);
        } catch (Exception e) {
            logger.error("将db:{}中key:{}的Hash类型的键的值减少异常", db, key);
            e.printStackTrace();
            throw new RuntimeException("redis减少Hash中" + key + "的值异常");
        }
    }

// ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key:
     * @param db:
     * @return: java.util.Set<java.lang.Object>
     * @Author: YUE
     * @Date: 2020/10/29 11:53
     **/
    public Set<Object> sGet(String key, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            logger.error("获取db:{}中key:{}的Set类型的值 异常", db, key);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key:
     * @param value:
     * @param db:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 11:54
     **/
    public boolean sKeyExist(String key, Object value, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            logger.error("查询db:{}中key:{}的Set类型的值是的存在 异常", db, key);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将数据放入set缓存
     *
     * @param db:
     * @param key:
     * @param values: 可以是多个
     * @return: long
     * @Author: YUE
     * @Date: 2020/10/29 11:59
     **/
    public long sSet(int db, String key, Object... values) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            logger.error("将key:{}的Set类型的数据存入db:{} 异常", key, db);
            e.printStackTrace();
            throw new RuntimeException("redis设置Set类型key：" + key + "的值异常");
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param db:
     * @param key:
     * @param time:   时间(秒)
     * @param values: 可以是多个
     * @return: long
     * @Author: YUE
     * @Date: 2020/10/29 12:30
     **/
    public long sSetAndTime(int db, String key, long time, Object... values) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time, db);
            }
            return count;
        } catch (Exception e) {
            logger.error("将key:{}的Set类型的数据存入db:{} 异常", key, db);
            e.printStackTrace();
            throw new RuntimeException("redis设置Set类型key：" + key + "的值异常");
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key:
     * @param db:
     * @return: long
     * @Author: YUE
     * @Date: 2020/10/29 12:32
     **/
    public long sGetSetSize(String key, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            logger.error("获取db:{}中key:{}的Set类型的长度 异常", db, key);
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 移除值为value的
     *
     * @param db:
     * @param key:
     * @param values:
     * @return: long
     * @Author: YUE
     * @Date: 2020/10/29 12:34
     **/
    public long setRemove(int db, String key, Object... values) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            logger.error("删除db:{}中key:{}的Set类型的值 异常", db, key);
            e.printStackTrace();
        }
        return 0;
    }
// ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key:
     * @param start: 开始
     * @param end:   结束 0 到 -1代表所有值
     * @param db:
     * @return: java.util.List<java.lang.Object>
     * @Author: YUE
     * @Date: 2020/10/29 12:35
     **/
    public List<Object> lGet(String key, long start, long end, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            logger.error("获取db:{}中key:{}的List类型的值 异常", db, key);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取list缓存的长度
     *
     * @param key:
     * @param db:
     * @return: long
     * @Author: YUE
     * @Date: 2020/10/29 12:36
     **/
    public long lGetListSize(String key, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            logger.error("获取db:{}中key:{}的List类型的长度 异常", db, key);
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key:
     * @param index: 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return: java.lang.Object
     * @Author: YUE
     * @Date: 2020/10/29 12:37
     **/
    public Object lGetIndex(String key, long index, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            logger.error("获取db:{}中key:{}的List类型索引index的值 异常", db, key);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将list放入缓存
     *
     * @param key:
     * @param value:
     * @param db:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 12:41
     **/
    public boolean lSet(String key, Object value, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            logger.error("将key:{}的List类型放入db:{}中 异常", key, db);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将list放入缓存,并设置过期时间
     *
     * @param key:
     * @param value:
     * @param time:
     * @param db:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 12:42
     **/
    public boolean lSet(String key, Object value, long time, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time, db);
            }
            return true;
        } catch (Exception e) {
            logger.error("将key:{}的List类型放入db:{}中 异常", key, db);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将list放入缓存
     *
     * @param key:
     * @param value:
     * @param db:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 12:43
     **/
    public boolean lSet(String key, List<Object> value, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            logger.error("将key:{}的List类型放入db:{}中 异常", key, db);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将list放入缓存
     *
     * @param key:
     * @param value:
     * @param time:  时间(秒)
     * @param db:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 12:44
     **/
    public boolean lSet(String key, List<Object> value, long time, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time, db);
            }
            return true;
        } catch (Exception e) {
            logger.error("将key:{}的List类型放入db:{}中 异常", key, db);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key:
     * @param index:
     * @param value:
     * @param db:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 12:47
     **/
    public boolean lUpdateIndex(String key, long index, Object value, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            logger.error("修改db:{}键key:{}的List类型索引为index的值 异常", db, key);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 移除N个值为value
     *
     * @param key:
     * @param count: 移除多少个
     * @param value:
     * @param db:
     * @return: long 移除的个数
     * @Author: YUE
     * @Date: 2020/10/29 12:47
     **/
    public long lRemove(String key, long count, Object value, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            logger.error("移除db:{}键key:{}的List类型count值 异常", db, key);
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * HyperLogLog类型添加数据
     *
     * @param key:
     * @param value:
     * @param db:
     * @return: boolean
     * @Author: YUE
     * @Date: 2020/10/29 12:50
     **/
    public boolean pfAdd(String key, Object value, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            Long add = redisTemplate.opsForHyperLogLog().add(key, value);
            if (add == 0) { //0已存在，1添加成功
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("设置db:{}键key:{}的HyperLogLog类型的值 异常", db, key);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * HyperLogLog类型统计数据
     *
     * @param key:
     * @return: long
     * @Author: YUE
     * @Date: 2020/10/29 12:51
     **/
    public long pfCount(String key, int db) {
        try {
            RedisTemplate<Serializable, Object> redisTemplate = getRedisTemplateByDb(db);
            Long size = redisTemplate.opsForHyperLogLog().size(key);
            return size;
        } catch (Exception e) {
            logger.error("统计db:{}键key:{}的HyperLogLog类型的值 异常", db, key);
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 每天从1开始递增
     *
     * @param key:
     * @return: java.lang.String
     * @Author: YUE
     * @Date: 2020/10/14 19:37
     **/
    public String incrNumStartOneEveryDay(String key) {
        String num = "1";
        if (!isKeyExist(key)) {
            set(key, 0);
            Calendar cal = Calendar.getInstance();
            expire(key, (86400 - cal.get(Calendar.SECOND)
                    - cal.get(Calendar.MINUTE) * 60
                    - cal.get(Calendar.HOUR_OF_DAY) * 60 * 60));
        }
        num = String.format("%05d", incr(key, 1) % 10000);
        return num;
    }
}