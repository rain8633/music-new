package com.example.demo.service;

import com.example.demo.model.Consumer;

import java.text.ParseException;
import java.util.List;

public interface ConsumerService  {

    boolean addUser(Consumer consumer);

    boolean updateUserMsg(Consumer consumer);

    boolean updateUserAvator(Consumer consumer);

    Object activation(String email, String validateCode) throws ParseException ;

    boolean existUser(String username);

    boolean veritypasswd(String username, String password);

    boolean deleteUser(Integer id);

    Consumer findByEmail(String email);

    Consumer findByUsername(String username);

    List<Consumer> allUser();

    List<Consumer> userOfId(Integer id);

    List<Consumer> loginStatus(String username);

    Consumer getConsumerById(Integer conId);
}
