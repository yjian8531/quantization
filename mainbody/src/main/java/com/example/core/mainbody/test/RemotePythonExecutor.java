package com.example.core.mainbody.test;

import com.jcraft.jsch.*;
import java.io.*;
import java.util.Properties;

public class RemotePythonExecutor {

    public static void main(String[] args) {
        // 连接参数
        String host = "47.236.102.48";
        int port = 22;
        String user = "root";
        String password = "Caifu2026!"; // 或者使用密钥认证

        // 本地Python脚本路径
        String localScript = "D:/StrategyYt/untitled2/test.py";
        // 远程服务器上存放脚本的路径
        String remoteScript = "/app/project/StrategyYt/test.py";

        Session session = null;
        ChannelSftp sftpChannel = null;
        ChannelExec execChannel = null;

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
            System.out.println("Python脚本已上传至: " + remoteScript);

            // 5. 通过SSH执行远程命令（运行Python脚本）
            String command = "/app/project/StrategyYt/venv/bin/python3 " + remoteScript; // 如果python3可用，可改为python3
            execChannel = (ChannelExec) session.openChannel("exec");
            execChannel.setCommand(command);

            // 获取命令输出流
            InputStream in = execChannel.getInputStream();
            InputStream err = execChannel.getErrStream();

            execChannel.connect();

            // 读取输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(err));

            String line;
            System.out.println("脚本执行输出:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("错误输出:");
            while ((line = errReader.readLine()) != null) {
                System.err.println(line);
            }

            // 等待命令执行完成
            execChannel.disconnect();

        } catch (JSchException | SftpException | IOException e) {
            e.printStackTrace();
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
        }
    }
}
