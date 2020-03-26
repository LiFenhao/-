package cn.piesat.datacatalogservice.utils;


import ch.ethz.ssh2.StreamGobbler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipOutputStream;

import static org.apache.commons.io.IOUtils.closeQuietly;

/**
 * @author ww
 * @date 2019/6/12 9:17
 * @description:
 */
public class FileUtil {

    private static final Logger logger = LogManager.getLogger(FileUtil.class);
    private static final String DEFAULT_CHART = "UTF-8";
    private static final int buffer = 2048;

    private static final String TIF_SUFFIX = ".tif";

    private static final String TIFF_SUFFIX = ".tiff";

    private static final String ZIP_SUFFIX = ".zip";

    /**
     * 解压到指定目录
     *
     * @param zipPath
     * @param descDir
     * @author isea533
     */
    @Deprecated
    public static Boolean unZipFiles(String zipPath, String descDir) {
        return unZipFiles(new File(zipPath), descDir);
    }

    /**
     * 解压文件到指定目录
     *
     * @param zipFile
     * @param descDir
     * @author isea533
     */
    @SuppressWarnings("rawtypes")
    public static Boolean unZipFiles(File zipFile, String descDir) {
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        try {
            ZipFile zip = new ZipFile(zipFile);
            for (Enumeration entries = zip.getEntries(); entries.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();
                InputStream in = zip.getInputStream(entry);
                String outPath = (descDir + zipEntryName).replaceAll("\\*", "/");
                outPath = new String(outPath.getBytes("utf-8"), "ISO8859-1");
                ;
                // 判断路径是否存在,不存在则创建文件路径
                File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
                if (!file.exists()) {
                    file.mkdirs();
                }
                // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if (new File(outPath).isDirectory()) {
                    continue;
                }
                OutputStream out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[1024];
                int len;
                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
                }
                in.close();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 递归压缩文件
     *
     * @param source   源路径,可以是文件,也可以目录
     * @param destinct 目标路径,压缩文件名
     * @throws IOException
     */
    public static void compress(String source, String destinct)
            throws IOException {
        List fileList = loadFilename(new File(source));
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
                new File(destinct)));

        byte[] buffere = new byte[8192];
        int length;
        BufferedInputStream bis;

        for (int i = 0; i < fileList.size(); i++) {
            File file = (File) fileList.get(i);
            zos.putNextEntry(new ZipEntry(getEntryName(source, file)));
            bis = new BufferedInputStream(new FileInputStream(file));

            while (true) {
                length = bis.read(buffere);
                if (length == -1)
                    break;
                zos.write(buffere, 0, length);
            }
            bis.close();
            zos.closeEntry();
        }
        zos.close();
    }

    /**
     * 递归获得该文件下所有文件名(不包括目录名)
     *
     * @param file
     * @return
     */
    public static List loadFilename(File file) {
        List filenameList = new ArrayList();
        if (file.isFile()) {
            filenameList.add(file);
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                filenameList.addAll(loadFilename(f));
            }
        }
        return filenameList;
    }

    /**
     * 获取文件的前缀
     *
     * @param file
     * @return
     */
    public static String getFilePrefix(File file) {
        return file.getName().substring(0, file.getName().lastIndexOf("."));
    }

    /**
     * 获取文件后缀
     *
     * @return
     */
    public static String getFileSuffix(File file) {
        return file.getName().substring(file.getName().lastIndexOf(".") + 1);
    }

    /**
     * 获得zip entry 字符串
     *
     * @param base
     * @param file
     * @return
     */
    public static String getEntryName(String base, File file) {
        File baseFile = new File(base);
        String filename = file.getPath();
        // int index=filename.lastIndexOf(baseFile.getName());
        if (baseFile.getParentFile().getParentFile() == null)
            return filename.substring(baseFile.getParent().length());
        return filename.substring(baseFile.getParent().length() + 1);
    }


    /**
     * 实现文件拷贝
     *
     * @param
     * @param
     * @throws Exception
     */
    public static void copyFile(File srcFile, File target) throws Exception {

        if (!srcFile.exists()) {
            throw new Exception("文件不存在！");
        }
        if (!srcFile.isFile()) {
            throw new Exception("不是文件！");
        }
        //判断目标路径是否是目录
        if (!target.isDirectory()) {
            throw new Exception("文件路径不存在！");
        }

        // 获取源文件的文件名
        String fileName = srcFile.getName();
        //判断是否存在相同的文件名的文件
        File[] listFiles = target.listFiles();
        for (File file : listFiles) {
            if (fileName.equals(file.getName())) {
                fileName += "_1";
            }
        }
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(target);
            //从in中批量读取字节，放入到buf这个字节数组中，
            // 从第0个位置开始放，最多放buf.length个 返回的是读到的字节的个数
            byte[] buf = new byte[8 * 1024];
            int len = 0;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                System.out.println("关闭输入流错误！");
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                System.out.println("关闭输出流错误！");
            }
        }

    }

    /**
     * 将多个文件压缩成同个zip文件
     *
     * @param srcFiles
     * @param
     */
    public static boolean zipFiles(File[] srcFiles, String zipFileStr) {

        // 判断压缩后的文件存在不，不存在则创建

        File zipFile = new File(zipFileStr);
        if (!zipFile.exists()) {
            try {
                zipFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 创建 FileOutputStream 对象
        FileOutputStream fileOutputStream = null;
        // 创建 ZipOutputStream
        ZipOutputStream zipOutputStream = null;
        // 创建 FileInputStream 对象
        FileInputStream fileInputStream = null;

        try {
            // 实例化 FileOutputStream 对象
            fileOutputStream = new FileOutputStream(zipFile);
            // 实例化 ZipOutputStream 对象
            zipOutputStream = new ZipOutputStream(fileOutputStream);
            // 创建 ZipEntry 对象
            ZipEntry zipEntry = null;
            // 遍历源文件数组
            for (int i = 0; i < srcFiles.length; i++) {
                // 将源文件数组中的当前文件读入 FileInputStream 流中
                fileInputStream = new FileInputStream(srcFiles[i]);
                // 实例化 ZipEntry 对象，源文件数组中的当前文件
                zipEntry = new ZipEntry(srcFiles[i].getName());
                zipOutputStream.putNextEntry(zipEntry);
                // 该变量记录每次真正读的字节个数
                int len;
                // 定义每次读取的字节数组
                byte[] buffer = new byte[1024 * 1024];
                while ((len = fileInputStream.read(buffer)) != -1) {
                    zipOutputStream.write(buffer, 0, len);
                }
            }
            zipOutputStream.closeEntry();
            zipOutputStream.close();
            fileInputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取目录的大小
     *
     * @return
     */
    public static Double getFileSize(File f) {
        Double size = 0.0;
        File[] files = f.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                size = size + getFileSize(files[i]) / 1024 / 1024;
            } else {
                size = size + files[i].length() / 1024 / 1024;
            }
        }
        return size;
    }

    /**
     * 删除目录下所有文件
     */
    public static void delFiles(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        //递归删除文件夹下所有的文件
        String[] list = file.list();
        File temp = null;
        String path = null;
        for (String item : list) {
            path = filePath + File.separator + item;
            temp = new File(path);
            if (temp.isFile()) {
                temp.delete();
                continue;
            }
            if (temp.isDirectory()) {
                delFiles(path);
                new File(path).delete();
                continue;
            }
        }
        //删除最外层文件夹：
        file.delete();
    }


    /**
     * 获取文件目录下的压缩文件:zip或tar或gz文件
     *
     * @return
     */
    public static List<File> getCompressFile(File file) {
        File[] files = file.listFiles();
        List<File> lists = new ArrayList<>();
        for (File f : files) {
            String name = f.getName();
            if (f.isFile()) {
                if (name.endsWith(".zip") || name.endsWith(".tar") || name.endsWith(".gz")) {
                    lists.add(f);
                }
            }
        }
        return lists;
    }

    /**
     * 判断文件是否是压缩文件
     *
     * @param file
     * @return
     */
    public static Boolean isCompressFile(File file) {
        Boolean flag = false;
        if (file.isFile()) {
            String fileName = file.getName();
            if (fileName.endsWith(".zip") || fileName.endsWith(".tar") || fileName.endsWith(".gz")) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 解压缩tar.gz文件
     *
     * @param file       压缩包文件
     * @param targetPath 目标文件夹
     * @param delete     解压后是否删除原压缩包文件
     */
    public static boolean decompressTarGz(File file, String targetPath, boolean delete) {
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        GZIPInputStream gzipIn = null;
        TarInputStream tarIn = null;
        OutputStream out = null;
        try {
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            gzipIn = new GZIPInputStream(bufferedInputStream);
            tarIn = new TarInputStream(gzipIn, 1024 * 2);

            // 创建输出目录
            createDirectory(targetPath, null);

            TarEntry entry = null;
            while ((entry = tarIn.getNextEntry()) != null) {
                if (entry.isDirectory()) { // 是目录
                    createDirectory(targetPath, entry.getName()); // 创建子目录
                } else { // 是文件
                    File tempFIle = new File(targetPath + File.separator + entry.getName());
                    createDirectory(tempFIle.getParent() + File.separator, null);
                    System.out.println(tempFIle.getName() + "    :" + tempFIle.getAbsolutePath());

                    out = new FileOutputStream(tempFIle);
                    int len = 0;
                    byte[] b = new byte[2048];

                    while ((len = tarIn.read(b)) != -1) {
                        out.write(b, 0, len);
                    }
                    out.flush();
                    out.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (tarIn != null) {
                    tarIn.close();
                }
                if (gzipIn != null) {
                    gzipIn.close();
                }
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

  /*  public static String getMd5Of(File file) throws IOException {
        return  DigestUtils.md5DigestAsHex(new FileInputStream(file));
    }

    public static void main(String[] args) {
        try {
            String md5Of = getMd5Of(new File("D:\\batchTest2\\GF22.zip"));
            System.out.println(md5Of);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


    /*  public static Boolean unZip(String zipFileName, String descFileName) {

          String descFileNames = descFileName;
          if (!descFileNames.endsWith(File.separator)) {
              descFileNames = descFileNames + File.separator;
          }
         *//* File fil = new File(zipFileName);
        try {
            String s = DigestUtils.md5DigestAsHex(new FileInputStream(fil));
            System.out.println(fil.getName()+"   MD5  "+s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("");*//*
        try {
            ZipFile zipFile = new ZipFile(zipFileName);
            ZipEntry entry = null;
            String entryName = null;
            String descFileDir = null;
            byte[] buf = new byte[4096];
            int readByte = 0;
            @SuppressWarnings("rawtypes")
            Enumeration enums = zipFile.getEntries();
            while (enums.hasMoreElements()) {
                entry = (ZipEntry) enums.nextElement();
                entryName = entry.getName();
                descFileDir = descFileNames + entryName;
                if (entry.isDirectory()) {
                    new File(descFileDir).mkdir();
                    continue;
                } else {
                    new File(descFileDir).getParentFile().mkdir();
                }
                File file = new File(descFileDir);
                OutputStream os = new FileOutputStream(file);
                InputStream is = zipFile.getInputStream(entry);
                while ((readByte = is.read(buf)) != -1) {
                    os.write(buf, 0, readByte);
                }
                String md5 = DigestUtils.md5DigestAsHex(new FileInputStream(file));
                System.out.println("文件："+file.getName()+"  MD5值为： "+md5);
                os.close();
                is.close();
            }
            zipFile.close();
            System.out.println("文件解压成功!");

        } catch (Exception e) {
            System.out.println("文件解压失败!");
            e.printStackTrace();
            return false;
        }
        return true;
    }*/
    public static boolean unZip(String fileAddress, String unZipAddress) {
        //去目录下寻找文件
        File file = new File(fileAddress);
        file.setReadOnly();
        org.apache.tools.zip.ZipFile zipFile = null;
        try {
            zipFile = new org.apache.tools.zip.ZipFile(file, "GBK");//设置编码格式
            Enumeration e = zipFile.getEntries();
            while (e.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) e.nextElement();
                if (zipEntry.isDirectory()) {
                    String name = zipEntry.getName();
                    name = name.substring(0, name.length() - 1);
                    File f = new File(unZipAddress + name);
                    f.mkdirs();
                } else {
                    File f = new File(unZipAddress + zipEntry.getName());
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                    InputStream is = zipFile.getInputStream(zipEntry);
                    FileOutputStream fos = new FileOutputStream(f);
                    int length = 0;
                    byte[] b = new byte[1024];

                    while ((length = is.read(b, 0, 1024)) != -1) {
                        fos.write(b, 0, length);
                    }
                    is.close();
                    fos.close();
                }
            }
            if (zipFile != null) {
                zipFile.close();
            }
            file.deleteOnExit();//解压完以后将压缩包删除
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }

        return true;

    }

    public static Boolean unTar(File file, String outputDir) {
        TarInputStream tarIn = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            tarIn = new TarInputStream(fileInputStream);
            createDirectory(outputDir, null);// 创建输出目录
            TarEntry entry = null;
            while ((entry = tarIn.getNextEntry()) != null && !"".equals(entry.getName())) {
                if (entry.isDirectory()) {// 是目录
                    createDirectory(outputDir, entry.getName());// 创建空目录
                } else {// 是文件
                    byte[] tmp = new byte[entry.getName().length()];
                    for (int i = 0; i < entry.getName().length(); i++) {
                        char c = entry.getName().charAt(i);
                        byte b = (byte) (0xFF & c);
                        tmp[i] = b;
                    }
                    String fName = new String(tmp, "ISO8859-1");
                    File tmpFile = new File(outputDir + File.separator + fName);
                    createDirectory(tmpFile.getParent() + File.separator, null);// 创建输出目录
                    OutputStream out = new FileOutputStream(tmpFile);
                    tarIn.copyEntryContents(out);
                    closeQuietly(out);

                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        } finally {
            closeQuietly(fileInputStream);
            closeQuietly(tarIn);
        }
        return true;
    }

    public static void createDirectory(String outputDir, String subDir) {
        File file = new File(outputDir);
        if (!(subDir == null || subDir.trim().equals(""))) {//子目录不为空  
            file = new File(outputDir + File.separator + subDir);
        }
        if (!file.exists()) {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            file.mkdirs();
        }
    }

    public static boolean deCompressFile(String filePath) {
        boolean result = false;

        try {
            File srcfile = new File(filePath);
            List<File> compressFiles = getCompressFile(srcfile);
            if (!CollectionUtils.isEmpty(compressFiles)) {
                //如果是压缩文件,先将压缩文件进行解压
                for (File compressFile : compressFiles) {
                    String compressPath = compressFile.getPath();
                    String fileSuffix = getFileSuffix(compressFile);
                    //1.解压文件：
                    if ("tar".equalsIgnoreCase(fileSuffix)) {
                        result = unTar(compressFile, filePath);
                    } else if ("zip".equalsIgnoreCase(fileSuffix)) {
                        result = unZip(compressPath, filePath);
                    } else if ("gz".equalsIgnoreCase(fileSuffix)) {
                        String str = new File(compressPath).getName();
                        String[] split = str.split("\\.tar");
                        if (ArrayUtils.isNotEmpty(split)) {
                            String fileName = split[0];
                            if (!StringUtils.isEmpty(fileName)) {
                                filePath = filePath + File.separator + fileName;
                            }
                        }
                        result = unTarGz(compressPath, filePath);
                    }
                    if (result) {
                        compressFile.delete();
                    }
                }
            }
            result = true;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    /**
     * 判断目录下的文件是否都是（普通文件）或（普通文件和目录）
     *
     * @param parentFile
     * @return
     */
    public static boolean isOrdinaryFileOrDir(File parentFile) {
        if (!subFilesAllIsDirectory(parentFile) && subFilesIsOrdinaryFile(parentFile)) {
            return true;
        }
        return false;
    }

    /**
     * 目录下的子文件如果是文件一定是普通文件
     *
     * @param parentFile
     * @return
     */
    public static boolean subFilesIsOrdinaryFile(File parentFile) {
        //1.获取目录下的所有文件：
        File[] files = parentFile.listFiles();
        //2.循环判断
        if (!ObjectUtils.isEmpty(files)) {
            for (File file : files) {
                if (file.isFile() && isCompressFile(file)) {
                    return false;
                }
            }

        }
        return true;
    }

    /**
     * 目录下的所有子文件都是目录：
     *
     * @param parentFile
     * @return
     */
    public static boolean subFilesAllIsDirectory(File parentFile) {
        //1.获取目录下的所有文件：
        File[] files = parentFile.listFiles();
        //2.循环判断：
        if (!ObjectUtils.isEmpty(files)) {
            for (File file : files) {
                if (file.isFile()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 对压缩文件进行解压并返回解压缩后的文件
     *
     * @param filePath
     * @return
     */
    public static String unCompress(String filePath, String descPath) {
        //1.解压后的文件绝对路径：
        String unCompressFilePath = "";
        //2.解压结果：
        Boolean result = false;
        try {
            //3.原始压缩文件
            File srcFile = new File(filePath);
            String fileSuffix = getFileSuffix(srcFile);
            //4.解压文件：
            if ("tar".equalsIgnoreCase(fileSuffix)) {
                result = unTar(srcFile, descPath);
                if (result) {
                    unCompressFilePath = descPath + File.separator + srcFile.getName().split("\\.t")[0];
                }
            } else if ("zip".equalsIgnoreCase(fileSuffix)) {
                // descPath = descPath + File.separator + srcFile.getName().split("\\.z")[0];
                //创建目标文件路径：
                if (!new File(descPath).exists()) {
                    new File(descPath).mkdir();
                }
                //判断命令执行结果：
                Integer integer = commandUnZip(descPath, filePath);
                if (integer == 0) {
                    unCompressFilePath = descPath;
                }
            } else if ("gz".equalsIgnoreCase(fileSuffix)) {
                String str = srcFile.getName();
                String[] split = str.split("\\.tar");
                if (ArrayUtils.isNotEmpty(split)) {
                    String fileName = split[0];
                    if (!StringUtils.isEmpty(fileName)) {
                        descPath = descPath + File.separator + fileName;
                        if (!new File(descPath).exists()) {
                            new File(descPath).mkdir();
                        }
                    }
                }
                Integer integer = commandUnTarGZ(descPath, filePath);
                if (integer == 0) {
                    unCompressFilePath = descPath;
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return unCompressFilePath;
    }

    /**
     * 检验目录结构，如果不合规格，应该修复
     *
     * @param beforeFilePath
     */
    /**
     * 检验目录结构，如果不合规格，应该修复
     *
     * @param beforeFilePath
     */
    public static String repairDirectoryStructure(String beforeFilePath) {

        String afterFilePath = beforeFilePath;
        File beforeFile = new File(beforeFilePath);
        if (beforeFile.exists()) {
            File[] files = beforeFile.listFiles();
            if (!ObjectUtils.isEmpty(files)) {
                if (files.length == 1) {
                    //判断该目录下为目录还是文件
                    File[] fileArray = files[0].listFiles();
                    if (!ObjectUtils.isEmpty(fileArray) && fileArray.length == 1) {
                        //若是目录（三级），将三级目录copy到二级目录中，返回二级目录路径
                        try {
                            //files[0] :二级目录 fileArray[0]:三级目录
                            FileUtils.copyDirectory(fileArray[0], files[0]);
                            FileUtils.deleteDirectory(fileArray[0]);
                            afterFilePath = files[0].getPath();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //若是文件,不做处理，返回二级路径
                        afterFilePath = files[0].getPath();
                    }

                }
            }
        }
        return afterFilePath;

    }

    /*public static void main(String[] args) {
        System.out.println(isOrdinaryFileOrDir(new File("C:\\栅格影像原始数据\\fufeng - 副本 (2)")));
    }*/

    /**
     * Unzip the tar.gz file
     *
     * @param
     * @param outputDir
     * @throws IOException
     */
    public static boolean unTarGz(String srcPath, String outputDir) {


        TarInputStream tarIn = null;
        try {
            tarIn = new TarInputStream(new GZIPInputStream(
                    new BufferedInputStream(new FileInputStream(srcPath))),
                    1024 * 2);

            createDirectory(outputDir, null);//创建输出目录

            TarEntry entry = null;
            while ((entry = tarIn.getNextEntry()) != null) {

                if (entry.isDirectory()) {//是目录
                    entry.getName();
                    createDirectory(outputDir, entry.getName());//创建空目录
                } else {//是文件
                    File tmpFile = new File(outputDir + "/" + entry.getName());
                    createDirectory(tmpFile.getParent() + "/", null);//创建输出目录
                    OutputStream out = null;
                    try {
                        out = new FileOutputStream(tmpFile);
                        int length = 0;

                        byte[] b = new byte[2048];

                        while ((length = tarIn.read(b)) != -1) {
                            out.write(b, 0, length);
                        }

                    } catch (IOException ex) {
                        throw ex;
                    } finally {
                        if (out != null) out.close();
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
            return false;
        } finally {
            try {
                if (tarIn != null) {
                    tarIn.close();
                }
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
        return true;
    }

    /**
     * 命令解压zip文件
     *
     * @param descPath
     * @param srcPath
     * @return
     */
    public static Integer commandUnZip(String descPath, String srcPath) {
        //unzip -o -o /home/sunny myfile.zip
        List<String> commandParams = new ArrayList<String>();
        commandParams.add("unzip");
        commandParams.add("-o");
        commandParams.add("-d");
        //目标路径：
        commandParams.add(descPath);
        //目标文件：
        commandParams.add(srcPath);
        logger.info("命令:" + commandParams.toString());
        ProcessBuilder builder = new ProcessBuilder(commandParams);
        Integer exitCode = null;
        try {
            Process process = builder.start();
            InputStream is = process.getInputStream();
            logger.info("获取输入流对象结果：   "+is);
            String result = processExecutionResult(is, DEFAULT_CHART);
            logger.info("命令执行结果：   "+ result);
            exitCode = process.waitFor();
            logger.info("exitCode:" + exitCode);
            if (exitCode != 0) {
                logger.info("脚本出错");
                // resultMap.put("result", false);
                //  resultMap.put("message", "切片失败");
                // return resultMap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return exitCode;
    }


    public static Integer commandUnTarGZ(String descPath, String srcPath) {
        //ar -zxvf /home/zdzlibs.tar.gz /home/zdz/java/zdzlibs/

        List<String> commandParams = new ArrayList<String>();
        commandParams.add("tar");
        commandParams.add("-zxvf");
        //原文件路径：
        commandParams.add(srcPath);
        commandParams.add("-C");
        //目标路径：
        commandParams.add(descPath);

        logger.info("命令:" + commandParams.toString());
        ProcessBuilder builder = new ProcessBuilder(commandParams);
        Integer exitCode = null;
        try {
            Process process = builder.start();
            InputStream is = process.getInputStream();
            processExecutionResult(is, DEFAULT_CHART);

            exitCode = process.waitFor();
            //   logger.info("exitCode:" + exitCode);
            if (exitCode != 0) {
                logger.info("脚本出错");

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return exitCode;
    }

    /**
     * 解析脚本执行返回的结果集
     *
     * @param in      输入流对象
     * @param charset 编码
     * @return 以纯文本的格式返回
     * @since V0.1
     */
    private static String processExecutionResult(InputStream in, String charset) {
        InputStream inputStream = new StreamGobbler(in);
        StringBuffer buffer = new StringBuffer();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charset));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                logger.info("command out:" + line);
                buffer.append(line + "\n");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            bufferedReader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    /**
     * 获取文件数据大小
     * @param size
     * @return
     */
    public static String getDataSize(long size) {
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "MB";
        } else {
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "GB";
        }
    }

    /**
     * 按行读取txt文件
     * @param file
     * @return
     */
    public static Map<String,List<Object>> readTxtFile(File file) {

        Map<String,List<Object>> batchData = new LinkedHashMap<>();
        //判断文件存在并且是文件
        if (file.exists() && file.isFile()) {
            BufferedReader bufferedReader = null;
            try {
                //构造一个BufferedReader类来读取文件
                bufferedReader = new BufferedReader(new FileReader(file));
                String lineTxt = null;
                //按使用readLine方法，一次读一行
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    //替换制表符：
                    String strReplaceTab = lineTxt.replaceAll("\\t", " ");
                    //替换多个连续的空格：
                    String strReplaceSpace = strReplaceTab.replaceAll("\\s{2,}", " ");
                    String[] split = strReplaceSpace.split(" ");
                    List<Object> strList = new ArrayList<>();
                    for (int i = 0; i < split.length; i++) {
                        strList.add(split[i]);
                    }
                    batchData.put(split[0],strList);
                }
            } catch (Exception e) {
                System.out.println("读取文件内容出错");
                e.printStackTrace();
            } finally {
                try {
                    bufferedReader.close();
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
            }
        } else {
            System.out.println("找不到指定的文件");
        }
        return batchData;
    }

    
    public static String progressPercent(int y, int z) { 
        String percent = "";// 接受百分比的值 
        double top = y * 1.0; 
        double bottom = z * 1.0; 
        double reslut = top / bottom;     
        DecimalFormat df1 = new DecimalFormat("0.00%");
       // ##.00%  // 百分比格式，后面不足2位的用0补齐  
        percent = df1.format(reslut); 
        return percent;
    
   } 
    
    public static double fileSizeConver(String str) {
        double size = 0.0;

        if(StringUtils.isEmpty(str)){
            return size;
        }
        String substring = StringUtils.substring(str, 0, str.length() - 2);

        if(StringUtils.endsWith(str,"B")){
            size = Double.parseDouble(substring);
        }
        
        if(StringUtils.endsWith(str,"KB")){
            size = Double.parseDouble(substring)*1024;
        }
        
        if(StringUtils.endsWith(str,"MB")){
            size = Double.parseDouble(substring)*1024*1024;
        }

        if(StringUtils.endsWith(str,"GB")){
            size = Double.parseDouble(substring)*1024*1024*1024;

        }

        if(StringUtils.endsWith(str,"TB")){
            size = Double.parseDouble(substring)*1024*1024*1024*1024;
        }
        if(StringUtils.endsWith(str,"PB")){
            size = Double.parseDouble(substring)*1024*1024*1024*1024*1024;
        }

        return size;
    }

    public static String fileSizeCalculate1(Double fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "";
        if(fileS == 0){
            return wrongSize;
        }
        if (fileS < 1024){
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576){
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        }else if (fileS < 1073741824){
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
            
        }
        return fileSizeString;
    }
    
    /**
     * 
     * @param d 转换得字节
     * @param si 是否需要单位
     * @return
     */
    public static String byteFormat(double d) {
    	 if (d < 1024) {
            return String.valueOf(d) + "B";
         }
        String[] units = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        int unit = 1024;
        int exp = (int) (Math.log(d) / Math.log(unit));
        double pre = 0;
        if (d > 1024) {
            pre = d / Math.pow(unit, exp);
        } else {
            pre = (double) d / (double) unit;
        }
        /*if (si) {           
           
        }*/
        return String.format(Locale.ENGLISH, "%.2f%s", pre, units[(int) exp]);
        
       // return String.format(Locale.ENGLISH, "%.2f", pre);
    }
    
}
