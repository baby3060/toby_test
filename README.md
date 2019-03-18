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

<hr />

### 트랜잭션을 적용하거나 다중 DB, 특정 DB 매핑 기술(JPA, Hibernate, myBatis 등) 등을 쓸 때 UserService에서 Connection 및 특정 구체 객체에 의존하는 경우가 생긴다. 이렇게 서비스 객체에서 특정 기술을 사용하기 때문에 추상화가 필요하다(이걸 스프링이 제공해준다).
### 자바 Mail Api를 스프링에서 사용하기 쉽게 추상화한 SimpleMailMessage(MimeMessage), JavaMailSenderImpl(MailSender)