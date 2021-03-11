package com.example.demo.service;

import com.example.demo.model.Grade;

public interface GradeService {

    int rankOfSongListId(Long songListId);

    boolean addRank(Grade grade);
}
