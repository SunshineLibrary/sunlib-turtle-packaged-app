package org.sunshinelibrary.turtle.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * User: fxp
 * Date: 10/16/13
 * Time: 4:40 PM
 */
public class ZipUtils {

    static byte[] buffer = new byte[4096];

    public static void unzip(File zipfile, File outputfolder) throws Exception {
        ZipFile zip = new ZipFile(zipfile);

        Enumeration entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            File unzipped = new File(outputfolder, entry.getName());

            if (entry.isDirectory() && !unzipped.exists()) {
                unzipped.mkdirs();
                continue;
            } else if (!unzipped.getParentFile().exists()) {
                unzipped.getParentFile().mkdirs();
            }

            InputStream in = zip.getInputStream(entry);
            FileOutputStream fos = new FileOutputStream(unzipped);

            int count;
            while ((count = in.read(buffer, 0, buffer.length)) != -1) {
                fos.write(buffer, 0, count);
            }

            fos.close();
            in.close();
        }
    }
//
//    public static void zip(File[] infiles, JarOutputStream jos) throws Exception
//    {
//        zip(infiles,"",jos);
//
//        // clean up
//        jos.flush();
//        jos.close();
//    }
//
//    public static void zip(File[] infiles, String basefolder, JarOutputStream jos) throws Exception
//    {
//        for(int i=0; i<infiles.length; i++)
//        {
//            if(infiles[i].isDirectory())
//            {
//                // recursive call for directories
//                zip(infiles[i].listFiles(), infiles[i].getName() + File.separator, jos);
//                continue;
//            }
//
//            String filepath = basefolder + infiles[i].getName();
//            JarEntry entry = new JarEntry(filepath);
//            jos.putNextEntry(entry);
//
//            FileInputStream fis = new FileInputStream(infiles[i]); // get stream
//
//            int count;
//            while((count = fis.read(buffer, 0, buffer.length)) != -1)
//                jos.write(buffer, 0, count);
//        }
//    }

}
