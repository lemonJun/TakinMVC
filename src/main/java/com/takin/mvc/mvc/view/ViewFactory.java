package com.takin.mvc.mvc.view;

import com.google.inject.ImplementedBy;
import com.takin.mvc.mvc.ActionResult;
import com.takin.mvc.mvc.internal.VelocityViewFactory;

/**
 * @author lemon
 */
@ImplementedBy(VelocityViewFactory.class)
public interface ViewFactory {
    ActionResult view(String viewName);
}
