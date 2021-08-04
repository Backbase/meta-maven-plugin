# meta-maven-plugin

Generate and attach an additional file in several formats containing structured information.

---

## Goal "meta:meta"

Generate and optionally attach additional artefacts containing structured information.

### Available parameters

##### attach

Whether or not to attach the metadata as artefacts.

*User property*: `meta.attach`

*Default*: `true`

##### classifier

The classifier of the metadata artefact.

*User property*: `meta.classifier`

*Default*: `meta`

##### formats

Specifies the formats of the metadata; a format can be one of the following values added as a
`<format>` subelement:

- json
- xml
- yaml
- yml

*User property*: `meta.formats`

*Default*: `json`

#####  meta

Free-form configuration in XML format used to generate the metadata.

*Required*: `yes`

##### prettyArrays

Controls arrays detection and restructures the meta accordingly.

Usually, in the XML world, arrays are represented as nested elements of a base element, e.g.

```xml
<items>
    <item>value1</item>
    <item>value2</item>
</items>
```

By applying a raw translation to JSON, the sample above is transformed to

```json
"items": {
    "item": ["value1", "value2" ]
}
```

... which is not quite we want, i.e.

```json
{
    "items": ["value1", "value2" ]
}
```

*User property*: `meta.prettyArrays`

*Default*: `true`

##### skip

Skip the execution.

*User property*: `meta.skip`

##### skipPom

By default, the execution is skipped for POM packaging, but setting this flag to false will generate the meta artifact for a POM project too.

The developers that use this flag need to be aware that the configuration of a Maven plugin is propagated to child modules, therefore they may need to

* set `<inherited>` element of the plugin to `false`
* disable the plugin in the child modules
* add child specific execution/configuration

*User property*: `meta.skipPom`

*Default*: `true`

### Configuration Example

#### Simple Project

```xml
<plugin>
    <groupId>com.backbase.oss</groupId>
    <artefactId>meta-maven-plugin</artefactId>
    <configuration>
        <!-- default values -->
        <formats>
            <format>json</format>
        </formats>
        <classifier>meta</classifier>
        <attach>true</attach>
        <prettyArrays>true</prettyArrays>
        <skip>false</skip>
    </configuration>
    <executions>
        <execution>
            <goals>
              <goal>meta</goal>
            </goals>
            <configuration>
                <meta>
                    <images>
                        <image>
                            <registry>experimental</registry>
                            <image>${project.artefactId}</image>
                            <tag>${project.version}</tag>
                        </image>
                        <image>
                            <registry>staging</registry>
                            <image>${project.artefactId}</image>
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

#### Meta Inheritance

* *Parent POM*

```xml
<plugin>
    <groupId>com.backbase.oss</groupId>
    <artefactId>meta-maven-plugin</artefactId>
    <executions>
        <execution>
            <id>generate-meta</id>
            <goals>
              <goal>meta</goal>
            </goals>
            <configuration>
                <meta>
                    <images>
                        <image>
                            <registry>experimental</registry>
                            <image>${project.artefactId}</image>
                            <tag>${project.version}</tag>
                        </image>
                    </images>
                </meta>
            </configuration>
        </execution>
    </executions>
</plugin>
```

* *Service POM*

```xml
<plugin>
    <groupId>com.backbase.oss</groupId>
    <artefactId>meta-maven-plugin</artefactId>
    <executions>
        <execution>
            <id>generate-meta</id>
            <configuration>
                <meta>
                    <images combine.children="append">
                        <image>
                            <registry>staging</registry>
                            <image>${project.artefactId}</image>
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
