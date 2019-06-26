/**
 * @(#)Hello.java, 2018-10-14.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * SaveFile
 *
 * @author lirongqian
 * @since 2018/10/14
 */
@Mojo(name = "save", defaultPhase = LifecyclePhase.PACKAGE)
public class SaveFile extends AbstractMojo {

    private static Logger log = Logger.getLogger("SaveFile");

    /** 扫描路径 **/
    @Parameter(property = "path")
    private String path;

    /** 指定路径下包含的文件 **/
    @Parameter(property = "includeFile")
    private String includeFile;

    /** 指定路径下需要排除的文件 **/
    @Parameter(property = "excludeFile")
    private String excludeFile;

    private List<String> includeList = new ArrayList<>();

    private List<String> excludeList = new ArrayList<>();

    private static final Map<String, String> PATH_MAP = new HashMap<>();

    @Override
    public void execute() {
        try {
            List<File> fileList = new ArrayList<>();
            String fileName = System.getProperty("user.dir") + "/src/main/java/" + path.replaceAll("\\.", "/");
            File file = new File(fileName);
            if (StringUtils.isNotEmpty(includeFile)) {
                String[] includeSplit = includeFile.split(",");
                includeList.addAll(Arrays.asList(includeSplit));
            }
            if (StringUtils.isNotEmpty(excludeFile)) {
                String[] includeSplit = excludeFile.split(",");
                excludeList.addAll(Arrays.asList(includeSplit));
            }
            getFile(file, fileList);
            file2String(fileList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFile(File file, List<File> fileList) {
        if (file.exists()) {
            if (file.isFile()) {
                // 获取去掉后缀的文件名
                String name = file.getName().split("\\.")[0];
                // 排除掉不需要的文件
                if (!excludeList.contains(name)) {
                    if (!includeList.isEmpty()) {
                        if (includeList.contains(name)) {
                            fileList.add(file);
                        }
                    } else {
                        fileList.add(file);
                    }
                }
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File single : files) {
                        getFile(single, fileList);
                    }
                }
            }
        }
    }

    private void file2String(List<File> fileList) {
        StringBuilder sb = new StringBuilder();
        pathMapper(fileList);
        sb.append(JSONObject.toJSONString(PATH_MAP)).append(",,,");
        for (File file : fileList) {
            String fileName = file.getName();
            String name = fileName.substring(0, fileName.indexOf("."));
            sb.append(name).append("~~");
            // 读取单个文件
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")))) {
                String temp;
                StringBuilder curSb = new StringBuilder();
                while ((temp = reader.readLine()) != null) {
                    curSb.append(temp);
                }
                sb.append(matching(curSb, name));
                // 每个文件以@@@分割
                sb.append("@@@");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String fileName = System.getProperty("user.dir").replaceAll("\\.", "/") + "/src/main/resources/easydoc.txt";
        File file = new File(fileName);
        // 文件存在时，先删除
        if (file.exists()) {
            file.delete();
        }
        // 直接使用FileWriter默认使用（ISO-8859-1 or US-ASCII）西方编码，中文会乱码
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), Charset.forName("UTF-8")))) {
            // 退出时存储消息
            writer.write(sb.toString());
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private StringBuilder matching(StringBuilder str, String name) {
        String regex = "(?<!:)\\/\\/.*|\\/\\*(\\s|.)*?\\*\\/";
        Matcher matcher = RegularExpressionUtils.createMatcherWithTimeout(str.toString(), regex, 200);
        StringBuilder sb = new StringBuilder();
        try {
            while (matcher.find()) {
                String temp = matcher
                        .group()
                        .replaceAll("\\/\\/[^\n]*", "")
                        .replaceAll("\\/\\*\\*", "")
                        .replaceAll("\\*\\/", "")
                        .replaceAll("\\*", "")
                        .replaceAll(" +", " ");
                // 每次匹配以~~分割
                sb.append(temp).append("~~");
            }
        } catch (Exception e) {
            log.warning("easydoc matcher error, name=" + name + " info=" + e.getMessage());
        }
        return sb;
    }

    /**
     * 生成pathMapper映射
     */
    private void pathMapper(List<File> fileList) {
        // !!! 插件用lambda报错
        for (File file : fileList) {
            NamePack namePack = path2Pack(file.getPath());
            PATH_MAP.put(namePack.getName(), namePack.getPackPath());
        }
    }

    /**
     * 将文件路径生成 文件名:包路径 的映射
     */
    private NamePack path2Pack(String filePath) {
        String temp = filePath.replaceAll("/", ".");
        String packPath = temp.substring(temp.indexOf(path));
        packPath = packPath.substring(0, packPath.lastIndexOf("."));
        return new NamePack(packPath.substring(packPath.lastIndexOf(".") + 1), packPath);
    }
}