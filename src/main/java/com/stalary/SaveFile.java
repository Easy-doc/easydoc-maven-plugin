/**
 * @(#)Hello.java, 2018-10-14.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary;

import com.alibaba.fastjson.JSONObject;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SaveFile
 *
 * @author lirongqian
 * @since 2018/10/14
 */
@Mojo(name = "save", defaultPhase = LifecyclePhase.PACKAGE)
public class SaveFile extends AbstractMojo {

    /** 扫描路径 **/
    @Parameter(property = "path")
    private String path;

    private static final Map<String, String> PATH_MAP = new HashMap<>();

    @Override
    public void execute() {
        try {
            List<File> fileList = new ArrayList<>();
            String fileName = System.getProperty("user.dir") + "/src/main/java/" + path.replaceAll("\\.", "/");
            File file = new File(fileName);
            getFile(file, fileList);
            file2String(fileList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFile(File file, List<File> fileList) {
        if (file.exists()) {
            if (file.isFile()) {
                fileList.add(file);
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
                String name =  fileName.substring(0, fileName.indexOf("."));
                sb.append(name).append("~~");
                // 读取单个文件
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")))) {
                    String temp;
                    StringBuilder curSb = new StringBuilder();
                    while ((temp = reader.readLine()) != null) {
                        curSb.append(temp);
                    }
                    sb.append(matching(curSb));
                    // 每个文件以@@@分割
                    sb.append("@@@");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try (FileWriter writer = new FileWriter(System.getProperty("user.dir").replaceAll("\\.", "/") + "/src/main/resources/easydoc.txt", false)) {
                // 退出时存储消息
                writer.write(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private StringBuilder matching(StringBuilder str) {
        String regex = "(?<!:)\\/\\/.*|\\/\\*(\\s|.)*?\\*\\/";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        StringBuilder sb = new StringBuilder();
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