package lemon.mvc.util;

import lemon.mvc.mvc.BeatContext;

public class ResponseStatus {

    public static void setStatus206(BeatContext beat) {

        beat.getResponse().setStatus(206);

    }
}
