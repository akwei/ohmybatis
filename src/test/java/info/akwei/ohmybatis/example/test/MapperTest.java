package info.akwei.ohmybatis.example.test;

import info.akwei.ohmybatis.example.entity.User;
import info.akwei.ohmybatis.example.mappers.UserMapper;
import info.akwei.ohmybatis.sqlprovider.EntityCopier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

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
        this.userMapper.insert(user);
        // auto_incr for userid
        Assert.assertTrue(user.getUserid() > 0);

        //insert another user
        User other = new User();
        other.setNick("danny");
        other.setSex(1);
        other.setAddr("China");
        other.setEnableflag(false);
        user.setLevel(2);
        this.userMapper.insert(user);

        //select user
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

        //if you want to update as :  update user set nick=? where userid=?

        //1 copy user to old
        User old = new User();
        EntityCopier.copy(user, old);

        //2 change user property
        user.setNick("akweiwei11");

        //3 invoke updateObj, set the second parameter = old
        ret = this.userMapper.updateObj(user, old);

        //must update success
        Assert.assertEquals(1, ret);

    }

}
