package until.memcach;

import com.money.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;

/**
 * Created by liumin on 15/7/2.
 * redis服务类
 */

@Component
public class MemCachService {

    @Autowired
    JedisConnectionFactory jedisConnectionFactory;

    private Jedis jedis = null;

    private static MemCachService redisService;

    MemCachService() {
        redisService = this;
    }

    public Jedis getShareJedisPoolConnection() {
        JedisConnection jedisConnection = jedisConnectionFactory.getConnection();
        jedis = jedisConnection.getNativeConnection();
        return jedis;
    }

    /**
     * 覆盖已有键值对
     */
    public static void MemCachSet(String Key, String Vaule) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            shardedJedis.set(Key, Vaule);
        } catch (Throwable e) {

        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }
    }

    public static void MemCachSet(byte[] Key, byte[] Vaule) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            shardedJedis.set(Key, Vaule);
        } catch (Throwable e) {

        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }

        //shardedPool.returnResourceObject( shardedJedis );
    }

    /**
     * 通过KEY获取值
     */
    public static String MemCachgGet(String Key) {
        Jedis shardedJedis = redisService.getShareJedisPoolConnection();
        try {
            return shardedJedis.get(Key);
        } catch (Exception e) {
            return "";
        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }

    }

    public static byte[] MemCachgGet(byte[] Key) {
        Jedis shardedJedis = redisService.getShareJedisPoolConnection();
        try {
            return shardedJedis.get(Key);
        } catch (Exception e) {
            return null;
        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }

    }

    /**
     * 检测一个键值是否存在
     */
    public static boolean KeyIsExists(String Key) {
        Jedis shardedJedis = redisService.getShareJedisPoolConnection();
        try {
            boolean IsExists = shardedJedis.exists(Key);
            //shardedPool.returnResourceObject( shardedJedis );
            return IsExists;
        } catch (Exception e) {
            return false;
        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }
    }

    /**
     * 插入一个键值
     */
    public static void InsertValue(String Key, String Value) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            shardedJedis.setnx(Key, Value);
        } catch (Exception e) {

        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }

        //shardedPool.returnResourceObject( shardedJedis );
    }

    /**
     * 在已有的键里追加值
     */
    public void AppendValue(String Key, String Value) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            shardedJedis.append(Key, Value);
        } catch (Exception e) {

        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }

        //shardedPool.returnResourceObject( shardedJedis );
    }

    /**
     * 插入键值对时  附加失效时间
     */
    public static void InsertValueWithTime(String Key, int time, String Value) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            shardedJedis.setex(Key, time, Value);
        } catch (Exception e) {

        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }

        //shardedPool.returnResourceObject( shardedJedis );
    }

    /**
     * 删除一对键值对
     */
    public static void RemoveValue(String Key) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            if (shardedJedis.exists(Key)) {
                shardedJedis.del(Key);
            }
        } catch (Exception e) {

        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }

        //shardedPool.returnResourceObject( shardedJedis );
    }

    public static void RemoveValue(byte[] Key) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            if (shardedJedis.exists(Key)) {
                shardedJedis.del(Key);
            }
        } catch (Exception e) {

        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }

        //shardedPool.returnResourceObject( shardedJedis );
    }

    /**
     * 递增
     *
     * @param Key
     * @param num
     */
    public static void increment(String Key, long num) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            if (shardedJedis.exists(Key)) {
                shardedJedis.incrBy(Key, num);
            }
        } catch (Exception e) {

        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }
    }

    public static void increment(byte[] Key, long num) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            if (shardedJedis.exists(Key)) {
                shardedJedis.incrBy(Key, num);
            }
        } catch (Exception e) {

        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }
    }

    /**
     * 递减
     *
     * @param Key
     * @param num
     */
    public static void decrement(String Key, long num) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            if (shardedJedis.exists(Key)) {
                shardedJedis.decrBy(Key, num);
            }
        } catch (Exception e) {

        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }
    }

    public static void decrement(byte[] Key, long num) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            if (shardedJedis.exists(Key)) {
                shardedJedis.decrBy(Key, num);
            }
        } catch (Exception e) {

        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }
    }

    /**
     * 获取一个键值对的失效时间
     */
    public static Long GetTimeOfKey(String Key) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            Long seconds = shardedJedis.ttl(Key);
            //shardedPool.returnResourceObject( shardedJedis );
            return seconds;
        } catch (Exception e) {
            return 0L;
        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }
    }

    /**
     * 取消一个键值对的失效时间
     */
    public static void CanleTimeOfKey(String Key) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            shardedJedis.persist(Key);
        } catch (Exception e) {

        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }

        //shardedPool.returnResourceObject( shardedJedis );
    }

    /**
     * 设置一个键值的失效时间
     *
     * @param Key
     * @param Time
     * @return
     */
    public static Long SetTimeOfKey(String Key, int Time) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            Long seconds = shardedJedis.expire(Key, Time);
            //shardedPool.returnResourceObject( shardedJedis );
            return seconds;
        } catch (Exception e) {
            return 0L;
        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }
    }

    public static Long SetTimeOfKey(byte[] Key, int Time) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            Long seconds = shardedJedis.expire(Key, Time);
            //shardedPool.returnResourceObject( shardedJedis );
            return seconds;
        } catch (Exception e) {
            return 0L;
        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }
    }

    public static String MemCachSetMap(String Key, Map<String, String> map) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            shardedJedis.hmset(Key, map);
            return Config.SERVICE_SUCCESS;
        } catch (Exception e) {
            return Config.SERVICE_FAILED;
        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }

        //shardedPool.returnResourceObject( shardedJedis );

    }

    public static String MemCachSetMap(String Key, int time, Map<String, String> map) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            shardedJedis.expire(Key, time);
            shardedJedis.hmset(Key, map);
            //shardedPool.returnResourceObject( shardedJedis );
            return Config.SERVICE_SUCCESS;
        } catch (Exception e) {
            return Config.SERVICE_FAILED;
        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }
    }

    public static String SetMemCachMapByMapKey(String Key, String MapKey, String MapValue) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            Map<String, String> map = GetMemCachMap(Key);
            map.put(MapKey, MapValue);
            shardedJedis.hmset(Key, map);
            //shardedPool.returnResourceObject( shardedJedis );
            return Config.SERVICE_SUCCESS;
        } catch (Exception e) {
            return Config.SERVICE_FAILED;
        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }
    }

    public static Map<String, String> GetMemCachMap(String Key) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            Map<String, String> map = shardedJedis.hgetAll(Key);
            //shardedPool.returnResourceObject( shardedJedis );
            return map;
        } catch (Exception e) {
            return null;
        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }

    }

    public static String GetMemCachMapByMapKey(String Key, String MapKey) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            String map = shardedJedis.hget(Key, MapKey);
            //shardedPool.returnResourceObject( shardedJedis );
            return map;
        } catch (Exception e) {
            return null;
        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }

    }

    public static List GetMemCachValuesByMapKey(String Key, String... MapKey) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = redisService.getShareJedisPoolConnection();
            return shardedJedis.hmget(Key, MapKey);
        } catch (Exception e) {
            return null;
        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }
    }

    /**
     * Redis队列服务
     */


    /**
     * 存储到redis队列中，插入到表头
     *
     * @param key
     * @param value
     */
    public static void lpush(byte[] key, byte[] value) {
        Jedis shardedJedis = redisService.getShareJedisPoolConnection();
        try {
            shardedJedis.lpush(key, value);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            shardedJedis.close();
        }
    }

    /**
     * 存储到redis队列中，插入到表尾
     *
     * @param key
     * @param value
     */
    public static void rpush(byte[] key, byte[] value) {
        Jedis shardedJedis = redisService.getShareJedisPoolConnection();
        try {
            shardedJedis.rpush(key, value);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            shardedJedis.close();
        }
    }

    public static byte[] lpop(byte[] key) {
        Jedis shardedJedis = redisService.getShareJedisPoolConnection();
        try {
            return shardedJedis.lpop(key);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            shardedJedis.close();
        }

        return null;
    }

    public static byte[] rpop(byte[] key) {
        Jedis shardedJedis = redisService.getShareJedisPoolConnection();
        try {
            return shardedJedis.rpop(key);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            shardedJedis.close();
        }

        return null;
    }

    public static List<byte[]> lrang(byte[] key, int startIndex, int endIndex) {
        Jedis shardedJedis = redisService.getShareJedisPoolConnection();
        try {
            return shardedJedis.lrange(key, startIndex, endIndex);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            shardedJedis.close();
        }

        return null;
    }

    public static List<String> lrang(String key, int startIndex, int endIndex) {
        Jedis shardedJedis = redisService.getShareJedisPoolConnection();
        try {
            return shardedJedis.lrange(key, startIndex, endIndex);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            shardedJedis.close();
        }

        return null;
    }

    public static long getLen(byte[] key) {
        Jedis shardedJedis = redisService.getShareJedisPoolConnection();
        try {
            return shardedJedis.llen(key);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            shardedJedis.close();
        }

        return 0L;
    }

    public static List<byte[]> getRedisList(byte[] key) {
        Jedis shardedJedis = redisService.getShareJedisPoolConnection();
        try {
            return shardedJedis.lrange(key, 0, -1);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            shardedJedis.close();
        }

        return null;
    }

    public static boolean isExistUpdate(final String... param) {
        Jedis shardedJedis = redisService.getShareJedisPoolConnection();
        try {
            String key = param[0];
            int expire = 20;
            if (param.length > 1) {
                expire = Integer.parseInt(param[1]);
            }
            long status = shardedJedis.setnx("redis_lock_" + key, "true");
            if (status > 0) {
                shardedJedis.expire("redis_lock_" + key, expire);
            }

            return status <= 0 ? false : true;
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            shardedJedis.close();
        }

        return false;

    }

    public static Long unLockRedisKey(final String key) {
        Jedis shardedJedis = redisService.getShareJedisPoolConnection();
        try {
            return shardedJedis.del("redis_lock_" + key);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            shardedJedis.close();
        }

        return -1L;
    }

/*    *//**
     * 模糊查找
     *//*
    public static Set<String> getKeys(String Keys) {
        Jedis shardedJedis = redisService.getShareJedisPoolConnection();
        try {
            return shardedJedis.(Keys);
        } catch (Throwable e) {
            return null;
        } finally {
            shardedJedis.close();
        }
    }*/

}
