package com.example.demo.dao;

import com.example.demo.model.Grade;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeMapper {

    int insert(Grade record);

    int insertSelective(Grade record);

    /**
     * 查总分
     * @param songListId
     * @return
     */
    int selectScoreSum(Long songListId);

    /**
     * 查总评分人数
     * @param songListId
     * @return
     */
    int selectRankNum(Long songListId);
}
