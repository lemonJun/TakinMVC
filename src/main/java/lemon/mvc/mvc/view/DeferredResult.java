package lemon.mvc.mvc.view;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletResponse;

import lemon.mvc.mvc.ActionResult;
import lemon.mvc.mvc.BeatContext;
import lemon.mvc.mvc.internal.DefaultBeatContext;

/**
 * <p>TODO 
 * <p>TODO
 * @author 58zhanglx
 * @date   2014-1-10 上午3:38:26
 * @see 
 * @modified TODO
 */
public class DeferredResult<T> extends ActionResult implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);

    private T result;
    private AsyncContext context;
    private boolean timeout = false;
    private boolean finish = false;
    private AsyncHandler<T> asyncHandler;
    private BeatContext asyncBeat;

    public AsyncContext getContext() {
        return context;
    }

    public DeferredResult(BeatContext beat, final AsyncHandler<T> asyncHandler) {
        if (asyncHandler == null)
            throw new IllegalArgumentException("AsyncHandler can not be null !");

        final T timeoutResult = asyncHandler.getTimeoutResult();

        if (timeoutResult == null)
            throw new IllegalArgumentException("TimeoutResult in asyncHandler can not be null !");

        this.asyncBeat = new DefaultBeatContext(beat.getModel(), beat.getClient(), beat.getServletContext(), beat.getBindResults());
        this.asyncHandler = asyncHandler;
        this.context = beat.getRequest().startAsync();

        // tomcat 对超时的返回状态码为500，不友好。jsonp支持差，所以使用线程池，休眠30s返回超时结果，这样就是200状态码了，有更好的解决方案后修改
        this.context.setTimeout(40L * 1000L);
        scheduledExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                done(timeoutResult);
            }
        }, 30, TimeUnit.SECONDS);

        asyncResponseListener();
    }

    @Override
    public void render(BeatContext beat) throws Exception {

    }

    public void done(T result) {
        if (finish)
            return;

        this.setResult(result);
        context.complete();
    }

    public T getResult() {
        return result;
    }

    private void setResult(T result) {
        this.result = result;
    }

    private void addListener(AsyncListener listener) {
        this.context.addListener(listener);
    }

    public boolean isFinish() {
        return finish;
    }

    public BeatContext getBeat() {
        return asyncBeat;
    }

    @Override
    protected void finalize() throws Throwable {
        if (!this.finish)
            this.context.complete();

        super.finalize();
    }

    private void asyncResponseListener() {
        this.addListener(new AsyncListener() {

            @Override
            public void onTimeout(AsyncEvent event) throws IOException {
                timeout = true;
                finish = true;
                asyncHandler.doTimeout();
            }

            @Override
            public void onStartAsync(AsyncEvent event) throws IOException {
                asyncHandler.doStartAsync();
            }

            @Override
            public void onError(AsyncEvent event) throws IOException {
                asyncHandler.doError();
            }

            @Override
            public void onComplete(AsyncEvent event) throws IOException {
                finish = true;
                if (event.getSuppliedResponse() instanceof HttpServletResponse)
                    ((HttpServletResponse) event.getSuppliedResponse()).setStatus(HttpServletResponse.SC_OK);

                event.getSuppliedResponse().getWriter().print(timeout ? asyncHandler.getTimeoutResult().toString() : result.toString());
                asyncHandler.doComplete();
            }
        });
    }

    public interface AsyncHandler<T> {
        public T getTimeoutResult();

        public void doTimeout();

        public void doStartAsync();

        public void doError();

        public void doComplete();
    }
}
