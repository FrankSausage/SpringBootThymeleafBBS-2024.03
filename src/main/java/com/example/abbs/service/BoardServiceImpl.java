package com.example.abbs.service;

import com.example.abbs.dao.BoardDao;
import com.example.abbs.entity.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardServiceImpl implements BoardService{
    @Autowired BoardDao bDao;

    @Override
    public Board getBoard(int bid) {
        return bDao.getBoard(bid);
    }

    @Override
    public int getBoardCount(String field, String query) {
        query = "%" + query + "%";
        return bDao.getBoardCount(field, query);
    }

    @Override
    public List<Board> getBoardList(int page, String field, String query) {
        int offset = (page - 1) * COUNT_PER_PAGE;
        query = "%" + query + "%";
        return bDao.getBoardList(COUNT_PER_PAGE, offset, field, query);
    }

    @Override
    public void insertBoard(Board board) {
        bDao.insertBoard(board);
    }

    @Override
    public void updateBoard(Board board) {
        bDao.updateBoard(board);
    }

    @Override
    public void deleteBoard(int bid) {
        bDao.deleteBoard(bid);
    }

    @Override
    public void increaseViewCount(int bid) {
        bDao.increaseCount("viewCount", bid);
    }

    @Override
    public void increaseReplyCount(int bid) {
        bDao.increaseCount("replyCount", bid);
    }

    @Override
    public void increaseLikeCount(int bid) {
        bDao.increaseCount("likeCount", bid);
    }
}
