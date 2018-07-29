package info.akwei.ohmybatis.example.entity;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_vote_item")
public class VoteItem {

    @Id
    private int voteItemId;

    private String title;

    private long createTime;

    public int getVoteItemId() {
        return voteItemId;
    }

    public void setVoteItemId(int voteItemId) {
        this.voteItemId = voteItemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

}
