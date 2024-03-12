package com.example.abbs.service;

import com.example.abbs.dao.ReplyDao;
import com.example.abbs.entity.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReplyServiceImpl implements ReplyService{
    @Autowired private ReplyDao rDao;

    @Override
    public List<Reply> getReplyList(int bid) {
        return rDao.getReplyList(bid);
    }

    @Override
    public void insertReply(Reply reply) {
        rDao.insertReply(reply);
    }
}
