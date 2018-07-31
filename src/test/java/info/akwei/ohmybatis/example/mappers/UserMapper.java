package info.akwei.ohmybatis.example.mappers;

import info.akwei.ohmybatis.MapperIface;
import info.akwei.ohmybatis.annotations.*;
import info.akwei.ohmybatis.example.entity.User;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends MapperIface<User> {

    //use auto incr id
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
    @AfterWhere("order by createtime desc limit #{offset} #{size}")
    List<User> getList(Integer sex, boolean enableflag, @NotColumn int offset, @NotColumn int size);

    //select count(*) from user where sex=#{sex} and enableflag=#{enableflag}
    int count(Integer sex, boolean enableflag);

    //look a bit of complicated example for single table select:
    //select * from user where sex=#{sex} and enableflag=#{enableflag} and nick like \"%\"#{nick}\"%\" and level>=#{minLevel} and level<=#{maxLevel} order by createtime desc limit #{offset} , #{size}
    @AfterWhere("order by createtime desc limit #{offset} #{size}")
    List<User> getList2(Integer sex,
                        boolean enableflag,
                        @Like("nick") String nick,
                        @MinValue("level") int minLevel,
                        @MaxValue("level") int maxLevel,
                        @NotColumn int offset, @NotColumn int size);

}
