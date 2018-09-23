package com.github.akwei.ohmybatis.example.test;

import com.github.akwei.ohmybatis.example.entity.User;
import com.github.akwei.ohmybatis.sqlprovider.EntityCopier;
import com.github.akwei.ohmybatis.example.mappers.UserMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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
        this.userMapper.deleteByUserid1(1);

        //delete from user where sex=#{sex} and enableflag=#{enableflag}
        this.userMapper.deleteBySexAndEnableflag2(1, true);

        //delete from user where userid=#{id}
        this.userMapper.deleteByUserid3(1);

        this.userMapper.getList2(1, true, "a", 0, 2, 0, 10);

        //if you want to use for update ,must has one boolean parameter named forUpdate. you can set value true / false
        this.userMapper.getById2(1, true);

        //if parameter forUpdate set value false,sql like:
        //select * from user where userid=?
        this.userMapper.getById2(1, false);

        //if you has more complicated select sql or multiple tables select sql,please use xml or @Select do it

    }

}
