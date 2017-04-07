package com.takin.mvc.mvc.cache;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.takin.mvc.mvc.Dispatcher;
import com.takin.mvc.mvc.annotation.OutputCache;
import com.takin.mvc.mvc.inject.GuiceDI;
import com.takin.mvc.spring.AnnotationUtils;

public class PageCache {

    private static final Logger log = LoggerFactory.getLogger(PageCache.class);

    private static final long lastModified = HttpCache.LAST_MODIFIED_INITIAL; // defines if the last-modified-header will be sent - default is intial setting

    private static final long expires = HttpCache.EXPIRES_ON; // defines if the expires-header will be sent - default is on

    private static Map<Method, Integer> methodMap = new ConcurrentHashMap<Method, Integer>();

    public static ResponseContent getCacheResult() {

        if (PageCacheMemCacheTool.getCache() == null) {
            log.error("you are using pagecache, but there is no cache in used...");
            return null;
        }

        String cacheKey = getCacheKey();
        Object cacheObject = PageCacheMemCacheTool.getCache().get(cacheKey);

        if (cacheObject == null)
            return null;

        return (ResponseContent) cacheObject;

    }

    public static void setCacheResult(HttpServletResponse response) {

        CacheHttpServletResponseWrapper responseWrapper = (CacheHttpServletResponseWrapper) response;
        int duration = getDuration();

        responseWrapper.cacheKey = getCacheKey();

        if (!(responseWrapper.getStatus() == HttpServletResponse.SC_OK))
            return;

        if (PageCacheMemCacheTool.getCache() == null)
            return;

        try {
            responseWrapper.flushBuffer();
            // TODO : 设置过期时间
            Date expired = new Date((new Date().getTime() + duration * 1000L));

            PageCacheMemCacheTool.getCache().set(getCacheKey(), responseWrapper.getContent(), expired);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CacheHttpServletResponseWrapper cacheResponseWrapper() {

        int duration = getDuration();
        long cacheControl = 0 - duration;
        CacheHttpServletResponseWrapper responseWrapper = new CacheHttpServletResponseWrapper(GuiceDI.getInstance(Dispatcher.class).currentResponse(), false, duration * 1000L, lastModified, expires, cacheControl);

        return responseWrapper;
    }

    private static String getCacheKey() {

        HttpServletRequest request = GuiceDI.getInstance(Dispatcher.class).currentRequest();

        StringBuffer url = new StringBuffer();
        String scheme = request.getScheme();
        int port = request.getServerPort();
        if (port < 0)
            port = 80; // Work around java.net.URL bug

        url.append(scheme);
        url.append("://");
        url.append(request.getServerName());
        if ((scheme.equals("http") && (port != 80)) || (scheme.equals("https") && (port != 443))) {
            url.append(':');
            url.append(port);
        }
        url.append(request.getRequestURI());

        String queryString = request.getQueryString();

        if (queryString != null)
            url.append('?').append(queryString);

        return url.toString();
    }

    private static int getDuration() {

        int duration = -1;
        Method method = GuiceDI.getInstance(Dispatcher.class).currentBeatContext().getAction().getActionMethod();

        Integer intDuration = methodMap.get(method);

        duration = intDuration == null ? -1 : intDuration;
        if (duration == -1) {

            OutputCache oc = AnnotationUtils.findAnnotation(method, OutputCache.class);
            duration = oc.duration();
            if (duration < 0)
                duration = 60;
            methodMap.put(method, Integer.valueOf(duration));
        }

        return duration;
    }
}
