package info.akwei.ohmybatis.example.mappers;

import info.akwei.ohmybatis.MapperIface;
import info.akwei.ohmybatis.annotations.*;
import info.akwei.ohmybatis.example.entity.User;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * all mapper's parameter name in methods as same as bean's field name
 */
public interface UserMapper extends MapperIface<User> {

    //insert will ignore @Param
    //use auto incr id
    @Options(useGeneratedKeys = true, keyProperty = "userid")
    void insert(User user);

    //delete from user where userid=#{userid}
    void deleteByUserid1(@Param("userid") long userid);

    //delete from user where sex=#{sex} and enableflag=#{enableflag}
    void deleteBySexAndEnableflag2(Integer sex, boolean enableflag);

    //delete from user where userid=#{id}
    void deleteByUserid3(@Param("id") long userid);

    //nick as same as bean's field name
    //newCreateTime not as same as bean's field createtime
    //update user set nick=#{nick}, createtime=#{ctime} where userid=#{userid}
    int updateNickAndCreateTime1(int userid, @UpdateField String nick, @UpdateField @Param("ctime") long createtime);

    //update user set nick=#{nick}, createtime=#{createtime} where userid=#{userid}
    int updateNickAndCreateTime2(int userid, @UpdateField String nick, @UpdateField long createtime);

    //select * from user where userid=#{userid}
    User getById(@Param("userid") long userid);

    User getById2(long userid, boolean forUpdate);

    //select * from user where sex=#{sex} and enableflag=#{enableflag} order by createtime desc limit #{offset} , #{size}
    //sometimes select have order by, group by etc sql, we can use @AfterWhere to help us for these situation
    //AfterWhere value must use table's column name
    @AfterWhere("order by createtime desc limit #{offset} , #{size}")
    List<User> getList(Integer sex, boolean enableflag, @NotColumn int offset, @NotColumn int size);

    //select count(*) from user where sex=#{sex} and enableflag=#{enableflag}
    int count(Integer sex, boolean enableflag);

    //look a bit of complicated example for single table select:
    //select * from user where sex=#{sex} and enableflag=#{enableflag} and nick like \"%\"#{nick}\"%\" and level>=#{minLevel} and level<=#{maxLevel} order by createtime desc limit #{offset} , #{size}
    // level as same as bean's level
    // nick as same as bean's nick
    @AfterWhere("order by createtime desc limit #{offset} , #{size}")
    List<User> getList2(Integer sex,
                        boolean enableflag,
                        @Like("nick") String nick,
                        @MinValue("level") int minLevel,
                        @MaxValue("level") int maxLevel,
                        @NotColumn int offset, @NotColumn int size);

}
