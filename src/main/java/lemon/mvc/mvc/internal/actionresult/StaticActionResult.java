package lemon.mvc.mvc.internal.actionresult;

import com.google.inject.ImplementedBy;

import lemon.mvc.mvc.ActionResult;
import lemon.mvc.mvc.BeatContext;
import lemon.mvc.mvc.exception.WFException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * 处理静态文件
 */
public class StaticActionResult {

    //	private final static String _VIEWSPATH = "/views";

    @ImplementedBy(DefaultFactory.class)
    public static interface Factory {
        ActionResult create(String simplyPath);
    }

    private static class DefaultFactory implements Factory {

        @Inject
        public DefaultFactory() {

        }

        @Override
        public ActionResult create(String simplyPath) {

            return new DefaultStaticResult(simplyPath);
        }
    }

    private static class DefaultStaticResult extends ActionResult {

        private final String simplyPath;

        public DefaultStaticResult(String simplyPath) {
            this.simplyPath = simplyPath;

        }

        @Override
        public void render(BeatContext beatContext) {

            HttpServletRequest request = beatContext.getRequest();
            HttpServletResponse response = beatContext.getResponse();

            try {
                // 交给web容器处理
                request.getRequestDispatcher("/resources" + simplyPath).forward(request, response);
            } catch (Throwable e) {
                throw WFException.newBuilder(e).addContextVariable("File", "/resources" + simplyPath).build();
            }

        }
    }

}
