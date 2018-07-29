package info.akwei.ohmybatis.example.test;

import info.akwei.ohmybatis.example.entity.VoteItem;
import info.akwei.ohmybatis.example.mappers.VoteItemMapper;
import info.akwei.ohmybatis.sqlprovider.EntityCopier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext.xml"})
public class MapperTest {

    @Resource
    private VoteItemMapper voteItemMapper;

    @Test
    public void getVoteItem() {
        VoteItem voteItem = this.voteItemMapper.getByVoteItemId(1);
        Assert.assertNotNull(voteItem);
    }

    @Test
    public void getVoteItem2() {
        VoteItem voteItem = this.voteItemMapper.getByVoteItemId2(1);
        Assert.assertNotNull(voteItem);
    }

//    @Test
//    public void getMapInIds() {
//        Map<Integer, VoteItem> map = this.voteItemService.getMapInIds(Arrays.asList(1, 2, 3));
//        System.out.println(map);
//    }

    @Test
    public void create() {
        VoteItem voteItem = new VoteItem();
        voteItem.setTitle("akweiwei");
        voteItem.setCreateTime(System.currentTimeMillis());
        this.voteItemMapper.insert(voteItem);
        System.out.println(voteItem.getVoteItemId());
    }

    @Test
    public void create2() {
        VoteItem voteItem = new VoteItem();
        voteItem.setTitle("akweiwei");
        voteItem.setCreateTime(System.currentTimeMillis());
        this.voteItemMapper.insert2(voteItem);
        System.out.println(voteItem.getVoteItemId());
    }

    @Test
    public void update() {
        VoteItem voteItem = this.voteItemMapper.getByVoteItemId2(1);
        VoteItem old = new VoteItem();
        EntityCopier.copy(voteItem, old);
        voteItem.setTitle("hhhhhhhhh" + System.currentTimeMillis());
        int res = this.voteItemMapper.update(voteItem, old);
        Assert.assertEquals(1, res);
    }

    @Test
    public void delete() {
        int res = this.voteItemMapper.delete(0);
        Assert.assertEquals(0, res);
    }

    @Test
    public void delete2() {
        int res = this.voteItemMapper.delete2(VoteItem.class, 1, "hhh");
        Assert.assertEquals(0, res);
    }

    @Test
    public void select() {
        this.voteItemMapper.getListByCdn(1, 2, "akwei",
                System.currentTimeMillis() - 1000000,
                System.currentTimeMillis(), 0, 10);
    }

    @Test
    public void select2() throws Exception {
        List<VoteItem> list = this.voteItemMapper.getListByCdn2("akwei", 0, 2);
        Assert.assertNotNull(list);
    }

    @Test
    public void count() {
        int count = this.voteItemMapper.count(VoteItem.class, 1, "hhhhhhhhh1532806847525");
        Assert.assertEquals(1, count);
        System.out.println(count);
    }

}
