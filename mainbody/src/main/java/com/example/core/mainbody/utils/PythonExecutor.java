package com.example.core.mainbody.utils;

import com.jcraft.jsch.*;

import java.util.Properties;

public class PythonExecutor {

    public static boolean exec(String host,int port,String user,String password, String localScript){
        // 远程服务器上存放脚本的路径
        String remoteScript = "/app/project/StrategyYt/trading_bot.py";

        Session session = null;
        ChannelSftp sftpChannel = null;
        ChannelExec execChannel = null;
        boolean bl = false;
        try {
            // 1. 创建JSch对象
            JSch jsch = new JSch();

            // 2. 获取会话并配置
            session = jsch.getSession(user, host, port);
            session.setPassword(password);

            // 跳过主机密钥检查（仅用于测试，生产环境建议配置known_hosts）
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            // 3. 连接会话
            session.connect(30000); // 超时30秒

            // 4. 通过SFTP上传文件
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            sftpChannel.put(localScript, remoteScript); // 上传文件
            bl = true;

        }catch (Exception e) {
            e.printStackTrace();
            bl = false;
        } finally {
            // 6. 关闭通道和会话
            if (sftpChannel != null && sftpChannel.isConnected()) {
                sftpChannel.disconnect();
            }
            if (execChannel != null && execChannel.isConnected()) {
                execChannel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
            return bl;
        }
    }

}
