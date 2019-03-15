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
<code>
&lt;dependency&gt;
    &lt;groupId&gt;[위의 -DgroupId]&lt;/groupId&gt;
    &lt;artifactId&gt;[위의 -DartifactId]&lt;/artifactId&gt;
    &lt;version&gt;[위의 -Dversion]&lt;/version&gt;
&lt;/dependency&gt;
</code>
</pre>

## 보통 Test 시 main에다가 코드 입력하여 콘솔 찍지만, 테스트 검증의 자동화를 위하여 따로 Test 프레임워크(JUnit)를 사용하여 테스트 수행
> 콘솔로 찍어서 검증하려면, 사람이 일일이 확인해야 하고, 실행하기 번거롭다(테스트할 메소드가 여러 개라면).
>> 테스트 시 가능하면 작은 단위로 수행할 것.