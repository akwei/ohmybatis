package info.akwei.ohmybatis.example.test;

import info.akwei.ohmybatis.example.entity.User;
import info.akwei.ohmybatis.example.mappers.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext.xml"})
@Transactional
public class MapperTest2 {

    @Resource
    private UserMapper userMapper;

    @Test
    public void lock() throws Exception {
        User user = this.userMapper.getById2(10000, true);
        Thread.sleep(100000);
    }
}
