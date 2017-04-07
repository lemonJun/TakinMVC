package com.takin.mvc.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.takin.mvc.mvc.inject.GuiceDI;
import com.takin.mvc.util.Converter;

public class WFHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public WFHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);

    }
    public String getOriginalParameter(String name) {

        return super.getParameter(name);
    }

    public String[] getOriginalParameterValues(String name) {

        return super.getParameterValues(name);
    }

    @Override
    public String getParameter(String name) {
        if (super.isAsyncStarted()) {
            String source = super.getParameter(name);
            return Converter.convert(source);
        }

        BeatContext beat = GuiceDI.getInstance(Dispatcher.class).currentBeatContext();
        String source = super.getParameter(name);

        String[] pwv = (String[]) beat.getModel().get("__PARAMSWITHOUTVALIDATE");
        if (source == null)
            return null;

        if (pwv == null)
            return Converter.convert(source);

        for (String s : pwv) {
            if (name.equals(s))
                return source;
        }

        return Converter.convert(source);
    }

    @Override
    public String[] getParameterValues(String name) { 
        String[] ss = super.getParameterValues(name);

        if (ss == null)
            return null;
        BeatContext beat = GuiceDI.getInstance(Dispatcher.class).currentBeatContext();
        String[] pwvs = (String[]) beat.getModel().get("__PARAMSWITHOUTVALIDATE");

        for (int i = 0; i < ss.length; i++) {
            String s = ss[i];
            if (s == null) {
                continue;
            }

            if (pwvs == null) {
                ss[i] = Converter.convert(s);
                continue;
            }

            boolean needConvert = true;

            for (String pwv : pwvs) {
                if (name.equals(pwv)) {
                    needConvert = false;
                    break;
                }
            }
            if (needConvert)
                ss[i] = Converter.convert(s);
        }
        return ss;

    }
}
