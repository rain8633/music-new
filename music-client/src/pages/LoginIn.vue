<template>
  <div class="login-in">
    <login-logo />
    <div class="login">
      <div class="login-head">
        <span>帐号登录</span>
      </div>
      <el-form
        :model="loginForm"
        status-icon
        :rules="rules"
        ref="loginForm"
        class="demo-ruleForm"
      >
        <el-form-item prop="username">
          <el-input
            placeholder="用户名"
            v-model="loginForm.username"
          ></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            type="password"
            placeholder="密码"
            v-model="loginForm.password"
            @keyup.enter.native="loginIn"
          ></el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-input
            size="normal"
            type="text"
            v-model="loginForm.code"
            auto-complete="off"
            placeholder="点击图片更换验证码"
            @keydown.enter.native="loginIn"
            style="width: 180px"
          ></el-input>
          <img
            :src="vcUrl"
            @click="updateVerifyCode"
            alt=""
            style="cursor: pointer"
          />
        </el-form-item>
        <div class="login-btn">
          <el-button @click="goSignUp()">注册</el-button>
          <el-button type="primary" @click="handleleLoginIn">登录</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { mixin } from "../mixins";
import LoginLogo from "../components/LoginLogo";
import { loginIn } from "../api/index";

export default {
  name: "login-in",
  components: {
    LoginLogo,
  },
  mixins: [mixin],
  data: function () {
    let validateName = (rule, value, callback) => {
      if (!value) {
        return callback(new Error("用户名不能为空"));
      } else {
        callback();
      }
    };
    let validatePassword = (rule, value, callback) => {
      if (value === " ") {
        callback(new Error("请输入密码"));
      } else {
        callback();
      }
    };
    return {
      vcUrl: "http://localhost:8888/verifyCode?time=" + new Date(),
      loginForm: {
        // 登录用户名密码
        username: "",
        password: "",
        code: "",
      },
      rules: {
        username: [
          { validator: validateName, message: "请输入用户名", trigger: "blur" },
        ],
        password: [
          {
            validator: validatePassword,
            message: "请输入密码",
            trigger: "blur",
          },
        ],
        code: [{ required: true, message: "请输入验证码", trigger: "blur" }],
      },
    };
  },
  mounted() {
    this.changeIndex("登录");
  },
  methods: {
    updateVerifyCode() {
      this.vcUrl = "http://localhost:8888/verifyCode?time=" + new Date();
    },
    changeIndex(value) {
      this.$store.commit("setActiveName", value);
    },
    handleleLoginIn() {
      let _this = this;
      let params = new URLSearchParams();
      params.append("username", this.loginForm.username);
      params.append("password", this.loginForm.password);
      params.append("code", this.loginForm.code);
      loginIn(params)
        .then((res) => {
          // console.log('-----------获取登录信息---------------'){}
          if (res.code === 3) {
            _this.notify("验证码错误", "error");
            this.vcUrl = "http://localhost:8888/verifyCode?time=" + new Date();
          } else if (res.code === 1) {
            _this.$message({
              message: "登录成功",
              type: "success",
            });
            _this.setUserMsg(res.userMsg[0]);
            _this.$store.commit("setLoginIn", true);
            setTimeout(function () {
              _this.changeIndex("首页");
              _this.$router.push({ path: "/" });
              _this.$router.go(0);
            }, 2000);
          } else {
            _this.notify(res.msg, "error");
            this.vcUrl = "http://localhost:8888/verifyCode?time=" + new Date();
          }
        })
        .catch((failResponse) => {});
    },
    setUserMsg(item) {
      this.$store.commit("setUserId", item.id);
      this.$store.commit("setUsername", item.username);
      this.$store.commit("setAvator", item.avator);
    },
    goSignUp() {
      this.$router.push({ path: "/sign-up" });
    },
  },
};
</script>

<style lang="scss" scoped>
@import "../assets/css/login-in.scss";
</style>
