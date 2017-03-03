var zipdir = require('zip-dir');
var pjson = require('./package.json');
var fs = require('fs');
var path = require('path');

var dir_build = path.resolve('builds');

zipdir(dir_build, { saveTo: `${dir_build}/${pjson.name}.zip` }, (err, buffer) => {
  if(err) {
    console.log(`failed to zip ${dir_build} folder`)
  }else {
    console.log(`Created ${pjson.name}.zip`)
  }
});

var pom = '<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"\n' +
'    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">\n' +
'    <parent>\n' +
'        <groupId>com.mcmcg.alm</groupId>\n' +
'        <artifactId>websuperpom</artifactId>\n' +
'        <version>0.0.1-SNAPSHOT</version>\n' +
'    </parent>\n' +
'    <modelVersion>4.0.0</modelVersion>\n' +
'    <groupId>com.mcmcg.dia</groupId>\n' +
'    <artifactId>' + pjson.name + '</artifactId>\n' +
'    <packaging>pom</packaging>\n' +
'    <version>' + pjson.version + '</version>\n' +
'    <description>' + pjson.description + '</description>\n' +
'    <build>\n' +
'        <plugins>\n' +
'            <plugin>\n' +
'                <groupId>org.codehaus.mojo</groupId>\n' +
'                <artifactId>build-helper-maven-plugin</artifactId>\n' +
'                <version>1.8</version>\n' +
'                <executions>\n' +
'                    <execution>\n' +
'                        <id>attach-artifacts</id>\n' +
'                        <phase>package</phase>\n' +
'                        <goals>\n' +
'                            <goal>attach-artifact</goal>\n' +
'                        </goals>\n' +
'                        <configuration>\n' +
'                            <artifacts>\n' +
'                                <artifact>\n' +
'                                    <file>builds/' + pjson.name + '.zip</file>\n' +
'                                    <type>zip</type>\n' +
'                                </artifact>\n' +
'                            </artifacts>\n' +
'                        </configuration>\n' +
'                    </execution>\n' +
'                </executions>\n' +
'            </plugin>\n' +
'            <plugin>\n' +
'                <groupId>org.apache.maven.plugins</groupId>\n' +
'                <artifactId>maven-dependency-plugin</artifactId>\n' +
'                <version>2.10</version>\n' +
'                <configuration>\n' +
'                    <artifactItems>\n' +
'                        <artifactItem>\n' +
'                            <groupId>${grp}</groupId>\n' +
'                            <artifactId>${art}</artifactId>\n' +
'                            <version>${ver}</version>\n' +
'                            <type>${type}</type>\n' +
'                            <classifier>${classifier}</classifier>\n' +
'                            <outputDirectory>${project.basedir}</outputDirectory>\n' +
'                            <destFileName>${destFileName}</destFileName>\n' +
'                        </artifactItem>\n' +
'                    </artifactItems>\n' +
'                </configuration>\n' +
'            </plugin>\n' +
'        </plugins>\n' +
'    </build>\n' +
'</project>';
fs.writeFile('pom.xml', pom, function(err) {
    if(err) {
        return console.log(err);
    }
    console.log("POM created!");
});
