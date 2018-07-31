Notice
----------
* column name、table name can be underscore and java bean property can be camelCase. **For example (column: user_name, java property: userName)**
* colum name、table name can equals with java bean property. **For example (column:user_name, java property: user_name or column:userName, java property userName)**
* complie with java8 and use argument -parameters, if use maven, add following xml data to pom.xml

```` xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.3</version>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
        <compilerArgs>
            <arg>-parameters</arg>
        </compilerArgs>
    </configuration>
</plugin>
````

How to use
----------
* pom import dependency

````xml
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.4.6</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-spring</artifactId>
    <version>1.3.2</version>
    <scope>provided</scope>
</dependency>
<!-- spring -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-beans</artifactId>
    <version>${org.springframework.version}</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>${org.springframework.version}</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>${slf4j.version}</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.7</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>javax.persistence</groupId>
    <artifactId>javax.persistence-api</artifactId>
    <version>2.2</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>25.1-jre</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>cglib</groupId>
    <artifactId>cglib</artifactId>
    <version>3.2.7</version>
    <scope>provided</scope>
</dependency>
````

* write spring application.xml
 
```` xml
<mybatis:scan base-package="info.akwei.ohmybatis.example.mappers"/>

<bean id="configuration" class="org.apache.ibatis.session.Configuration">
    <property name="mapUnderscoreToCamelCase" value="true"/>
    <property name="logImpl" value="org.apache.ibatis.logging.stdout.StdOutImpl"/>
</bean>

<!- use special sqlSessionFactoryBean ->
<bean id="sqlSessionFactoryBean" class="info.akwei.ohmybatis.OhSqlSessionFactoryBean">
    <property name="dataSource" ref="dataSource"/>
    <property name="configuration" ref="configuration"/>
</bean>

````

* write a bean

```` java

import info.akwei.ohmybatis.annotations.NotColumn;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "user")
public class User {

    @Id
    private long userid;

    private String nick;

    private Integer sex;

    private String addr;

    private boolean enableflag;

    private int level;

    private long createtime;

    //use @NotColumn mark the property is not a table column
    @NotColumn
    private String otherField;

    setter getter ......
}

````  

* write a bean's mapper extends MapperIface<T>

```` java

import info.akwei.ohmybatis.MapperIface;
import info.akwei.ohmybatis.annotations.*;
import info.akwei.ohmybatis.example.entity.User;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends MapperIface<User> {

    @Options(useGeneratedKeys = true, keyProperty = "userid")
    void insert(User user);

    //delete from user where userid=#{userid}
    void delete1(@Param("userid") long userid);

    //delete from user where sex=#{sex} and enableflag=#{enableflag}
    void delete2(Integer sex, boolean enableflag);

    //select * from user where userid=#{userid}
    User getById(@Param("userid") long userid);

    //select * from user where sex=#{sex} and enableflag=#{enableflag} order by createtime desc limit #{offset} , #{size}
    //sometimes select have order by, group by etc sql, we can use @AfterWhere to help us for these situation
    //AfterWhere value must use table's column name
    @AfterWhere("order by createtime desc limit #{offset} , #{size}")
    List<User> getList(Integer sex, boolean enableflag, @NotColumn int offset, @NotColumn int size);

    //select count(*) from user where sex=#{sex} and enableflag=#{enableflag}
    int count(Integer sex, boolean enableflag);

    //look a bit of complicated example for single table select:
    //select * from user where sex=#{sex} and enableflag=#{enableflag} and nick like \"%\"#{nick}\"%\" and level>=#{minLevel} and level<=#{maxLevel} order by createtime desc limit #{offset} , #{size}
    @AfterWhere("order by createtime desc limit #{offset} , #{size}")
    List<User> getList2(Integer sex,
                        boolean enableflag,
                        @Like("nick") String nick,
                        @MinValue("level") int minLevel,
                        @MaxValue("level") int maxLevel,
                        @NotColumn int offset, @NotColumn int size);

}

````

* invoke mapper method

```` java
    @Resource
    private UserMapper userMapper;

    @Test
    public void insert() {
        //insert user
        User user = new User();
        user.setAddr("北京市");
        user.setEnableflag(true);
        user.setNick("akwei");
        user.setLevel(1);
        user.setCreatetime(System.currentTimeMillis());
        this.userMapper.insert(user);
        // auto_incr for userid
        Assert.assertTrue(user.getUserid() > 0);

        //insert another user
        User other = new User();
        other.setNick("danny");
        other.setSex(1);
        other.setAddr("China");
        other.setEnableflag(true);
        other.setLevel(2);
        other.setCreatetime(System.currentTimeMillis());
        this.userMapper.insert(other);

        //select * from user where userid=#{userid}
        User user1 = this.userMapper.getById(user.getUserid());

        //equals
        Assert.assertNotNull(user1);
        Assert.assertEquals(user.getAddr(), user1.getAddr());
        Assert.assertEquals(user.isEnableflag(), user1.isEnableflag());
        Assert.assertEquals(user.getNick(), user1.getNick());

        //update
        user.setNick("akweiwei");
        int ret = this.userMapper.updateObj(user, null);

        //must update success
        Assert.assertEquals(1, ret);

        //if you want to update as :  update user set nick=#{nick} where userid=#{userid}

        //1 copy user to old
        User old = new User();
        EntityCopier.copy(user, old);

        //2 change user property
        user.setNick("akweiwei11");

        //3 invoke updateObj, set the second parameter = old
        ret = this.userMapper.updateObj(user, old);

        //must update success
        Assert.assertEquals(1, ret);

        //select
        //simple select: select * from user where sex=#{sex} and enableflag=#{enableflag} order by createtime desc limit #{offset} , #{size}
        List<User> list = this.userMapper.getList(1, true, 0, 10);
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());

        //simple count: select count(*) from user where sex=#{sex} and enableflag=#{enableflag}
        int count = this.userMapper.count(1, true);
        Assert.assertEquals(1, count);

        //delete
        //delete from user where userid=#{userid}
        this.userMapper.delete1(1);

        //delete from user where sex=#{sex} and enableflag=#{enableflag}
        this.userMapper.delete2(1, true);

        this.userMapper.getList2(1, true, "a", 0, 2, 0, 10);

        //if you has more complicated select sql or multiple tables select sql,please use xml or @Select do it
    }
````