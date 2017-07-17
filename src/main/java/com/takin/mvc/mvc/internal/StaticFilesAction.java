package com.takin.mvc.mvc.internal;

import java.io.File;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.takin.mvc.mvc.ActionResult;
import com.takin.mvc.mvc.InitHelper;
import com.takin.mvc.mvc.inject.MVCSystem;
import com.takin.mvc.mvc.internal.actionresult.StaticActionResult;
import com.takin.mvc.util.Pair;
import com.takin.mvc.util.TouchTimer;

/**
 * 对静态文件处理，把所有静态文件名保存在set中，如何精确匹配，表明当前请求就是静态文件
 *
 * 这是对是静动态分离准备的吧
 */
@Singleton
public class StaticFilesAction implements Action {

    /**
     * 静态文件名set
     */
    private Set<String> staticFiles = Sets.newHashSet();

    /**
     * 不允许访问的文件或文件夹
     */
    //    private final Set<String> forbitPath = ImmutableSet.of("/WEB-INF");
    private final Set<String> forbitPath = ImmutableSet.of("");

    /**
     * 定时获取静态文件更新，但不需要另外的定时线程
     */
    private final TouchTimer timer;

    private final StaticActionResult.Factory staticFactory;

    @Inject
    public StaticFilesAction(InitHelper god, ServletContext servletContext, StaticActionResult.Factory staticFactory, @MVCSystem
    final Executor executor) {

        this.staticFactory = staticFactory;
        //        String viewFolder = viewFolderPath(god);
        //        final File staticResourcesFolder = new File(viewFolder);
        String resourceFolder = servletContext.getRealPath("/") + "/resources";
        final File staticResourcesFolder = new File(resourceFolder);

        Runnable findFiles = new Runnable() {
            @Override
            public void run() {
                staticFiles = findFiles(staticResourcesFolder, staticFiles.size(), forbitPath);
            }
        };

        timer = TouchTimer.build(60 * 1000, findFiles, executor);

        timer.immediateRun();
    }

    @Override
    public int order() {
        return 100;
    }

    @Override
    public RouteResult matchAndInvoke(RouteBag bag) {
        return RouteResult.invoked(match(bag));
    }

    @Override
    public String path() {
        return "File Path : /resources/*";
    }

    private ActionResult match(RouteBag bag) {

        String simplyPath = bag.getSimplyPath();

        if (!exist(simplyPath))
            return ActionResult.NULL;

        return staticFactory.create(simplyPath);

    }

    private boolean exist(String url) {
        timer.touch();
        return staticFiles.contains(url);
    }

    Set<String> findFiles(File directory, int cap, Set<String> forbitPath) {

        Set<String> staticFiles = new HashSet<String>(cap);

        Deque<Pair<File, String>> dirs = Lists.newLinkedList();

        dirs.add(Pair.build(directory, "/"));

        while (dirs.size() > 0) {
            Pair<File, String> pop = dirs.pop();

            File[] files = pop.getKey().listFiles();

            if (files == null)
                continue;

            for (File file : files) {
                String name = pop.getValue() + file.getName();

                if (forbitPath.contains(name))
                    continue;

                if (file.isDirectory()) {
                    dirs.push(Pair.build(file, name + '/'));
                    continue;
                }

                staticFiles.add(name);
            }
        }

        return staticFiles;
    }

    private String viewFolderPath(InitHelper argo) {
        File parent = argo.currentFolder();
        return new File(parent, "views").getAbsolutePath();
    }
}
