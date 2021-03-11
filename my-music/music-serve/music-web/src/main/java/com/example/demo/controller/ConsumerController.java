package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.deploy.ServerConfig;
import com.example.demo.model.Consumer;
import com.example.demo.model.MailConstants;
import com.example.demo.model.MailSendLog;
import com.example.demo.service.IEmailService;
import com.example.demo.service.impl.ConsumerServiceImpl;
import com.example.demo.service.impl.MailSendLogService;
import com.example.demo.utils.MD5Util;
import com.example.demo.utils.VerificationCode;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.TemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Controller
public class ConsumerController {

    @Autowired
    private ConsumerServiceImpl consumerService;

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private IEmailService iEmailService;

    @Autowired
    private MailSendLogService mailSendLogService;

    @Configuration
    public class MyPicConfig implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
//            registry.addResourceHandler("/avatorImages/**").addResourceLocations("file:/Users/hongweiyin/Documents/github-workspace/music-website/music-server/avatorImages/");
            registry.addResourceHandler("/avatorImages/**").addResourceLocations("file:I:\\源码\\springboot+vue\\music-website-master\\music-website-master\\music-server\\avatorImages\\");
        }
    }

//    添加用户
    @ResponseBody
    @RequestMapping(value = "/user/add", method = RequestMethod.POST)
    public Object addUser(HttpServletRequest req){
        JSONObject jsonObject = new JSONObject();
        String username = req.getParameter("username").trim();
        String password = req.getParameter("password").trim();
        String email = req.getParameter("email").trim();
        String sex = req.getParameter("sex").trim();
        String phone_num = req.getParameter("phone_num").trim();
        String birth = req.getParameter("birth").trim();
        String introduction = req.getParameter("introduction").trim();
        String location = req.getParameter("location").trim();
        String avator = req.getParameter("avator").trim();
        //生成盐和加盐密码
        String salt = MD5Util.md5Encrypt32Lower(email);
        // 使用SimpleHash类对原始密码进行加密
        String Pw = new SimpleHash("MD5", password,salt, 1024).toHex();
         Consumer consumer1=consumerService.findByUsername(username);
         Consumer consumer2=consumerService.findByEmail(email);
        if(consumer1!=null){
            jsonObject.put("code", 0);
            jsonObject.put("msg", "用户名已存在");
            return jsonObject;
        }else if(consumer2!=null) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "邮箱已被注册!");
            return jsonObject;
         } else {
            Consumer consumer = new Consumer();
            //生成激活码
            String validateCode = MD5Util.md5Encrypt32Upper(email);
            consumer.setValidateCode(validateCode);
            consumer.setSalt(salt);
            consumer.setEmailstatus(0);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date myBirth = new Date();
            try {
                myBirth = dateFormat.parse(birth);
            } catch (Exception e) {
                e.printStackTrace();
            }
            consumer.setUsername(username);
            consumer.setPassword(Pw);
            consumer.setSex(new Byte(sex));
            if (phone_num == "") {
                consumer.setPhoneNum(null);
            } else {
                consumer.setPhoneNum(phone_num);
            }

            if (email == "") {
                consumer.setEmail(null);
            } else {
                consumer.setEmail(email);
            }
            consumer.setBirth(myBirth);
            consumer.setIntroduction(introduction);
            consumer.setLocation(location);
            consumer.setAvator(avator);
            consumer.setCreateTime(new Date());
            consumer.setUpdateTime(new Date());

            boolean res = consumerService.addUser(consumer);

            if (res) {
//                //组装发送邮件参数
//                String title = "账户激活";
//                Context context = new Context();
//                //组装激活地址
//                String url = serverConfig.getUrl().concat("/activation").concat("?email=")
//                        .concat(email).concat("&validateCode=").concat(validateCode);
//
//                context.setVariable("url", url);
//                String emailContent = templateEngine.process("email/email", context);
//
//                //发送邮件
//                iEmailService.sendMail(email, title, emailContent);

                Consumer con = consumerService.findByUsername(consumer.getUsername());
                //生成消息的唯一id
                String msgId = UUID.randomUUID().toString();
                MailSendLog mailSendLog = new MailSendLog();
                mailSendLog.setMsgId(msgId);
                mailSendLog.setCreateTime(new Date());
                mailSendLog.setExchange(MailConstants.MAIL_EXCHANGE_NAME);
                mailSendLog.setRouteKey(MailConstants.MAIL_ROUTING_KEY_NAME);
                mailSendLog.setConId(con.getId());
                mailSendLog.setTryTime(new Date(System.currentTimeMillis() + 1000 * 60 * MailConstants.MSG_TIMEOUT));
                mailSendLogService.insert(mailSendLog);
                rabbitTemplate.convertAndSend(MailConstants.MAIL_EXCHANGE_NAME, MailConstants.MAIL_ROUTING_KEY_NAME, con, new CorrelationData(msgId));


                jsonObject.put("code", 1);
                jsonObject.put("msg", "success");
                jsonObject.put("email", email);
                return jsonObject;
            } else {
                jsonObject.put("code", 0);
                jsonObject.put("msg", "注册失败");
                return jsonObject;
            }
        }
    }

    /**
     * 激活用户状态
     *
     * @param email
     * @param validateCode
     * @return
     * @throws ParseException
     */
    @RequestMapping(value = "/activation", method = RequestMethod.GET)
    public String activation(String email, String validateCode, Model model) throws ParseException {

        Object baseResponse = consumerService.activation(email, validateCode);

        String jsonStr=JSONObject.toJSONString(baseResponse);

      JSONObject jsonObject=JSONObject.parseObject(jsonStr);
        model.addAttribute("url", "http://localhost:8080");

        if (jsonObject.containsKey("code")) {
            return "email/dmail";
        } else {
            model.addAttribute("error",jsonObject.getString("msg"));
            return "email/error";
        }

    }

    //    判断是否登录成功
    @ResponseBody
    @RequestMapping(value = "/user/login/status", method = RequestMethod.POST)
    public Object loginStatus(HttpServletRequest req, HttpSession session){

        JSONObject jsonObject = new JSONObject();
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        String code=req.getParameter("code");
        String verify_code = (String) req.getSession().getAttribute("verify_code");
        if(code == null || verify_code == null || "".equals(code) || !verify_code.toLowerCase().equals(code.toLowerCase())){
             jsonObject.put("msg","验证码错误");
             jsonObject.put("code",3);
             return jsonObject;
        }else {
//        System.out.println(username+"  "+password);
            //登录验证
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            Subject subject = SecurityUtils.getSubject();
            Consumer consumer=consumerService.findByUsername(username);
            try {
                subject.login(token);
            } catch (AuthenticationException e) {
                jsonObject.put("code", 0);
                jsonObject.put("msg","用户名或密码错误");
                return jsonObject;
            }
            if(consumer.getEmailstatus() ==0){
                jsonObject.put("code", 0);
                jsonObject.put("msg","邮箱未激活!");
                return jsonObject;
           }
                jsonObject.put("code", 1);
                jsonObject.put("msg", "登录成功");
                jsonObject.put("userMsg", consumerService.loginStatus(username));
                session.setAttribute("username", username);
                return jsonObject;
        }
    }

    @ResponseBody
    @GetMapping("/verifyCode")
    public void verifyCode(HttpServletRequest request, HttpServletResponse resp) throws IOException {
        VerificationCode code = new VerificationCode();
        BufferedImage image = code.getImage();
        String text = code.getText();
        HttpSession session = request.getSession(true);
        session.setAttribute("verify_code", text);
        VerificationCode.output(image,resp.getOutputStream());
    }

//    返回所有用户
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @ResponseBody
    public Object allUser(){
        return consumerService.allUser();
    }

//    返回指定ID的用户
    @RequestMapping(value = "/user/detail", method = RequestMethod.GET)
    @ResponseBody
    public Object userOfId(HttpServletRequest req){
        String id = req.getParameter("id");
        return consumerService.userOfId(Integer.parseInt(id));
    }

//    删除用户
    @RequestMapping(value = "/user/delete", method = RequestMethod.GET)
    @ResponseBody
    public Object deleteUser(HttpServletRequest req){
        String id = req.getParameter("id");
        return consumerService.deleteUser(Integer.parseInt(id));
    }

//    更新用户信息
    @ResponseBody
    @RequestMapping(value = "/user/update", method = RequestMethod.POST)
    public Object updateUserMsg(HttpServletRequest req){
        JSONObject jsonObject = new JSONObject();
        String id = req.getParameter("id").trim();
        String username = req.getParameter("username").trim();
        String password = req.getParameter("password").trim();
        String sex = req.getParameter("sex").trim();
        String phone_num = req.getParameter("phone_num").trim();
        String email = req.getParameter("templates/email").trim();
        String birth = req.getParameter("birth").trim();
        String introduction = req.getParameter("introduction").trim();
        String location = req.getParameter("location").trim();
//        String avator = req.getParameter("avator").trim();
//        System.out.println(username+"  "+password+"  "+sex+"   "+phone_num+"     "+email+"      "+birth+"       "+introduction+"      "+location);

        if (username.equals("") || username == null){
            jsonObject.put("code", 0);
            jsonObject.put("msg", "用户名或密码错误");
            return jsonObject;
        }
        Consumer consumer = new Consumer();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date myBirth = new Date();
        try {
            myBirth = dateFormat.parse(birth);
        }catch (Exception e){
            e.printStackTrace();
        }
        consumer.setId(Integer.parseInt(id));
        consumer.setUsername(username);
        consumer.setPassword(password);
        consumer.setSex(new Byte(sex));
        consumer.setPhoneNum(phone_num);
        consumer.setEmail(email);
        consumer.setBirth(myBirth);
        consumer.setIntroduction(introduction);
        consumer.setLocation(location);
//        consumer.setAvator(avator);
        consumer.setUpdateTime(new Date());

        boolean res = consumerService.updateUserMsg(consumer);
        if (res){
            jsonObject.put("code", 1);
            jsonObject.put("msg", "修改成功");
            return jsonObject;
        }else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "修改失败");
            return jsonObject;
        }
    }

//    更新用户头像
    @ResponseBody
    @RequestMapping(value = "/user/avatar/update", method = RequestMethod.POST)
    public Object updateUserPic(@RequestParam("file") MultipartFile avatorFile, @RequestParam("id")int id){
        JSONObject jsonObject = new JSONObject();

        if (avatorFile.isEmpty()) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "文件上传失败！");
            return jsonObject;
        }
        String fileName = System.currentTimeMillis()+avatorFile.getOriginalFilename();
        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "avatorImages" ;
        File file1 = new File(filePath);
        if (!file1.exists()){
            file1.mkdir();
        }

        File dest = new File(filePath + System.getProperty("file.separator") + fileName);
        String storeAvatorPath = "/avatorImages/"+fileName;
        try {
            avatorFile.transferTo(dest);
            Consumer consumer = new Consumer();
            consumer.setId(id);
            consumer.setAvator(storeAvatorPath);
            boolean res = consumerService.updateUserAvator(consumer);
            if (res){
                jsonObject.put("code", 1);
                jsonObject.put("avator", storeAvatorPath);
                jsonObject.put("msg", "上传成功");
                return jsonObject;
            }else {
                jsonObject.put("code", 0);
                jsonObject.put("msg", "上传失败");
                return jsonObject;
            }
        }catch (IOException e){
            jsonObject.put("code", 0);
            jsonObject.put("msg", "上传失败"+e.getMessage());
            return jsonObject;
        }finally {
            return jsonObject;
        }
    }
}
