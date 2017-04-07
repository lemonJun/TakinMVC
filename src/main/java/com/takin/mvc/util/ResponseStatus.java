package com.takin.mvc.util;

import com.takin.mvc.mvc.BeatContext;

public class ResponseStatus {

    public static void setStatus206(BeatContext beat) {

        beat.getResponse().setStatus(206);

    }
}
