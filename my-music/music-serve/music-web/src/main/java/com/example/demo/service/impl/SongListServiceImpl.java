package com.example.demo.service.impl;

import com.example.demo.dao.SongListMapper;
import com.example.demo.model.SongList;
import com.example.demo.service.SongListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongListServiceImpl implements SongListService {

    @Autowired
    private SongListMapper songListMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean updateSongListMsg(SongList songList) {
        return songListMapper.updateSongListMsg(songList) >0 ?true:false;
    }

    @Override
    public boolean deleteSongList(Integer id) {
        return songListMapper.deleteSongList(id) >0 ?true:false;
    }

    @Override
    public List<SongList> allSongList()
    {

        List<SongList> songList=(List<SongList>) redisTemplate.opsForValue().get("songList");
        if(songList==null){
             songList=songListMapper.allSongList();
            redisTemplate.opsForValue().set("songList",songList);
            return songList;
        }
        return songList;
    }

    @Override
    public List<SongList> likeTitle(String title)
    {
        return songListMapper.likeTitle(title);
    }

    @Override
    public List<SongList> likeStyle(String style)
    {
        return songListMapper.likeStyle(style);
    }

    @Override
    public List<SongList> songListOfTitle(String title)
    {
        return songListMapper.songListOfTitle(title);
    }

    @Override
    public boolean addSongList(SongList songList)
    {
        return songListMapper.insertSelective(songList) > 0?true:false;
    }

    @Override
    public boolean updateSongListImg(SongList songList) {

        return songListMapper.updateSongListImg(songList) >0 ?true:false;
    }
}
