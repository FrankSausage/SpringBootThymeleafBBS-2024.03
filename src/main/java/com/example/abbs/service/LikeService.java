package com.example.abbs.service;

import com.example.abbs.entity.Like;

import java.util.List;

public interface LikeService {

    Like getLike(String uid, int bid);

    List<Like> getLikeList(int bid);

    void insertLike(Like like);

    void toggleLike(Like like);         // value가 0이면 1로 , 1이면 0으로 변경

    int getLikeCount(int bid);
}
