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

<hr />

## Dao 리팩토링 필요(try ~ catch ~ finally의 사용으로 인해 소스코드가 너무 길어짐)
> 재사용되는 부분의 메소드 추출 리팩토링 : pstmt 생성 부분
>> 더 줄어들기는 하지만 소스의 양이 눈에 띄게 줄어들지는 않음
>> 재사용을 한다고는 생각되지 않음.

<hr />

> 템플릿 메소드 패턴 사용 : Connection 생성, 자원해제(try ~ catch ~ finally) 부분은 고정시키고(템플릿 메소드), 바뀌는 부분(PreparedStatement 생성)은 추상 메소드로 가지는 추상 클래스를 만들어서 이를 확장해나가기
>> Dao 안의 메소드 마다 클래스가 하나씩 생김. 
>> UserDao 안에 클래스로 생성해버림(인터페이스에 따르지 않음).

<hr />

> 전략 패턴 : PstmtStatement를 생성하는 전략을 따로 만들고, 각 메소드에서 이 구현된 전략대로 생성한 PreparedStatement를 사용.
>> 그래도 중복된다(자원 해제, executeUpdate).
>> 특정 구체 클래스를 사용한다.
>>> 하지만, 특정 구체 클래스가 아닌 인터페이스를 받고, 중복되는 부분을 캡슐화한다면 사용할 수 있다. : 템플릿 메소드에 인터페이스를 인자로 넘겨받아서 그 인터페이스의 메소드를 실행