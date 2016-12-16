package com.cic.datacrawl.test;

import com.cic.datacrawl.runner.TaskRunnerClient;

public class TaskRunnerTest {
    public static void main(String[] args) {
        
        args = new String[10];
        //args[0] = "E:\\deploy\\Script\\login.test\\login_script_baidu.js";
        args[0] = "C:\\Users\\charles.chen\\Desktop\\weixin.sogou.com\\search_script_test.js";
        args[1] = "domain()";
        args[2] = "3000";
        args[3] = "1";
        args[4] = "1";
        args[5] = "0.0.0.0";
        args[6] = "123456789";
        args[7] = "1000";
        args[8] = "1";
        args[9] = "0";
        TaskRunnerClient.main(args);
        /*try {
            new BaiduLoginService("cicdata", "q1w2e3r4t5","","").login();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
