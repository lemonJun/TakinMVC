package lemon.mvc.mvc.view;

import java.io.IOException;

import lemon.mvc.mvc.ActionResult;
import lemon.mvc.mvc.BeatContext;

public class RedirectResult extends ActionResult {
    public RedirectResult(String url) {
        this.url = url;
    }

    private String url;

    /**
     * @return the viewName
     */
    public String getUrl() {
        return url;
    }

    @Override
    public void render(BeatContext beat) {
        try {
            beat.getResponse().sendRedirect(url);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}