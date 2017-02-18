package lemon.mvc.mvc.context;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lemon.mvc.mvc.ActionAttribute;
import lemon.mvc.mvc.BeatContext;
import lemon.mvc.mvc.Dispatcher;
import lemon.mvc.mvc.bind.BeatBindResults;
import lemon.mvc.mvc.client.ClientContext;
import lemon.mvc.mvc.inject.GuiceDI;
import lemon.mvc.mvc.server.ServerContext;

public class BeatContextBean implements BeatContext {

    @Override
    public Model getModel() {

        return GuiceDI.getInstance(Dispatcher.class).currentBeatContext().getModel();
    }

    @Override
    public HttpServletRequest getRequest() {

        return GuiceDI.getInstance(Dispatcher.class).currentBeatContext().getRequest();
    }

    @Override
    public HttpServletResponse getResponse() {

        return GuiceDI.getInstance(Dispatcher.class).currentBeatContext().getResponse();
    }

    @Override
    public ServletContext getServletContext() {

        return GuiceDI.getInstance(Dispatcher.class).currentBeatContext().getServletContext();
    }

    @Override
    public ClientContext getClient() {

        return GuiceDI.getInstance(Dispatcher.class).currentBeatContext().getClient();
    }

    @Override
    public ServerContext getServer() {

        return GuiceDI.getInstance(Dispatcher.class).currentBeatContext().getServer();
    }

    @Override
    public ActionAttribute getAction() {

        return GuiceDI.getInstance(Dispatcher.class).currentBeatContext().getAction();

    }

    @Override
    public String getRelativeUrl() {

        return GuiceDI.getInstance(Dispatcher.class).currentBeatContext().getRelativeUrl();
    }

    @Override
    public BeatBindResults getBindResults() {

        return GuiceDI.getInstance(Dispatcher.class).currentBeatContext().getBindResults();
    }

}
