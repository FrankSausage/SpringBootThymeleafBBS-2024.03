package com.example.abbs.dao;

import com.example.abbs.entity.Reply;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReplyDao {

    @Select("select reply.*, users.uname from reply join users on reply.uid=users.uid" +
            " where reply.bid=#{bid}")
    List<Reply> getReplyList(int bid);

    @Insert("insert into reply values(default, #{comment}, default, #{uid}, #{bid}, #{isMine})")
    void insertReply(Reply reply);
}
