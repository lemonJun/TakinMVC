package lemon.mvc.mvc.view;

import com.google.inject.ImplementedBy;

import lemon.mvc.mvc.ActionResult;
import lemon.mvc.mvc.internal.VelocityViewFactory;

/**
 * @author lemon
 */
@ImplementedBy(VelocityViewFactory.class)
public interface ViewFactory {
    ActionResult view(String viewName);
}
