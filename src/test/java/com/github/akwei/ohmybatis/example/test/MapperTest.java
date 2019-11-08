package com.github.akwei.ohmybatis.example.test;

import com.github.akwei.ohmybatis.EntityCopier;
import com.github.akwei.ohmybatis.example.entity.User;
import com.github.akwei.ohmybatis.example.mappers.UserMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
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
        user.snapshot();
        User old = user.getSnapShotObj();
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
        user.snapshot();
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
        idList.add(user_batch.getUserid());
        idList.add(other_batch.getUserid());
        Assert.assertEquals(2, this.userMapper.countInUserids(idList));
        System.out.println(this.userMapper.getListInUserids(idList, 0, 10));
        System.out.println(this.userMapper.selectInIds(idList));
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
