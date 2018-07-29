package info.akwei.ohmybatis.example.mappers;

import info.akwei.ohmybatis.annotations.*;
import info.akwei.ohmybatis.example.entity.VoteItem;
import info.akwei.ohmybatis.mapper.BaseMapper;
import info.akwei.ohmybatis.sqlprovider.EntitySQLProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface VoteItemMapper extends BaseMapper<VoteItem> {

    @Select("select * from tb_vote_item where vote_item_id=#{voteItemId}")
    VoteItem getByVoteItemId(int voteItemId);

    VoteItem getByVoteItemId2(int voteItemId);

    @SelectProvider(type = EntitySQLProvider.class, method = EntitySQLProvider.SELECT)
    @AfterWhere("order by create_time desc limit #{offset},#{size}")
    List<VoteItem> getListByCdn(
            int voteItemId,
            @NotEq("voteItemId") int noVoteItemId,
            String title,
            @MinValue("createTime") long beginTime,
            @MaxValue("createTime") long endTime,
            @NotColumn int offset,
            @NotColumn int size);

    @SelectProvider(type = EntitySQLProvider.class, method = EntitySQLProvider.SELECT)
    @AfterWhere("order by create_time desc limit #{offset},#{size}")
    List<VoteItem> getListByCdn2(
            @Like String title,
            @NotColumn int offset,
            @NotColumn int size);

    @Insert("insert into tb_vote_item(title,create_time) values(#{title},#{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "voteItemId")
    void insert(VoteItem voteItem);

    @InsertProvider(type = EntitySQLProvider.class, method = EntitySQLProvider.INSERT)
    @Options(useGeneratedKeys = true, keyProperty = "voteItemId")
    void insert2(VoteItem voteItem);

    @UpdateProvider(type = EntitySQLProvider.class, method = EntitySQLProvider.UPDATE_OBJ)
    int update(VoteItem voteItem, VoteItem old);

    @DeleteProvider(type = EntitySQLProvider.class, method = EntitySQLProvider.DELETE)
    int delete(@Param("voteItemId") int voteItemId);

    @DeleteProvider(type = EntitySQLProvider.class, method = EntitySQLProvider.DELETE)
    int delete2(Class clazz, int voteItemId, String title);

    @SelectProvider(type = EntitySQLProvider.class, method = EntitySQLProvider.COUNT)
    int count(Class clazz, int voteItemId, String title);

    List<VoteItem> getMapInIds(List<Integer> ids);
}
