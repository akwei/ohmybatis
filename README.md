## Info
can use insert object, inserBatch, update object, deleteById, selectById

Notice
----------
* column name、table name can be underscore and java bean property can be camelCase. **For example (column: user_name, java property: userName)**
* colum name、table name can equals with java bean property. **For example (column:user_name, java property: user_name or column:userName, java property userName)**

How to use
----------
## pom import dependency
* mybatis-3.4.6
* mybatis-spring-1.3.2
* spring4
* slf4j
* log4j2
* mysql-connector-java-5.1.41
* commmons-lang3
* javax.persistence-api
* guava-25.1-jre
* cblib-3.2.7


### write spring application.xml
 
```` xml
<mybatis:scan base-package="com.github.akwei.ohmybatis.example.mappers"/>
... other config like datasource ...

<bean id="configuration" class="com.github.akwei.ohmybatis.OhMyConfiguration">
    <property name="mapUnderscoreToCamelCase" value="true"/>
    <property name="logImpl" value="org.apache.ibatis.logging.stdout.StdOutImpl"/>
</bean>
<bean id="sqlSessionFactoryBean" class="org.mybatis.spring.SqlSessionFactoryBean">
    <property name="dataSource" ref="dataSource"/>
    <property name="configuration" ref="configuration"/>
    <property name="mapperLocations" value="classpath*:mappers/*-mapper.xml"/>
</bean>
<bean class="com.github.akwei.ohmybatis.IMapperFactory"/>

````

* write a bean

``` java
package com.github.akwei.ohmybatis.example.entity;

import com.github.akwei.ohmybatis.BaseEntity;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

//table name
@Table(name = "t_user")
public class User extends BaseEntity<User> {

    //mark userid is a primary key of table user.
    //table can have multi @Id property, like composite-id
    @Id
    // id auto increment by mysql
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userid;

    private String nick;

    private Integer sex;

    private String addr;

    private boolean enableflag;

    private int level;

    private Date createtime;

    //use @NotColumn mark the property is not a table column
    @Transient
    private String otherField;

    getter setter ...
}


```

* write a bean's mapper extends IMapper<T>

``` java

package com.github.akwei.ohmybatis.example.mappers;

import com.github.akwei.ohmybatis.IMapper;
import com.github.akwei.ohmybatis.OhMyXmlDriver;
import com.github.akwei.ohmybatis.SQL;
import com.github.akwei.ohmybatis.example.entity.User;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * all mapper's parameter name in methods as same as bean's field name
 */
public interface UserMapper extends IMapper<User> {

    //select * from user where userid in (?,?..)
    @Lang(OhMyXmlDriver.class)
    @Select({SQL.SELECT,
          "where userid $in{userids} order by createtime desc limit #{offset} , #{size}"})
    List<User> getListInUserids(@Param("userids") List<Long> userids, int offset, int size);

    //select count(*) from user where userid in (?,?..)
    @Lang(OhMyXmlDriver.class)
    @Select({SQL.COUNT, "where userid $in{userids} order by userid desc"})
    int countInUserids(@Param("userids") List<Long> userids);

    @Lang(OhMyXmlDriver.class)
    @Update({SQL.UPDATE, "set createtime=#{createtime} where userid $in{userids}"})
    int updateInUserids(@Param("userids") List<Long> userids, Date createtime);

    @Lang(OhMyXmlDriver.class)
    @Delete({SQL.DELETE, "where userid $in{userids}"})
    int deleteInUserids(@Param("userids") List<Long> userids);
}

```

* example for use
```java
package com.github.akwei.ohmybatis.example.test;

import com.github.akwei.ohmybatis.EntityCopier;
import com.github.akwei.ohmybatis.example.entity.User;
import com.github.akwei.ohmybatis.example.mappers.UserMapper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext.xml"})
@Transactional
public class MapperTest {

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
        user.setCreatetime(new Date());
        this.userMapper.insert(user);
        // auto_incr for userid
        Assert.assertTrue(user.getUserid() > 0);
        User userFromDb = this.userMapper.selectById(user.getUserid(), false);
        Assert.assertNotNull(userFromDb);
        Assert.assertEquals(user.getUserid(), userFromDb.getUserid());
    }

    @Test
    public void insert1() {
        //insert user
        User user = new User();
        user.setAddr("北京市");
        user.setEnableflag(true);
        user.setNick("akwei");
        user.setLevel(1);
        user.setCreatetime(new Date());
        user.insert();
        // auto_incr for userid
        Assert.assertTrue(user.getUserid() > 0);
        User userFromDb = this.userMapper.selectById(user.getUserid(), true);
        Assert.assertNotNull(userFromDb);
        Assert.assertEquals(user.getUserid(), userFromDb.getUserid());
    }

    @Test
    public void update() {
        //insert user
        User user = new User();
        user.setAddr("北京市");
        user.setEnableflag(true);
        user.setNick("akwei");
        user.setLevel(1);
        user.setCreatetime(new Date());
        this.userMapper.insert(user);

        //update
        user.setNick("akweiwei");

        //must update success
        Assert.assertEquals(1, this.userMapper.update(user, null));

        //if you want to update as: update user set nick=#{nick} where userid=#{userid}
        //1 copy user to old
        User old = user.snapshot();
        EntityCopier.copy(user, old);
        //2 change user property
        user.setNick("akweiwei11");
        //must update success
        Assert.assertEquals(1, this.userMapper.update(user, old));

        //or use save as: update user set nick=#{nick} where userid=#{userid}
        user.setNick("akweiwei22");
        this.userMapper.update(user, old);
    }

    @Test
    public void update1() {
        //insert user
        User user = new User();
        user.setAddr("北京市");
        user.setEnableflag(true);
        user.setNick("akwei");
        user.setLevel(1);
        user.setCreatetime(new Date());
        user.insert();

        //update
        user.setNick("akweiwei");

        //must update success
        Assert.assertEquals(1, user.update());

        //if you want to update as: update user set nick=#{nick} where userid=#{userid}
        //snapshot user
        user.localSnapshot();
        //change user property
        user.setNick("akweiwei11");
        //update t_user set nick=#{nick} where userid=#{userid}
        //must update success
        Assert.assertEquals(1, user.update());

        //or use save as: update user set nick=#{nick} where userid=#{userid}
        user.setNick("akweiwei22");
        Assert.assertEquals(1, user.update());
    }

    @Test
    public void insertBatch() {
        User user_batch = new User();
        user_batch.setAddr("北京市");
        user_batch.setEnableflag(true);
        user_batch.setNick("akwei");
        user_batch.setLevel(1);
        user_batch.setCreatetime(new Date());

        //insert another user
        User other_batch = new User();
        other_batch.setNick("danny");
        other_batch.setSex(111111);
        other_batch.setAddr("China");
        other_batch.setEnableflag(true);
        other_batch.setLevel(2);
        other_batch.setCreatetime(new Date());

        List<User> userlist = new ArrayList<>();
        userlist.add(user_batch);
        userlist.add(other_batch);
        this.userMapper.insertBatch(userlist);

        // auto_incr for userid
        Assert.assertTrue(user_batch.getUserid() > 0);
        Assert.assertTrue(other_batch.getUserid() > 0);
    }

    @Test
    public void selectIn() {
        User user_batch = new User();
        user_batch.setAddr("北京市");
        user_batch.setEnableflag(true);
        user_batch.setNick("akwei");
        user_batch.setLevel(1);
        user_batch.setCreatetime(new Date());

        //insert another user
        User other_batch = new User();
        other_batch.setNick("danny");
        other_batch.setSex(111111);
        other_batch.setAddr("China");
        other_batch.setEnableflag(true);
        other_batch.setLevel(2);
        other_batch.setCreatetime(new Date());

        List<User> userlist = new ArrayList<>();
        userlist.add(user_batch);
        userlist.add(other_batch);
        this.userMapper.insertBatch(userlist);

        List<Long> idList = new ArrayList<>();
        idList.add(1L);
        idList.add(2L);
        System.out.println(this.userMapper.getListInUserids(idList, 0, 10));
        System.out.println(this.userMapper.countInUserids(idList));
        this.userMapper.updateInUserids(idList, new Date());
        this.userMapper.deleteInUserids(idList);
    }

    @Test
    public void selectById() {
        //insert user
        User user = new User();
        user.setAddr("北京市");
        user.setEnableflag(true);
        user.setNick("akwei");
        user.setLevel(1);
        user.setCreatetime(new Date());
        this.userMapper.insert(user);
        Assert.assertEquals(user.getUserid(),
              this.userMapper.selectById(user.getUserid(), true).getUserid());
        Assert.assertEquals(user.getUserid(),
              this.userMapper.selectById(user.getUserid(), false).getUserid());

    }

    @Test
    public void deleteById() {
        //insert user
        User user = new User();
        user.setAddr("北京市");
        user.setEnableflag(true);
        user.setNick("akwei");
        user.setLevel(1);
        user.setCreatetime(new Date());
        this.userMapper.insert(user);
        Assert.assertEquals(user.getUserid(),
              this.userMapper.selectById(user.getUserid(), true).getUserid());
        this.userMapper.deleteById(user.getUserid());
        Assert.assertNull(this.userMapper.selectById(user.getUserid(), false));
    }

    @Test
    public void deleteById2() {
        //insert user
        User user = new User();
        user.setAddr("北京市");
        user.setEnableflag(true);
        user.setNick("akwei");
        user.setLevel(1);
        user.setCreatetime(new Date());
        user.insert();
        Assert.assertEquals(user.getUserid(),
              this.userMapper.selectById(user.getUserid(), true).getUserid());
        Assert.assertEquals(1, user.delete());
        Assert.assertNull(this.userMapper.selectById(user.getUserid(), false));
    }
}


```