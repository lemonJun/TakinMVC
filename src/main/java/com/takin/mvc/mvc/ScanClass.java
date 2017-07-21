package com.takin.mvc.mvc;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.takin.emmet.string.StringUtil;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

public class ScanClass {

    private static final Logger logger = LoggerFactory.getLogger(ScanClass.class);

    private ScanClass() {

    }

    /**
     * 这个辅助类居然跟网上查到的一样的
     * 从包package中获取所有的Class
     *
     * @param pack 包名
     * @return
     */
    public static Set<Class<?>> getClasses(String pack) {
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        boolean recursive = true;
        String packageName = pack;
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                logger.info(String.format("scan package:%s", url.getPath()));
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    JarFile jar;
                    try {
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        // 去掉后面的".class" 获取真正的类名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            // 添加到classes
                                            String newClass = StringUtil.isNullOrEmpty(packageName) ? className : packageName + '.' + className;
                                            if (StringUtil.isNotNullOrEmpty(newClass)) {
                                                classes.add(Class.forName(newClass));
                                            }
                                        } catch (ClassNotFoundException e) {
                                            logger.error("class not found", e);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        logger.error("", e);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("", e);
        }

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        if (dirfiles == null || dirfiles.length < 1)
            return;
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                String newfile = StringUtil.isNullOrEmpty(packageName) ? file.getName() : packageName + "." + file.getName();
                findAndAddClassesInPackageByFile(newfile, file.getAbsolutePath(), recursive, classes);
            } else {
                String newClass = "";
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    //classes.add(Class.forName(packageName + '.' + className));
                    //经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                    newClass = StringUtil.isNullOrEmpty(packageName) ? className : packageName + "." + className;
                    if (StringUtil.isNotNullOrEmpty(newClass)) {
                        //                        logger.info("scan Controller:" + newClass);
                        classes.add(Thread.currentThread().getContextClassLoader().loadClass(newClass));
                    }
                } catch (ClassNotFoundException e) {
                    logger.info("scan Controller:" + newClass);
                }
            }
        }
    }

    /**
     * 得到方法参数名称数组
     * 由于java没有提供获得参数名称的api，利用了javassist来实现
     *
     * @return 参数名称数组
     */
    public static String[] getMethodParamNames(Class<?> clazz, Method method) {
        Class<?>[] paramTypes = method.getParameterTypes();

        try {
            ClassPool pool = ClassPool.getDefault();

            pool.insertClassPath(new ClassClassPath(clazz));

            CtClass cc = pool.get(clazz.getName());

            String[] paramTypeNames = new String[method.getParameterTypes().length];

            for (int i = 0; i < paramTypes.length; i++)
                paramTypeNames[i] = paramTypes[i].getName();

            CtMethod cm = cc.getDeclaredMethod(method.getName(), pool.get(paramTypeNames));

            // 使用javaassist的反射方法获取方法的参数名
            MethodInfo methodInfo = cm.getMethodInfo();

            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            if (attr == null) {
                throw new RuntimeException("class:" + clazz.getName() + ", have no LocalVariableTable, please use javac -g:{vars} to compile the source file");
            }

            int startIndex = getStartIndex(attr);
            String[] paramNames = new String[cm.getParameterTypes().length];
            int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;

            for (int i = 0; i < paramNames.length; i++)
                paramNames[i] = attr.variableName(startIndex + i + pos);

            return paramNames;

        } catch (NotFoundException e) {
            return new String[0];
        }
    }

    private static int getStartIndex(LocalVariableAttribute attr) {

        int startIndex = 0;
        for (int i = 0; i < attr.length(); i++) {
            if ("this".equals(attr.variableName(i))) {
                startIndex = i;
                break;
            }
        }
        return startIndex;
    }

}
