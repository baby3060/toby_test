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
>>> 전략 패턴을 사용하여 특정 구체를 사용하게 되면, 그 특정 구현 클래스에 의존하는 것이므로 좋지 않다. 여기서는 전략을 무명 클래스로 생성하여, 이 템플릿 메소드에 바로 넘기면 된다.
>> update 및 query를 수행하는 메소드의 경우 다른 Dao(UserDao)에서도 사용하므로, UserDao에만 있는 것은 좋지 않다. 따라서 이 메소드는 다른 클래스(JdbcContext : Context 인터페이스 구현)에서 생성하고, UserDao에서는 이 JdbcContext 클래스에 의존하여, 이 클래스의 메소드를 호출하면 된다.
>> 이제, 설정에서는 UserDao의 DataSource 의존은 필요 없고(완전히 수정하지 않았을 경우에 없애면 안 된다), JdbcContext에서 DataSource를 의존하고, 이 UserDao에서는 JdbcContext를 의존하면 된다.
>>> JdbcContext를 스프링 Bean으로 의존하지 않는 방법도 있다.
>> 설정 XML에서 jdbcContext 부분을 모두 빼주고, UserDao에서 
<pre>
<code>
  public void setDataSource(DataSource dataSource) {
      this.dataSource = dataSource;
  }
</code>
</pre>
이 부분을
<pre>
<code>
  public void setDataSource(DataSource dataSource) {
      this.jdbcContext = new JdbcContext();

      this.jdbcContext.setDataSource(dataSource);
      
      this.dataSource = dataSource;
  }
</code>
</pre>
로 바꿔주면, jdbcContext를 Bean으로 등록하지 않고, DI하는 식이 된다.

### 콜백을 수행하는 메소드(jdbcContext의 updateStrategyContext)에서 다시 콜백(jdbcContext의 executeSql) 메소드를 호출하여 UserDao에서의 로직을 보다 간소화 하였다. 
#### UserDao의 실행 메소드에서 콜백을 위한 익명 클래스 생성까지 하자니 그래도 길었다(deleteAll을 기준으로).

1.  deleteAll에 있는 코드를 모두 executeSql이라는 메소드로 옮겼다.

<pre>
<code>
  // In UserDao.java

  public void executeSql() throws SQLException {

        this.jdbcContext.updateStrategyContext(new StatementStrategy() {
            public PreparedStatement makePreparedStatement(Connection connection) throws SQLException {
                String sql = "Delete From USER";

                PreparedStatement pstmt = connection.prepareStatement(sql);

                return pstmt;
            }
        });
    }
</code>
</pre>

2. 이 실행 문장은 UserDao에서만 사용하는 것이 아니므로, jdbcContext로 옮겼다. 그리고 deleteAll에서는 jdbcContext의 executeSql()을 호출하였다.

<pre>
<code>
  // In JdbcContext.java

  public void executeSql() throws SQLException {

        updateStrategyContext(new StatementStrategy() {
            public PreparedStatement makePreparedStatement(Connection connection) throws SQLException {
                String sql = "Delete From USER";

                PreparedStatement pstmt = connection.prepareStatement(sql);

                return pstmt;
            }
        });
    }
</code>

<code>
  public void deleteAll() throws SQLException {
    this.jdbcContext.executeSql();
  }
</code>

</pre>

3. sql문이 항상 바뀌게 된다. 이것을 입력받게 수정하였다(내부 익명 클래스에서 외부 인자를 사용할 때는 final로 선언).

<pre>
<code>
  public void executeSql(final String sql) throws SQLException {

        updateStrategyContext(new StatementStrategy() {
            public PreparedStatement makePreparedStatement(Connection connection) throws SQLException {
                PreparedStatement pstmt = connection.prepareStatement(sql);

                return pstmt;
            }
        });
    }
</code>

<code>
  public void deleteAll() throws SQLException {
    this.jdbcContext.executeSql("Delete From USER");
  }
</code>

</pre>

4. deleteAll같은 경우에는 매개변수가 필요없었지만, add, update, delete와 같은 경우에는 특정 필드가 필요하다. add와 delete의 경우에는 Target Object를 그대로 넘기면 어느 정도는 매핑이 되겠지만, Update 같은 경우에는 다르다. 따라서 필드를 넘겨받았다(Java 5의 가변인자).

<pre>
<code>
  public void executeSql(final String sql, Object ...args) throws SQLException {

        updateStrategyContext(new StatementStrategy() {
            public PreparedStatement makePreparedStatement(Connection connection) throws SQLException {
                PreparedStatement pstmt = connection.prepareStatement(sql);

                return pstmt;
            }
        });
    }
</code>

<code>
  public void delete(String id) throws SQLException {
    this.jdbcContext.executeSql("Delete From USER Where id = ? ", id);
  }
</code>

</pre>

5. pstmt에 순서대로 클래스타입에 맞춰서 매핑해주었다. 

<pre>
<code>
  public void executeSql(final String sql, Object ...args) throws SQLException {

        updateStrategyContext(new StatementStrategy() {
            public PreparedStatement makePreparedStatement(Connection connection) throws SQLException {
                int index = 1;

                PreparedStatement pstmt = connection.prepareStatement(sql);

                if( param.length > 0 ) {
                    for(Object obj : param) {
                        if( obj instanceof Integer ) {
                            pstmt.setInt(index, (Integer)obj);
                        } else if( obj instanceof String ) {
                            pstmt.setString(index, (String)obj);
                        } else if( obj instanceof Double ) {
                            pstmt.setDouble(index, (Double)obj);
                        } else if( obj instanceof Float ) {
                            pstmt.setFloat(index, (Float)obj);
                        } else if( obj instanceof Long ) {
                            pstmt.setLong(index, (Long)obj);
                        }
                        index++;
                    }
                }

                return pstmt;
            }
        });
    }
</code>
</pre>

##### PreParedStatement의 set~ 메소드의 종류에 따라 더 추가해주면 된다.

#### UserDao에서 단일 반환(int : Count) 구현의 경우, PreparedStatement를 만드는 콜백 메소드는 똑같이하고, 따로 int형 result를 반환하는 템플릿 메소드를 정의하여, 그 값을 바로 반환하는 외부 호출 메소드 executeQueryOneInt를 구현하였다.

##### UserDao의 int 필요 메소드에서 다음과 같이 사용하였다.

<pre>
<code>
  public int count(String id) throws Exception {
        int count = this.jdbcContext.executeQueryOneInt("Select Count(*) As cnt From USER Where id = ? ", id);

        return count;
    }

    public int countAll() throws Exception {
        int count = this.jdbcContext.executeQueryOneInt("Select Count(*) As cnt From USER");

        return count;
    }
</code>
</pre>

#### 객체 반환 및 리스트 반환의 경우 따로, 콜백으로 안 나눈 이유가 ResultSet은 Connection과 PreparedStatement와 함께 닫히는 것이 좋기 때문. 
#### 그리고, JdbcContext에 저장하려다 보니까, 정확한 반환 타입등(Integer, Concre Object, List)을 지정할 수가 없었다.
##### 리플렉션을 사용했다.

### UserDao의 CRUD 시 스프링의 jdbcTemplate 사용
#### jdbcContext에서 PreparedStatement를 생성한 후 반환한 전략 메소드(StatementStrategy의 makePreparedStatement)의 역할을 jdbcTemplate에서는 PreparedStatementCreate 인터페이스가 한다.
#### 조회를 할 때는 그 결과를 가질 ResultSetExtractor<Type> 형태로 생성해준다.
> 참고 사이트 : https://www.mkyong.com/spring/jdbctemplate-queryforint-is-deprecated/
##### ResultSetExtractor : 단일 컬럼, RowMapper : 여러 컬럼을 매핑
#### jdbcTemplate.queryForObject는 단일행이고, query는 기본이 List이다.

#### 스프링 JDBC를 사용할 때는 jdbcTemplate, mybatis와 spring 연동시는 org.mybatis.spring.SqlSessionTemplate 사용

<hr />

### 트랜잭션을 적용하거나 다중 DB, 특정 DB 매핑 기술(JPA, Hibernate, myBatis 등) 등을 쓸 때 UserService에서 Connection 및 특정 구체 객체에 의존하는 경우가 생긴다. 이렇게 서비스 객체에서 특정 기술을 사용하기 때문에 추상화가 필요하다(이걸 스프링이 제공해준다).
### 자바 Mail Api를 스프링에서 사용하기 쉽게 추상화한 SimpleMailMessage(MimeMessage), JavaMailSenderImpl(MailSender)

<hr />

### 테스트 시 테스트 내역은 좁히는 것이 좋다(데이터베이스, 네트워크 같은 것들은 제외하고 로직만 테스트하는 것이 좋다).
### 트랜잭션을 따로 적용할 때 뺀 UserServiceImpl(실제 로직)와 UserServiceTx(부가 기능 추가)는 데코레이터 + 프록시 패턴이다.
### 트랜잭션을 적용할 대상이 더 생길 수도 있다. 현재는 하나의 메소드에만 트랜잭션 경계를 설정하였으나, 더 많은 메소드에 트랜잭션 경계를 적용하려면 코드의 중복을 피할 수 없다. 

### 프록시 : 타깃과 동일한 인터페이스를 구현. 타깃에 접근 권한을 제어하거나 부가 기능을 추가하고자 할 때 사용할 수 있다. 하지만, 부가 기능이나 접근 권한을 줄 메소드를 제외한 나머지 모든 인터페이스의 메소드들을 구현하여 한다.

### 다이내믹 프록시 : 런타임 시 동적으로 생성되는 프록시. 다이내믹 프록시를 사용하면, 어떤 타깃에도 해당 인터페이스의 모든 메서드를 구현하지 않고, 필요한 메소드에만 특정 기능을 부여할 수 있다(Proxy.newInstance 사용).
#### 패턴을 입력하지 않으면 트랜잭션 인보커를 실행하지 않는다.
#### 다이내믹 프록시의 경우 클래스가 어떤 것인지 알 수 없어서, 스프링 자체에서 Bean 객체로 생성해줄 수 없다.
##### 스프링의 팩토리빈(FactoryBean)을 사용하면, 다이내믹 프록시를 Bean으로 등록할 수도 있다.
### 다이내믹 프록시로 부가 기능을 추가하는 것도 괜찮은 방법이지만, 하나의 target만을 설정할 수 있다(즉, 한 번에 하나만 적용할 수 있다). 만약 한 target에 여러 개의 부가 기능을 추가하고 싶다면, 더 많은 부가기능에 target으로 넘기면 되지만, 그에 따라 코드 양도 증가하게 된다. 그리고, 부가기능 부여 객체 또한 계속 생성된다(싱글톤이 아니다). 
### 스프링에는 프록시를 만들고, 이 프록시를 Bean으로 만들어주는 ProxyFactoryBean이라는 게 존재한다. 
### ProxyFactoryBean이 생성하는 프록시에 추가할 부가기능은 MethodInterceptor 인터페이스를 구현한다.
#### MethodInterceptor에는 InvocationHandler처럼 클래스가 딱 하나 밖에 없으며, InvocationHandler와는 달리 타깃을 넘기지 않아도 된다.

<hr />

### 추가 기능은 같지만 target이 다른 비슷한 내용의 ProxyFactoryBean 설정이 중복되는 경우가 있다.

<pre>
<code>
&lt;bean id="userService" class="org.springframework.aop.framework.ProxyFactoryBean"&gt;
        &lt;property name="target" ref="[이 부분]" /&gt;
        ...
    &lt;/property&gt;
&lt;/bean>
</code>
</pre>

#### 이럴 때는 BeanPostProcessor 인터페이스를 사용하여 중복을 없앨 수 있다(변하지 않는 부분 중 핵심 부분을 제외하고, 대부분 확장 가능한 확장 포인트).
##### DefaultAdvisorAutoProxyCreator를 단순히 Bean으로만 등록해놓으면 된다. Bean으로 등록되어 있을 경우 자동으로, 스프링에서 만든 Bean을 후처리 프로세서로 보낸다.
> 후처리 프로세서에서 넘겨진 Bean이 프록시 적용 대상일 경우 현재 Bean에 대한 프록시를 만들게 하고, 어드바이저를 연결해준다.
>> 프록시가 생성되면, 원래 컨테이너가 전달해준 Bean 객체 대신 프록시 오브젝트를 컨테이너에게 돌려준다.
> Bean 대상이 아닐 경우, 컨테이너에게 넘겨받은 Bean 객체 그대로를 돌려준다.
##### 이렇게 하면, 일일이 ProxyFactoryBean을 등록하지 않아도 된다.
##### DefaultAdvisorAutoProxyCreator을 적용할 경우, Bean 설정 파일에서 ProxyFactoryBean 부분을 빼야 한다.
### PointCut은 표현식을 이용하여 지정할 수 있다.
#### PointCut이 내장된 어드바이저를 사용하면 더 보기 쉬운 대신에, 공통으로 사용할 포인트컷이 없고, 외장되었을 경우 내장된 경우보다 보기가 더 깔끔하지 않다.

> DefaultTransactionDefinition
>> 트랜잭션을 어떻게 동작시킬것인가에 대한 정의를 내리는 클래스
>>> 전파 : 이미 진행 중인 트랜잭션이 있을 경우 그 트랜잭션과 합칠지 아니면 전혀 별개의 트랜잭션을 새로 생성할지에 대한 옵션(같은 메소드에서 다른 jdbcTemplate.update 메소드 호출)
>>> 격리수준 : 멀티 트랜잭션
>>> 제한시간 : 수행하는 제한시간
>>> 읽기전용 : 읽기전용으로 할 경우 수정이나 삽입, 삭제 못함

### 메소드별로 각기 다른 트랜잭션을 속성을 정의하려면, 현재 Advice(TransactionAdvice)의 기능을 확장해야 한다(invoke의 매개변수 MethodInvocation에 정의된 getMethod 변수를 이용하여 그 메소드의 이름에 따라 각기 다른 속성을 사용하는 방식으로 하면 됨). 스프링에서 제공하는 TransactionInterceptor를 사용하면 해결.

### Transaction 적용 시 어드바이저 만드는 방법도 있지만, 애노테이션을 이용하여 만드는 방법도 존재(@Transactional). 우선시 되는 순서가 존재한다.

1. 타겟 클래스(Transaction을 적용할 메소드가 존재하는 클래스 : Service 클래스)
2. 대상 메소드(타겟 클래스에 선언된)
3. 인터페이스(타겟 클래스가 구현한 인터페이스)
4. 인터페이스의 추상 메소드

> 설정 파일에 다음과 같이 작성해주면 @Transactional 애노테이션을 사용할 수 있다.
<code>
&lt;tx:annotation-driven /&gt;
</code>

>> 설정 파일의
<pre>
<code>
    &lt;aop:config&gt;
        &lt;aop:advisor advice-ref="transactionAdvice" pointcut="bean(*Service)" /&gt;
    &lt;/aop:config&gt;

    &lt;tx:advice id="transactionAdvice"&gt;
        &lt;tx:attributes&gt;
            &lt;tx:method name="get*" propagation="REQUIRED" read-only="true" timeout="30" /&gt;
            &lt;tx:method name="count*" propagation="REQUIRED" read-only="true" timeout="30" /&gt;
            &lt;tx:method name="select*" propagation="REQUIRED" read-only="true" timeout="30" /&gt;
            &lt;tx:method name="upgrade*" propagation="REQUIRES_NEW" isolation="SERIALIZABLE" /&gt;
            &lt;tx:method name="*" /&gt;
        &lt;/tx:attributes&gt;
    &lt;/tx:advice&gt;

</code>
</pre>

>> 부분을 주석처리하고, 타겟 클래스에 
<code>
    @Transactional 
</code>

>> 애노테이션을 달고, get, count, select로 시작하는 메소드에

<code>
    @Transactional(readOnly)
</code>

>> 를 달아도 기존 테스트(트랜잭션)와 똑같은 결과를 내보였다.

#### 애노테이션은 설정을 간단하게 하고, 보다 정밀하게 설정하고 싶을 때는 xml에서 설정해야되겠다.

### JAXB 사용법(jdk 11 기준)
1. xsd 파일 작성(프로젝트 루트에 저장)
2. https://javaee.github.io/jaxb-v2/ 에서 다운로드
3. 압축 풀고, 특정 위치에 옮기기.
4. cmd창 띄우고, 프로젝트 루트에서 
<code>
java -cp [zip 파일 압축 해제]\* com.sun.tools.xjc.XJCFacade -p [압축 풀 위치] [1에서 작성한 파일 이름 확장자까지] -d [기본 디렉토리] -encoding UTF-8
</code>

## 책에 나와있는 순환참조를 하니까 allowEagerInit 에러가 뜬다(스프링 5.1.5 기준, circular reference). 
<code>
&lt;beans default-lazy-init="true"&gt;&lt;/beans&gt;
</code>
와 같은 형태를 하라고 해서 했는데도, 안 되고, lazy-init 속성을 사용해도 안 되었다. 그냥 이런 순환 참조 형태가 안 되게끔 만들어야 되겠다.

### Resource : 서비스를 제공하는 것이 아닌 단순한 정보를 가진 값
#### 실질적으로 가져오는 거 ResourceLoader
##### 접두어를 붙이면, ResourceLoader의 종류와 상관없이 해당 접두어(위치)에 따라 리소스를 가져온다.
> file:(FileSystemResource), classpath:(ClassPathResource), 접두어 없음(ResourceLoader 구현에 따라 결정), http:(UrlResource)

#### @PostConstruct와 @PreDestory는 deprecated 대신 스프링의 InitializingBean을 구현하여 afterPropertiesSet() 메소드에 PostConstruct 메소드를, @PreDestroy 대신 DisposableBean을 구현하여, destroy() 메소드를 작성