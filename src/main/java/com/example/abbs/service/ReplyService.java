package com.example.abbs.service;

import com.example.abbs.entity.Reply;

import java.util.List;

public interface ReplyService {

    List<Reply> getReplyList(int bid);

    void insertReply(Reply reply);
}
