package demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lemon.mvc.mvc.ActionResult;
import lemon.mvc.mvc.annotation.Controller;
import lemon.mvc.mvc.annotation.Path;

@Path("")
@Controller
public class DemonController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(DemonController.class);

    @Path("/index")
    public ActionResult index() {
        long start = System.currentTimeMillis();
        System.out.println("system start");
        logger.info("start=" + start);
        //        fibonacci(46);
        logger.info(System.currentTimeMillis() - start + "");

        return ActionResult.view("index");
    }

    @Path("/main/{id:\\d+}")
    public ActionResult main(long id) {
        logger.info("" + id);
        return ActionResult.view("main");
    }

    @Path("/bean")
    public ActionResult bean(DemonBean bean) {
        logger.info("" + bean.getId());
        return ActionResult.view("main");
    }

    public static int fibonacci(int n) {
        if (n <= 2) {
            return 1;
        } else {
            return fibonacci(n - 1) + fibonacci(n - 2);
        }
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        fibonacci(46);
        System.out.println(System.currentTimeMillis() - start);
    }
}
