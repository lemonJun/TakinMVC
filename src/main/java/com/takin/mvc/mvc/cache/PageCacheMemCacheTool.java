package com.takin.mvc.mvc.cache;

import java.io.File;

import com.bj58.sfft.caching.Memcache;
import com.takin.mvc.mvc.Env;

public class PageCacheMemCacheTool {

    private static Memcache mc = null;

    /**
     * 获得Cache对象
     * @return
     */
    public static Memcache getCache() {

        if (mc != null)
            return mc;

        String path = Env.getConfigFolder() + Env.getNamespace() + "/pagecache_memcache.xml";

        File cacheFile = new File(path);

        if (!cacheFile.exists())
            return null;

        synchronized (PageCacheMemCacheTool.class) {
            if (mc != null)
                return mc;
            mc = Memcache.GetMemcache(path);
            return mc;
        }
    }

}
