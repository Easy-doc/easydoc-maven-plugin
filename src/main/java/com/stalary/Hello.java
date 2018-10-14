/**
 * @(#)Hello.java, 2018-10-14.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Hello
 *
 * @author lirongqian
 * @since 2018/10/14
 */
@Mojo(name = "save", defaultPhase= LifecyclePhase.PACKAGE)
public class Hello extends AbstractMojo {

    @Parameter
    private String path;

    @Override
    public void execute() {
        System.out.println("hello" + path);
    }
}