# meta-maven-plugin

Generate and attach an additional file in several formats containing structured information.

TODO: Implement.

Example configuration:

```xml
  <plugin>
      <groupId>com.backbase.oss</groupId>
      <artifactId>meta-maven-plugin</artifactId>
      <executions>
          <execution>
              <id>generate-meta</id>
              <configuration>
                  <meta>
                      <images>
                          <image>
                              <registry>experimental</registry>
                              <image>${project.artifactId}</image>
                              <tag>${project.version}</tag>
                          </image>
                          <image>
                              <registry>staging</registry>
                              <image>${project.artifactId}</image>
                              <tag>${project.version}-special</tag>
                              <suffix>special</suffix>
                              <extra-info>Some extra information - fyi.</extra-info>
                          </image>
                      </images>
                  </meta>
              </configuration>
          </execution>
      </executions>
  </plugin>
```
