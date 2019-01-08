![logo](logo.png)
# easydoc-maven-plugin
easy-doc的maven插件，用于存储过滤后的源文件，文件会在resouce中生成


# 引入依赖方法
使用maven引入
```xml
<build>
        <plugins>
            <plugin>
                <groupId>com.stalary</groupId>
                <artifactId>easydoc-maven-plugin</artifactId>
                <!-- -Dpath可以替代 -->
                <!--<configuration>
                    <path>com.stalary.easydoc</path>
                </configuration>-->
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>save</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
</build>
</project>
```

# 使用方法
- 在plugin中配置path，即为需要扫描的路径
- mvn install时使用-Dpath设置路径



