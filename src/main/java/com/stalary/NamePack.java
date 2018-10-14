/**
 * @(#)NamePack.java, 2018-10-06.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary;

/**
 * NamePack
 *
 * @author lirongqian
 * @since 2018/10/06
 */
public class NamePack {

    /** 类名 **/
    private String name;

    /** 包路径 **/
    private String packPath;

    public NamePack(String name, String packPath) {
        this.name = name;
        this.packPath = packPath;
    }

    public String getName() {
        return name;
    }

    public String getPackPath() {
        return packPath;
    }
}