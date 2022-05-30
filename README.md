![logo](logo.png)
# easydoc-maven-plugin
easy-doc的maven插件，用于存储过滤后的源文件，文件会在resouce中生成

# 版本说明
## 1.0.0
- 第一个线上可用版本
- 可以选择解析指定文件以及跳过指定文件

# 引入依赖方法
使用maven引入
```xml
<build>
        <plugins>
            <plugin>
                <groupId>com.stalary</groupId>
                <artifactId>easydoc-maven-plugin</artifactId>
                <version>1.0.0</version>
                <!-- -Dpath可以替代 -->
                <!--<configuration>
                    <path>com.stalary.easydoc</path>
                    <excludePath>A,B</excludePath>
                    <includePath>C,D,F</includePath>
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



