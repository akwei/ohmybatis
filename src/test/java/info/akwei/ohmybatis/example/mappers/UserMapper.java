package info.akwei.ohmybatis.example.mappers;

import info.akwei.ohmybatis.MapperIface;
import info.akwei.ohmybatis.annotations.AfterWhere;
import info.akwei.ohmybatis.annotations.NotColumn;
import info.akwei.ohmybatis.example.entity.User;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends MapperIface<User> {

    //use auto incr id
    @Options(useGeneratedKeys = true, keyProperty = "userid")
    void insert(User user);

    void delete1(@Param("userid") long userid);

    void delete2(Integer sex, boolean enableflag);

    User getById(@Param("userid") long userid);

    //sometimes select have order by, group by etc sql, we can use @AfterWhere to help us for these situation
    //AfterWhere value must use table's column name
    @AfterWhere("order by createtime desc limit #{offset} #{size}")
    List<User> getList(Integer sex, boolean enableflag, @NotColumn int offset, @NotColumn int size);

    int count(Integer sex, boolean enableflag);

    //if you want to use some simple sql ,for example:
    //

}
