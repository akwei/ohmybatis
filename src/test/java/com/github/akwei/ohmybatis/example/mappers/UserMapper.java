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
 * entity must has only one mapper like this class use &lt;User&gt;
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
