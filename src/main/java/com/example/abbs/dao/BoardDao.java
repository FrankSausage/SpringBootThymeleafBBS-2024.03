package com.example.abbs.dao;

import com.example.abbs.entity.Board;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardDao {
    @Select("select board.*, users.uname from board" +
            " join users on board.uid=users.uid" +
            " where board.bid=${bid}")
    Board getBoard(int bid);

    @Select("select count(bid) from board" +
            " join users on board.uid=users.uid" +
            " where board.isDeleted=0 and ${field} like #{query}")
    int getBoardCount(String field, String query);

    @Select("select board.*, users.uname from board" +
            " join users on board.uid=users.uid" +
            " where board.isDeleted=0 and ${field} like #{query}" +
            " order by modTime desc" +
            " LIMIT #{count} offset #{offset}")
    List<Board> getBoardList(int count, int offset, String field, String query);

    @Insert("insert into board values(default, #{title}, #{content}, #{uid}," +
            " default, default, default, default, default ,#{files})")
    void insertBoard(Board board);

    @Update("update board set title=#{title}, content=#{content}, modTime=now()," +
            " files=#{files} where bid=#{bid}")
    void updateBoard(Board board);

    @Delete("update board set board.isDeleted=1 where bid=#{bid}")
    void deleteBoard(int bid);

    @Update("update board set ${field}=${field}+1 where bid=#{bid}")
    void increaseCount(String field, int bid);

    @Update("update board set likeCount=#{count} where bid=#{bid}")
    void updateLikeCount(int bid, int count);
}
