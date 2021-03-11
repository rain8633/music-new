package com.example.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.dao.ConsumerMapper;
import com.example.demo.model.Consumer;
import com.example.demo.service.ConsumerService;
import com.example.demo.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ConsumerServiceImpl implements ConsumerService {

    @Autowired
    private ConsumerMapper consumerMapper;

    @Override
    public boolean addUser(Consumer consumer) {
        return consumerMapper.insertSelective(consumer) >0 ?true:false;
    }

    @Override
    public boolean updateUserMsg(Consumer consumer) {
        return consumerMapper.updateUserMsg(consumer) >0 ?true:false;
    }

    @Override
    public boolean updateUserAvator(Consumer consumer) {

        return consumerMapper.updateUserAvator(consumer) >0 ?true:false;
    }

    @Override
    public boolean existUser(String username) {
        return consumerMapper.existUsername(username)>0 ? true:false;
    }

    @Override
    public boolean veritypasswd(String username, String password) {

        return consumerMapper.verifyPassword(username, password)>0?true:false;
    }

//    删除用户
    @Override
    public boolean deleteUser(Integer id) {
        return consumerMapper.deleteUser(id) >0 ?true:false;
    }

    @Override
    public Consumer findByEmail(String email) {
        return consumerMapper.findByEmail(email);
    }

    @Override
    public Consumer findByUsername(String username) {
        return consumerMapper.findByUsername(username);
    }

    /**
     * 激活账户
     *
     * @param email
     * @param validateCode
     * @return
     * @throws ParseException
     */

    @Transactional
    @Override
    public Object activation(String email, String validateCode) throws ParseException {
        JSONObject jsonObject=new JSONObject();
        try {
            Consumer consumer=consumerMapper.findByEmail(email);
            if (consumer == null) {
                 jsonObject.put("msg","未查询到该邮箱，请核对信息！");
                 return jsonObject;
            }
            String nowDate = DateUtil.getCurrentDateToString();
            Date start = consumer.getCreateTime();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm:ss");

            Date end = sdf.parse(nowDate);
            long cha = end.getTime() - start.getTime();
            double result = cha * 1.0 / (1000 * 60 * 60);
            if (result > 48) {
                jsonObject.put("msg","激活邮件已过期，请重试！");
                return jsonObject;
            }
            if (!validateCode.equals(consumer.getValidateCode())) {
                jsonObject.put("msg","激活码错误，请联系管理员！");
                return jsonObject;
            }
            if (consumer.getEmailstatus() == 1) {
                jsonObject.put("msg","账户已被激活，请勿重复操作！");
                return jsonObject;

            }
            consumer.setEmailstatus(1);

            consumerMapper.updateUserMsg(consumer);
            jsonObject.put("code",1);
            jsonObject.put("msg","success");
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("msg","激活账户异常，请稍后重试！");
            return jsonObject;
        }
    }

    @Override
    public List<Consumer> allUser() {
        return consumerMapper.allUser();
    }

    @Override
    public List<Consumer> userOfId(Integer id) {

        return consumerMapper.userOfId(id);
    }

    @Override
    public List<Consumer> loginStatus(String username) {

        return consumerMapper.loginStatus(username);
    }

    @Override
    public Consumer getConsumerById(Integer conId) {
        return consumerMapper.getConsumerById(conId);
    }


}
