# toby_test
## 클래스패스의 파일 읽어오기
> Gradle에서는 
<pre>
  URL url = ClassLoader.getSystemClassLoader().getResource(fileName);
  File file = url.getFile();
</pre>

> Maven을 사용할 때는
<pre>
  ClassLoader classLoader = getClass().getClassLoader();
  File file = new File(classLoader.getResource(fileName).getFile());
</pre>

## Maven에서 OJDBC 연결하려고 하니 안떴을 때 해결 방법

1. oracle home에서 ojdbc7.jar 다운

2. mvn install:install-file -Dfile={ojdbc7.jar 저장 위치} -DgroupId=com.oracle -DartifactId=ojdbc7 -Dversion=12.1.0 -Dpackaging=jar

3. pom.xml dependency 추가
<pre>
    <dependency>
		    <groupId>[위의 -DgroupId]</groupId>
		    <artifactId>[위의 -DartifactId]</artifactId>
		    <version>[위의 -Dversion]</version>
		</dependency>
</pre>
