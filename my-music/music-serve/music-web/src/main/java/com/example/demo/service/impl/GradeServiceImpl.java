package com.example.demo.service.impl;

import com.example.demo.dao.GradeMapper;
import com.example.demo.model.Grade;
import com.example.demo.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GradeServiceImpl implements GradeService {

    @Autowired
    private GradeMapper gradeMapper;

    @Override
    public int rankOfSongListId(Long songListId) {
        return gradeMapper.selectScoreSum(songListId) / gradeMapper.selectRankNum(songListId);
    }

    @Override
    public boolean addRank(Grade grade) {

        return gradeMapper.insertSelective(grade) > 0 ? true:false;
    }
}
