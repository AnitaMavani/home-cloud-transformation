<template>
    <div class="login-wrapper">
    <div :class="['login-container', type == 'login' ? 'active' : '']">
        <div class="switch-wrapper row aCenter jCenter">
          <div class="btn-wrapper row aCenter jCenter">
            <div
              v-if="type == 'login'"
              class="txt row aCenter jCenter"
              @click="type = 'register'"
            >
              Register
            </div>
            <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
            <div v-else class="txt row aCenter jCenter" @click="type = 'login'">
              Login
            </div>
          </div>
        </div>
        <div :class="['outerBox', type == 'login' ? 'active' : '']">
          <div class="container row aCenter jCenter">
            <div class="login" v-show="type == 'login'">
              <el-form
                :model="loginUser"
                status-icon
                :rules="loginRules"
                ref="loginForm"
              >
                <div class="title">Login</div>
                <el-form-item prop="email">
                  <el-input
                    type="text"
                    prefix-icon="el-icon-user"
                    placeholder="Enter Email"
                    v-model="loginUser.email"
                  />
                </el-form-item>
                <el-form-item prop="password">
                  <el-input
                    type="password"
                    prefix-icon="el-icon-view"
                    placeholder="Enter Password"
                    v-model="loginUser.password"
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" class="btn-login" @click="login"
                    >Login</el-button
                  >
                </el-form-item>
              </el-form>
            </div>
            <div class="register" v-show="type == 'register'">
              <el-form
                :model="registerUser"
                status-icon
                :rules="registerRules"
                ref="registerForm"
              >
                <div class="title">Register</div>
                <el-form-item prop="email">
                  <el-input
                    type="text"
                    prefix-icon="el-icon-user"
                    placeholder="Enter Email"
                    v-model="registerUser.email"
                  />
                </el-form-item>
                <el-form-item prop="nickName">
                  <el-input
                    type="text"
                    prefix-icon="el-icon-message"
                    placeholder="Enter Nickname"
                    v-model="registerUser.nickName"
                  />
                
                </el-form-item>
                <el-form-item prop="password">
                  <el-input
                    type="password"
                    prefix-icon="el-icon-view"
                    placeholder="Enter Password"
                    v-model="registerUser.password"
                  />
                </el-form-item>
                <el-form-item prop="passwordConfirm">
                    <el-input
                     type="password"
                     prefix-icon="el-icon-view"
                     placeholder="Confirm Password"
                     v-model="registerUser.passwordConfirm"
                    />
                </el-form-item>

                <el-form-item>
                  <el-button type="primary" class="btn-login" @click="register"
                    >Register</el-button
                  >
                </el-form-item>
              </el-form>
            </div>
          </div>
        </div>
      </div>
    </div>
</template>

   
<script scoped>
import CryptoJS from 'crypto-js';
import axios from 'axios';

export default {
    name: "login",

    data() {
        return {
          errorMessage: '',  
          type: "login",
            loginUser: {
                email: "",
                password: "",
            },
            loginRules: {
                email: [
                    { required: true, message: "Please enter email address", trigger: "blur" },
                    { validator: this.validateEmail, trigger: "blur" },
                ],
                password: [{ required: true, message: "Please enter password", trigger: "blur" }],
            },
            registerUser: {
                nickName: "",
                password: "",
                email: "",
                passwordConfirm: "" 
            },
            registerRules: {
                email: [
                    { required: true, message: "Please enter email address", trigger: "blur" },
                    { validator: this.validateEmail, trigger: "blur" },
                ],
                nickName: [{ required: true, message: "Please enter nickname", trigger: "blur" }],
                password: [ { required: true, message: "Please enter password", trigger: "blur" },
    { validator: this.validatePasswordFormat, trigger: "blur" },],
                passwordConfirm: [
                { required: true, message: "Please enter password", trigger: "blur" },
    { validator: this.validatePasswordFormat, trigger: "blur" },
                    { validator: this.validatePasswordConfirm, trigger: "blur" },
                ],
            },
            userData: {}  // Used to store user information
        };
    },
    methods: {
      validatePasswordFormat(rule, value, callback) {
    const passwordPattern = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,18}$/;
    if (!passwordPattern.test(value)) {
        callback(new Error('8-18 chars, with numbers & letters'));
    } else {
        callback();
    }
},

      simulateLoginClickAndNavigate() {
        this.loginClickHandler();  // Simulate clicking the login button logic
        this.$router.push('/');  // Navigate to the root path
    },

      loginClickHandler() {
        this.type = 'login';
         },
        validatePasswordConfirm(rule, value, callback) {
            if (value === '') {
                callback(new Error('Please input the password again'));
            } else if (value !== this.registerUser.password) {
                callback(new Error('The two passwords do not match!'));
            } else {
                callback();
            }
        },
        validateEmail(rule, value, callback) {
            const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
            if (!emailPattern.test(value)) {
                callback(new Error('Please enter a valid email address'));
            } else {
                callback();
            }
        },
        async login() {
            if (!this.loginUser.email || !this.loginUser.password) {
                alert("Email or password cannot be empty!");
                return;
            }

            //MD5 hash password
            const hashedPassword = CryptoJS.MD5(this.loginUser.password).toString();
            const loginURL = `/api/login`;

            try {
                const response = await axios.post(loginURL, null, {
                    params: {
                        email: this.loginUser.email,
                        password: hashedPassword
                    }
                });

                if (response.data.status === "success") {
                  this.$store.dispatch('setUserData', response.data.data); // Save all user information
                    this.$router.push('/main');
                } else {
                    alert('Login failed:' + response.data.info);
                }
            } catch (error) {
                console.error("Login error:", error);
                alert('An error occurred during the login process. Please try again later');
            }
        },
        async register() {
    this.$refs.registerForm.validate(async valid => {
        if (valid) {
            const registerURL = `/api/register`;

            try {
                const response = await axios.post(registerURL, null, {
                    params: {
                        email: this.registerUser.email,
                        nickName: this.registerUser.nickName,
                        password: this.registerUser.password
                    }
                });

                if (response.data.status === "success") {
                    alert('Registration Successful!');
                    this.simulateLoginClickAndNavigate();
                } else {
                    alert('login has failed：' + response.data.info);
                }
            } catch (error) {
                console.error("Registration error:", error);
                alert('An error occurred during the registration process. Please try again later');
            }
        } else {
            alert('Please ensure all fields are filled out correctly.');
            return false;
        }
    });
}
},
};
</script>

   
  <style  scoped>
  .login-wrapper {
    background-image: url(../assets/bg.png);
    background-size: 100% 100%;
    /*background-repeat: no-repeat;
    background-position: center center;
    background-size: cover;
    background-position: center;
    background-color: #000;*/
    display: flex;
    width: 98vw;
    height: 98vh;
    overflow: hidden;
    margin: auto;
  }
  .login-container {
    background-color: rgba(255, 255, 255, 0);
    position: relative;
    width: 500px;
    height: 350px;
    margin: auto;
    overflow: hidden;
    border-radius: 5px;
    box-shadow: 0 0 10px 5px #ddd;
   /* backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);  */
  }
  
 

  .switch-wrapper {
    position: absolute;
    z-index: 99;
    left: 0;
    overflow: hidden;
    width: 32%;
    height: 100%;
    transition: transform 1s ease-in-out;
  }
  .switch-wrapper::after {
    content: "";
    display: block;
    background-color: rgba(255, 255, 255, 0.5);  /* semi-transparent white */
    
    background-size: 1000px 550px;
    background-position: top left;
    width: 100%;
    height: 100%;
    overflow: hidden;
    transition: all 1s ease-in-out;
  }
  .active .switch-wrapper {
    transform: translateX(calc(500px - 100%));
  }
  .active .switch-wrapper::after {
    background-position: top right;
  }
  .txt {
    width: 100%;
    height: 100%;
    transition: all 1s ease-in-out;
  }
  .top-wrapper {
    position: absolute;
    width: 260px;
    height: 168px;
    top: 50px;
    background-color: rgba(255, 255, 255, 0.7);
    padding: 10px;
    border-radius: 5px;
  }
  .btn-wrapper {
    position: absolute;
    width: 100px;
    height: 36px;
    color: #fffffe;
    background-color: #6689e2;
    font-size: 15px;
    border-radius: 30px;
    cursor: pointer;
    flex-wrap: wrap;
    overflow: hidden;
  }
   
  .outerBox {
    position: absolute;
    z-index: 3;
    left: 32%;
    overflow: hidden;
    width: 68%;
    height: 100%;
    transition: all 1s ease-in-out;
  }
  .container {
    width: 100%;
    height: 100%;
    background-color: rgba(255, 255, 255, 0.2);  /* semi-transparent white */
    backdrop-filter: blur(10px);
}

  .active .outerBox {
    transform: translateX(calc(-500px + 100%));
  }
   
  .ld {
    width: 200px;
    height: 40px;
    position: absolute;
    right: 16px;
    top: 16px;
  }
  .title {
    font-size: 36px;
    line-height: 1.5;
    text-align: center;
    margin-bottom: 10px;
    color: #666;
  }
  /* Targeting the el-form-item component */
.el-form-item {
    margin-bottom: 20px;  /* Adjust this value for desired spacing */
}

  .btn-login {
    width: 100%;
  }
   
  .row {
    display: flex;
    display: -webkit-flex;
    flex-direction: row;
  }
  .column {
    display: flex;
    display: -webkit-flex;
    flex-direction: column;
  }
  .aCenter {
    align-items: center;
  }
  .jCenter {
    justify-content: center;
  }
  

  @keyframes drift {
    0% { transform: translateY(0%); }
    100% { transform: translateY(100%); }
  }
 
  </style>