package lemon.mvc.mvc.inject;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;
import javax.servlet.ServletContext;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;

import lemon.mvc.mvc.ActionResult;
import lemon.mvc.mvc.BeatContext;
import lemon.mvc.mvc.init.InitHelper;
import lemon.mvc.mvc.internal.DefaultBeatContext;
import lemon.mvc.mvc.internal.StaticFilesAction;
import lemon.mvc.mvc.internal.actionresult.StatusCodeActionResult;
import lemon.mvc.mvc.route.Action;
import lemon.mvc.mvc.route.StaticActionAnnotation;
import lemon.mvc.mvc.server.BaseSessionHandler;
import lemon.mvc.mvc.server.SessionHandler;

/**
 * 框架级别的DI绑定
 */
public class WFModule extends AbstractModule {

    private final InitHelper wfGod;

    public WFModule(InitHelper wfGod) {
        this.wfGod = wfGod;
    }

    @Override
    protected void configure() {

        //        bind(ServletRequest.class).to(HttpServletRequest.class);
        //        bind(ServletResponse.class).to(HttpServletResponse.class);

        bind(BeatContext.class).annotatedWith(WFSystem.class).to(DefaultBeatContext.class);

        bind(ActionResult.class).annotatedWith(Names.named("HTTP_STATUS=404")).toInstance(StatusCodeActionResult.defaultSc404);

        bind(ActionResult.class).annotatedWith(Names.named("HTTP_STATUS=405")).toInstance(StatusCodeActionResult.defaultSc405);

        bind(Action.class).annotatedWith(StaticActionAnnotation.class).to(StaticFilesAction.class);

        //        bind(ClientContext.class).to(DefaultClientContext.class);
        //        bind(Model.class).to(DefaultModel.class);
        bind(SessionHandler.class).to(BaseSessionHandler.class);
    }

    //    @Provides
    //    private HttpServletRequest provideReuqest() {
    //        return wfGod.currentRequest();
    //    }
    //
    //    @Provides
    //    private HttpServletResponse provideResponse() {
    //        return wfGod.currentResponse();
    //    }

    @Provides
    @Singleton
    private InitHelper provideWfGod() {
        return wfGod;
    }

    @Provides
    @Singleton
    private ServletContext provideServletContext() {
        return wfGod.servletContext();
    }

    @Provides
    @WFSystem
    @Singleton
    private Executor provideExecutor() {
        return Executors.newCachedThreadPool();
    }

}
