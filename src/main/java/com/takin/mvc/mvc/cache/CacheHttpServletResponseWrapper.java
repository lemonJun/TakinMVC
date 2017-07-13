package com.takin.mvc.mvc.cache;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CacheServletResponse is a serialized representation of a response
 *
 * @author  <a href="mailto:sergek@lokitech.com">Serge Knystautas</a>
 * @version $Revision: 469 $
 */
public class CacheHttpServletResponseWrapper extends HttpServletResponseWrapper {

    private static final Logger log = LoggerFactory.getLogger(CacheHttpServletResponseWrapper.class);

    private String cacheKey = null;

    /**
     * We cache the printWriter so we can maintain a single instance
     * of it no matter how many times it is requested.
     */
    private PrintWriter cachedWriter = null;
    private ResponseContent result = null;
    private SplitServletOutputStream cacheOut = null;
    private boolean fragment = false;
    private int status = SC_OK;
    private long expires = HttpCache.EXPIRES_ON;
    private long lastModified = HttpCache.LAST_MODIFIED_INITIAL;
    private long cacheControl = -60;

    /**
     * Constructor
     *
     * @param response The servlet response
     */
    public CacheHttpServletResponseWrapper(HttpServletResponse response) {
        this(response, false, Long.MAX_VALUE, HttpCache.EXPIRES_ON, HttpCache.LAST_MODIFIED_INITIAL, -60);
    }

    /**
     * Constructor
     *
     * @param response The servlet response
     * @param fragment true if the repsonse indicates that it is a fragement of a page
     * @param time the refresh time in millis
     * @param lastModified defines if last modified header will be send, @see HttpCache
     * @param expires defines if expires header will be send, @see HttpCache
     * @param cacheControl defines if cache control header will be send, @see HttpCache
     */
    public CacheHttpServletResponseWrapper(HttpServletResponse response, boolean fragment, long time, long lastModified, long expires, long cacheControl) {
        super(response);
        result = new ResponseContent();
        this.fragment = fragment;
        this.expires = expires;
        this.lastModified = lastModified;
        this.cacheControl = cacheControl;

        // only set inital values for last modified and expires, when a complete page is cached
        if (!fragment) {
            // setting a default last modified value based on object creation and remove the millis
            if (lastModified == HttpCache.LAST_MODIFIED_INITIAL) {
                long current = System.currentTimeMillis();
                current = current - (current % 1000);
                result.setLastModified(current);
                super.setDateHeader(HttpCache.HEADER_LAST_MODIFIED, result.getLastModified());
            }
            // setting the expires value
            if (expires == HttpCache.EXPIRES_TIME) {
                result.setExpires(result.getLastModified() + time);
                super.setDateHeader(HttpCache.HEADER_EXPIRES, result.getExpires());
            }
            // setting the cache control with max-age 
            if (this.cacheControl == HttpCache.MAX_AGE_TIME) {
                // set the count down
                long maxAge = System.currentTimeMillis();
                maxAge = maxAge - (maxAge % 1000) + time;
                result.setMaxAge(maxAge);
                super.addHeader(HttpCache.HEADER_CACHE_CONTROL, "max-age=" + time / 1000);
            } else if (this.cacheControl != HttpCache.MAX_AGE_NO_INIT) {
                result.setMaxAge(this.cacheControl);
                super.addHeader(HttpCache.HEADER_CACHE_CONTROL, "max-age=" + (-this.cacheControl));
            } else if (this.cacheControl == HttpCache.MAX_AGE_NO_INIT) {
                result.setMaxAge(this.cacheControl);
            }
        }
    }

    /**
     * Get a response content
     *
     * @return The content
     */
    public ResponseContent getContent() {
        // Flush the buffer
        try {
            flush();
        } catch (IOException ignore) {
        }

        // Create the byte array
        result.commit();

        // Return the result from this response
        return result;
    }

    /**
     * Set the content type
     *
     * @param value The content type
     */
    @Override
    public void setContentType(String value) {
        if (log.isDebugEnabled()) {
            log.debug("ContentType: " + value);
        }

        super.setContentType(value);
        result.setContentType(value);
    }

    /**
     * Set the date of a header
     *
     * @param name The header name
     * @param value The date
     */
    @Override
    public void setDateHeader(String name, long value) {
        if (log.isDebugEnabled()) {
            log.debug("dateheader: " + name + ": " + value);
        }

        // only set the last modified value, if a complete page is cached
        if ((lastModified != HttpCache.LAST_MODIFIED_OFF) && (HttpCache.HEADER_LAST_MODIFIED.equalsIgnoreCase(name)) && !fragment) {
            result.setLastModified(value);
        }

        // implement RFC 2616 14.21 Expires (without max-age)
        if ((expires != HttpCache.EXPIRES_OFF) && (HttpCache.HEADER_EXPIRES.equalsIgnoreCase(name)) && !fragment) {
            result.setExpires(value);
        }

        super.setDateHeader(name, value);
    }

    /**
     * @return the cacheKey
     */
    public String getCacheKey() {
        return cacheKey;
    }

    /**
     * @param cacheKey the cacheKey to set
     */
    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    /**
     * Add the date of a header
     *
     * @param name The header name
     * @param value The date
     */
    @Override
    public void addDateHeader(String name, long value) {
        if (log.isDebugEnabled()) {
            log.debug("dateheader: " + name + ": " + value);
        }

        // only set the last modified value, if a complete page is cached
        if ((lastModified != HttpCache.LAST_MODIFIED_OFF) && (HttpCache.HEADER_LAST_MODIFIED.equalsIgnoreCase(name)) && !fragment) {
            result.setLastModified(value);
        }

        // implement RFC 2616 14.21 Expires (without max-age)
        if ((expires != HttpCache.EXPIRES_OFF) && (HttpCache.HEADER_EXPIRES.equalsIgnoreCase(name)) && !fragment) {
            result.setExpires(value);
        }

        super.addDateHeader(name, value);
    }

    /**
     * Set a header field
     *
     * @param name The header name
     * @param value The header value
     */
    @Override
    public void setHeader(String name, String value) {
        if (log.isDebugEnabled()) {
            log.debug("header: " + name + ": " + value);
        }

        if (HttpCache.HEADER_CONTENT_TYPE.equalsIgnoreCase(name)) {
            result.setContentType(value);
        }

        if (HttpCache.HEADER_CONTENT_ENCODING.equalsIgnoreCase(name)) {
            result.setContentEncoding(value);
        }

        super.setHeader(name, value);
    }

    /**
     * Add a header field
     *
     * @param name The header name
     * @param value The header value
     */
    @Override
    public void addHeader(String name, String value) {
        if (log.isDebugEnabled()) {
            log.debug("header: " + name + ": " + value);
        }

        if (HttpCache.HEADER_CONTENT_TYPE.equalsIgnoreCase(name)) {
            result.setContentType(value);
        }

        if (HttpCache.HEADER_CONTENT_ENCODING.equalsIgnoreCase(name)) {
            result.setContentEncoding(value);
        }

        super.addHeader(name, value);
    }

    /**
     * Set the int value of the header
     *
     * @param name The header name
     * @param value The int value
     */
    @Override
    public void setIntHeader(String name, int value) {
        if (log.isDebugEnabled()) {
            log.debug("intheader: " + name + ": " + value);
        }

        super.setIntHeader(name, value);
    }

    /**
     * We override this so we can catch the response status. Only
     * responses with a status of 200 (<code>SC_OK</code>) will
     * be cached.
     */
    @Override
    public void setStatus(int status) {
        super.setStatus(status);
        this.status = status;
    }

    /**
     * We override this so we can catch the response status. Only
     * responses with a status of 200 (<code>SC_OK</code>) will
     * be cached.
     */
    @Override
    public void sendError(int status, String string) throws IOException {
        super.sendError(status, string);
        this.status = status;
    }

    /**
     * We override this so we can catch the response status. Only
     * responses with a status of 200 (<code>SC_OK</code>) will
     * be cached.
     */
    @Override
    public void sendError(int status) throws IOException {
        super.sendError(status);
        this.status = status;
    }

    /**
     * We override this so we can catch the response status. Only
     * responses with a status of 200 (<code>SC_OK</code>) will
     * be cached.
     */
    @Override
    @Deprecated
    public void setStatus(int status, String string) {
        super.setStatus(status, string);
        this.status = status;
    }

    /**
     * We override this so we can catch the response status. Only
     * responses with a status of 200 (<code>SC_OK</code>) will
     * be cached.
     */
    @Override
    public void sendRedirect(String location) throws IOException {
        this.status = SC_MOVED_TEMPORARILY;
        super.sendRedirect(location);
    }

    /**
     * Retrieves the captured HttpResponse status.
     */
    @Override
    public int getStatus() {
        return status;
    }

    /**
     * Set the locale
     *
     * @param value The locale
     */
    @Override
    public void setLocale(Locale value) {
        super.setLocale(value);
        result.setLocale(value);
    }

    /**
     * Get an output stream
     *
     * @throws IOException
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        // Pass this faked servlet output stream that captures what is sent
        if (cacheOut == null) {
            cacheOut = new SplitServletOutputStream(result.getOutputStream(), super.getOutputStream());
        }

        return cacheOut;
    }

    /**
     * Get a print writer
     *
     * @throws IOException
     */
    @Override
    public PrintWriter getWriter() throws IOException {
        if (cachedWriter == null) {
            String encoding = getCharacterEncoding();
            if (encoding != null) {
                cachedWriter = new PrintWriter(new OutputStreamWriter(getOutputStream(), encoding));
            } else { // using the default character encoding
                cachedWriter = new PrintWriter(new OutputStreamWriter(getOutputStream()));
            }
        }

        return cachedWriter;
    }

    /**
     * Flushes all streams.
     * @throws IOException
     */
    private void flush() throws IOException {
        if (cacheOut != null) {
            cacheOut.flush();
        }

        if (cachedWriter != null) {
            cachedWriter.flush();
        }
    }

    @Override
    public void flushBuffer() throws IOException {
        super.flushBuffer();
        flush();
    }

}