<h1 align="center">music-website</h1>

<br/>

<p align="center">
  <a href=""><img alt="license" src="https://img.shields.io/github/license/Yin-Hongwei/music-website"></a>
</p>
<h1 align="center">声明</h1>



该项目是我修改的一位大佬的音乐播放网站。后端整合了redis+shiro+RabbitMQ,实现注册时发送邮箱激活的功能

之前发送邮件时，一旦重启服务器，邮件就会重新启动定时任务，发送指定次数（还没找到啥原因）之前我的github地址:https://github.com/rain8633/music.git

修改之后后端采用模块化,把邮件服务的模块单独抽离出来启动

<br/>

## 项目说明

本音乐网站的开发主要利用 VUE 框架开发前台和后台，后端接口用 Spring Boot + MyBatis 来实现，数据库使用的是 MySQL。实现思路可以看[这里](https://yin-hongwei.github.io/2019/03/04/music/#more)。

<br/>

## 项目截图

> 前台截图预览

[![6Nw43Q.png](https://s3.ax1x.com/2021/03/11/6Nw43Q.png)](https://imgtu.com/i/6Nw43Q)

<br/>

[![6Nwh9g.png](https://s3.ax1x.com/2021/03/11/6Nwh9g.png)](https://imgtu.com/i/6Nwh9g)



[![6NwRN8.png](https://s3.ax1x.com/2021/03/11/6NwRN8.png)](https://imgtu.com/i/6NwRN8)



[![6N0gM9.png](https://s3.ax1x.com/2021/03/11/6N0gM9.png)](https://imgtu.com/i/6N0gM9)



[![6NwIjs.png](https://s3.ax1x.com/2021/03/11/6NwIjs.png)](https://imgtu.com/i/6NwIjs)







![](https://tva1.sinaimg.cn/large/007S8ZIlly1geec0qtdxrj31c00u07wj.jpg)<br/>

![](https://tva1.sinaimg.cn/large/007S8ZIlly1geec19x0e6j31c00u0npe.jpg)<br/>

![](https://tva1.sinaimg.cn/large/007S8ZIlly1geec1nmbt4j31c00u0hcf.jpg)<br/>

![](https://tva1.sinaimg.cn/large/007S8ZIlly1geec1yc0gkj31c00u0kjm.jpg)<br/>

![](https://tva1.sinaimg.cn/large/007S8ZIlly1geec29vvdtj31c00u0nok.jpg)<br/>

![](https://tva1.sinaimg.cn/large/007S8ZIlly1geec2ixqk1j31c00u0qf8.jpg)

<br/>

![](https://tva1.sinaimg.cn/large/007S8ZIlly1geec31i06gj31c00u0wtw.jpg)<br/>

![](https://tva1.sinaimg.cn/large/007S8ZIlly1geec3ozxt9j31c00u0qbv.jpg)<br/>

![](https://tva1.sinaimg.cn/large/007S8ZIlly1geec41r7onj31c00u047y.jpg)<br/>

> 后台截图预览

![](https://tva1.sinaimg.cn/large/006tNbRwly1g9hhhu4n7tj31c00u04qq.jpg)<br/>

![](https://tva1.sinaimg.cn/large/00831rSTly1gdj8jf3uusj31c00u0n5b.jpg)<br/>

![](https://tva1.sinaimg.cn/large/00831rSTly1gdie89mujrj31c00u07kx.jpg)<br/>

![](https://tva1.sinaimg.cn/large/00831rSTly1gdie8sox6uj31c00u01gb.jpg)<br/>

![](https://tva1.sinaimg.cn/large/00831rSTly1gdie9beckpj31c00u0qh9.jpg)<br/>

![](https://tva1.sinaimg.cn/large/00831rSTly1gdie9qq7yhj31c00u0ttq.jpg)<br/>

## 功能

- 音乐播放
- 用户登录注册
- 用户信息编辑、头像修改
- 歌曲、歌单搜索
- 歌单打分
- 歌单、歌曲评论
- 歌单列表、歌手列表分页显示
- 歌词同步显示
- 音乐收藏、下载、拖动控制
- 后台对用户、歌曲、歌手、歌单信息的管理

<br/>

## 技术栈

#### 后端

SpringBoot + MyBatis

#### 前端

Vue + Vue-Router + Vuex + Axios +  ElementUI

<br/>

## 安装

#### 1、下载项目到本地

```
git clone https://github.com/rain8633/music-new.git
```

#### 2、下载数据库中记录的资源

去【链接: https://pan.baidu.com/s/1Qv0ohAIPeTthPK_CDwpfWg 提取码: gwa4 】下载网站依赖的歌曲及图片，将 data 夹里的文件直接放到 music-server的music-web下 文件夹下。

​                                                                  [![6Nw5cj.png](https://s3.ax1x.com/2021/03/11/6Nw5cj.png)](https://imgtu.com/i/6Nw5cj)



#### 3、修改
1）数据库：将sql文件夹中的 tp_music.sql 文件导入数据库。

2）music-server：启动后端服务之前，有一些地方需要修改，先去 /music-website/music-server/src/main/resources 这个目录下的文件里修改自己的 spring.datasource.username 和 spring.datasource.password，并且修改下圈出来的的文件中 MyPicConfig 类下的 addResourceLocations方法中的路径，否则资源加载不了。（Mac 和 win 下路径有些差异，如图是 Mac 上的路径，win 上需要在 file: 后标明是哪个盘，例如："file:C:\\\user\\\XXX\\\\"）



[![6NwTun.png](https://s3.ax1x.com/2021/03/11/6NwTun.png)](https://imgtu.com/i/6NwTun)

#### 4、启动项目

my-music 是本项目的后端，mail-serve和music-serve是两个微服务模块，用于监听前端发来的请求，提供接口。music-client 和 music-manage 都是本项目的前端部分，前者是前台，后者是后台。运行时后端必须启动，两个前端项目可以都启动，也可以只启动其中一个，他们是独立的。

启动项目前开启redis服务和RabbitMQ服务

然后进入 my-music文件夹，运行下面命令启动服务器 在mail-serve和music-serve两个服务下分别启动

```js
// 方法一
./mvnw spring-boot:run

// 方法二
mvn spring-boot:run // 前提装了 maven

//三
直接开启启动类（Maven）
```

进入 music-client 文件夹，运行下面命令启动前台项目

```js
npm install // 安装依赖

npm run dev // 启动前台项目
```

进入 music-manage 文件夹，运行下面命令启动后台管理项目

```js
npm install // 安装依赖

npm run dev // 启动后台管理项目
```

<br/>

###  原项目地址

```
https://github.com/Yin-Hongwei/music-website.git
```

