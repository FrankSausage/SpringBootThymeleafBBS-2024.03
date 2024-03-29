package com.example.abbs.service;

import com.example.abbs.dao.LikeDao;
import com.example.abbs.entity.Like;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeServiceImpl implements LikeService{
    @Autowired private LikeDao lDao;
    @Override
    public Like getLike(String uid, int bid) {
        return lDao.getLike(uid, bid);
    }

    @Override
    public List<Like> getLikeList(int bid) {
        return lDao.getLikeList(bid);
    }

    @Override
    public void insertLike(Like like) {
        lDao.insertLike(like);
    }

    @Override
    public int toggleLike(Like like) {
        like = lDao.getLike(like.getUid(), like.getBid());
        int value = like.getValue() == 0 ? 1 : 0;
        like.setValue(value);
        lDao.toggleLike(like);
        return value;
    }

    @Override
    public int getLikeCount(int bid) {
        List<Like> list = lDao.getLikeList(bid);
        int count = 0;
        for (Like like: list){
            count += like.getValue();
        }
        return count;
    }
}
