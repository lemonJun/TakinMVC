package com.takin.mvc.mvc.cache;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletResponse;

import com.takin.mvc.mvc.ActionResult;
import com.takin.mvc.mvc.BeatContext;
import com.takin.mvc.mvc.InitHelper;
import com.takin.mvc.mvc.annotation.OutputCache;
import com.takin.mvc.mvc.interceptor.ActionInterceptor;
import com.takin.mvc.mvc.interceptor.PostInterceptor;
import com.takin.mvc.util2.AnnotationUtils;

/**
 * 实现的一个方法拦截器
 * 
 * @author renjun
 * 
 */
public class OutputCacheInterceptor implements ActionInterceptor {

    @Override
    public ActionResult preExecute(BeatContext beat) {

        final ResponseContent respContent = PageCache.getCacheResult();

        long clientLastModified = beat.getRequest().getDateHeader(HttpCache.HEADER_IF_MODIFIED_SINCE); // will return -1 if no header...

        if (respContent != null) {

            // only reply with SC_NOT_MODIFIED
            // if the client has already the newest page and the response isn't a
            // fragment in a page
            if ((clientLastModified != -1) && (clientLastModified >= respContent.getLastModified())) {
                return new ActionResult() {
                    @Override
                    public void render(BeatContext beat) throws Exception {

                        beat.getResponse().setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    }

                };
            }

            return new ActionResult() {
                @Override
                public void render(BeatContext beat) throws Exception {
                    respContent.writeTo(beat.getResponse(), false, false);
                }
            };
        }

        beat.getModel().add("needcache", true);
        return null;
    }
}