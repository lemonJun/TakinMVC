package learn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

class Copy {

    // 传过来的参数为文件名和目标地址
    public Copy(String from, String to) {
        copy(from, to);
    }

    public Copy(File from, File to) {
        copy(from, to);
    }

    // 参数 from文件复制到to文件中
    public static void copy(File from, File to) {
        if (from.isDirectory()) {
            if (!to.exists())
                to.mkdir();
            String[] s = from.list();
            for (int i = 0; i < s.length; i++) {
                File f = new File(from.getAbsoluteFile() + "/" + s[i]);
                File t = new File(to.getAbsoluteFile() + "/" + s[i]);
                copy(f, t);
            }
        } else if (from.isFile()) {
            copyFile(from, to);
        }
    }

    //
    public static void copy(String from, String to) {
        File file = new File(from);
        // System.out.println("原文件名："+file.getName());
        File newFile = new File(to + "/" + file.getName());
        if (file.isDirectory()) {
            newFile.mkdir();
            String[] s = file.list();
            for (int i = 0; i < s.length; i++) {
                // System.out.println("新文件名："+newFile.getName());
                copy(file.getAbsolutePath() + "/" + s[i], newFile.getAbsolutePath());
            }

        } else if (file.isFile()) {
            copyFile(file, newFile);
        }

    }

    // 复制文件
    public static void copyFile(File from, File to) {

        if (to.isFile())
            return;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        byte[] t = new byte[256];
        try {
            fis = new FileInputStream(from);
            fos = new FileOutputStream(to);
            int c;
            int i = 0;
            while ((c = fis.read(t)) != -1) {
                fos.write(c);
                // System.out.println("复制文件中:"+i++);
            }
        } catch (Exception e) {
            System.err.println("FileStreamsTest: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                fis.close();
                fos.close();
            } catch (IOException e) {
                System.err.println("FileStreamsTest: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

}