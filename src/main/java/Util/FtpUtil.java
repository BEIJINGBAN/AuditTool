package Util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FtpUtil {

        // 连接超时时间
        protected static final int CONNECT_TIMEOUT = 20 * 1000;
        // 数据传输超时（60秒）
        protected static final int DATA_TIMEOUT = 60 * 1000;
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(FtpUtil.class);

    /**
         * 登录 FTP 服务器
         */
        public static FTPClient login(String host, int port, String username, String password) throws Exception {
            FTPClient ftpClient = new FTPClient();
            try {
                // 设置连接超时和字符集
                ftpClient.setConnectTimeout(CONNECT_TIMEOUT);
                ftpClient.setDataTimeout(DATA_TIMEOUT);
                ftpClient.setDefaultTimeout(CONNECT_TIMEOUT);
                ftpClient.setControlEncoding("UTF-8");

                // 连接服务器
                log.info("开始连接FTP服务器");
                ftpClient.connect(host, port);

                // 检查连接是否成功
                int replyCode = ftpClient.getReplyCode();
                if (!FTPReply.isPositiveCompletion(replyCode)) {
                    ftpClient.disconnect();
                    log.error("FTP服务器连接失败，响应码: " + replyCode);
                    throw new Exception("FTP服务器连接失败，响应码: " + replyCode);
                }

                // 登录
                boolean success = ftpClient.login(username, password);
                if (!success) {
                    log.error("FTP登录失败，用户名或密码错误");
                    throw new Exception("FTP登录失败，用户名或密码错误");
                }

                // 设置被动模式（推荐用于 NAT/防火墙环境）
                ftpClient.enterLocalPassiveMode();

                // 设置传输类型为二进制
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                return ftpClient;
            } catch (Exception e) {
                try {
                    if (ftpClient.isConnected()) {
                        ftpClient.disconnect();
                    }
                } catch (IOException ex) {
                    log.error("关闭连接时发生异常: " + ex.getMessage());
                    System.out.println("关闭连接时发生异常: " + ex.getMessage());
                }
                throw new Exception("连接FTP服务器异常: " + e.getMessage(), e);
            }
        }

        /**
         * 安全登出并关闭连接
         */
        public static void logout(FTPClient ftpClient) {
            if (ftpClient != null && ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                } catch (IOException e) {
                    log.error("FTP登出失败: " + e.getMessage());
                    System.out.println("FTP登出失败: " + e.getMessage());
                }
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    log.error("关闭FTP连接失败: " + e.getMessage());
                    System.out.println("关闭FTP连接失败: " + e.getMessage());
                }
            }
        }

        /**
         * 文件上传
         */
        public static boolean upload(String host, int port, String username, String password,
                                     String basePath, String ftpFileName, InputStream input) throws Exception {
            log.info("FTP开始上传: " + ftpFileName);
            System.out.println("FTP开始上传: " + ftpFileName);
            boolean success = false;
            FTPClient ftpClient = null;
            try {
                ftpClient = login(host, port, username, password);

                // 创建并切换到目标目录（递归）
                mkdirs(ftpClient, basePath);
//                ftpClient.changeWorkingDirectory(basePath);
                String remotePath = basePath + "/" + ftpFileName;
                // 执行上传
                success = ftpClient.storeFile(remotePath, input);
                log.info(ftpClient.getReplyString());
                if (!success) {
                    String replyString = ftpClient.getReplyString();
                    log.error("FTP上传失败，响应信息: " + replyString);
                    throw new Exception("FTP上传失败，响应信息: " + replyString);
                }

                log.info("文件上传完成: " + ftpFileName);
                System.out.println("文件上传完成: " + ftpFileName);
                return true;

            } finally {
                logout(ftpClient);
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        log.error("关闭输入流失败: " + e.getMessage());
                        System.out.println("关闭输入流失败: " + e.getMessage());
                    }
                }
            }
        }

        /**
         * 文件下载
         */
        public static void download(FTPClient ftpClient, String remoteFilePath,
                                    String remoteFileName, OutputStream outputStream) throws Exception {
            if (ftpClient == null || !ftpClient.isConnected()) {
                log.error("FTP服务器未连接");
                throw new Exception("FTP服务器未连接");
            }
            try {
                String remotePath = remoteFilePath + "/" + remoteFileName;
                boolean retrieved = ftpClient.retrieveFile(remotePath, outputStream);
                if (!retrieved) {
                    log.error("下载失败，文件可能不存在: " + remotePath);
                    throw new Exception("下载失败，文件可能不存在: " + remotePath);
                }
            } catch (Exception e) {
                log.error("下载失败: " + e.getMessage());
                log.error("FTP服务器异常: " + e.getMessage());
//                System.out.println("下载失败: " + e.getMessage());
                throw new Exception("FTP服务器异常: " + e.getMessage());
            }
        }

        /**
         * 删除文件
         */
        public static boolean delete(String host, int port, String username, String password,
                                     String filePath) throws Exception {
            boolean success = false;
            FTPClient ftpClient = null;
            try {
                ftpClient = login(host, port, username, password);

                String dir = filePath.substring(0, Math.max(filePath.lastIndexOf('/'), 0));
                String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);

                if (!ftpClient.changeWorkingDirectory(dir)) {
                    throw new Exception("无法进入目录: " + dir);
                }

                success = ftpClient.deleteFile(fileName);
                if (!success) {
                    System.out.println("删除失败，可能文件不存在");
                }
                return success;

            } finally {
                logout(ftpClient);
            }
        }

        /**
         * 判断文件是否存在
         */
        public static boolean isFileExist(String host, int port, String username, String password,
                                          String remotePath) throws Exception {
            FTPClient ftpClient = null;
            try {
                ftpClient = login(host, port, username, password);

                String dir = remotePath.substring(0, Math.max(remotePath.lastIndexOf('/'), 0));
                String fileName = remotePath.substring(remotePath.lastIndexOf('/') + 1);

                if (!ftpClient.changeWorkingDirectory(dir)) {
                    return false;
                }

                FTPFile[] files = ftpClient.listFiles();
                for (FTPFile file : files) {
                    if (fileName.equals(file.getName())) {
                        return true;
                    }
                }
                return false;

            } finally {
                logout(ftpClient);
            }
        }

        /**
         * 列出目录下的文件名
         */
        public static List<String> fileList(String host, int port, String username, String password,
                                            String remotePath) throws Exception {
            List<String> filelist = new ArrayList<>();
            FTPClient ftpClient = null;
            try {
                ftpClient = login(host, port, username, password);

                if (!ftpClient.changeWorkingDirectory(remotePath)) {
                    throw new Exception("无法进入目录: " + remotePath);
                }

                FTPFile[] files = ftpClient.listFiles();
                for (FTPFile file : files) {
                    if (!file.getName().equals(".") && !file.getName().equals("..")) {
                        filelist.add(file.getName());
                    }
                }
                return filelist;

            } finally {
                logout(ftpClient);
            }
        }

        /**
         * 重命名文件
         */
        public static boolean rename(String host, int port, String username, String password,
                                     String remotePath, String newFileName) throws Exception {
            FTPClient ftpClient = null;
            try {
                ftpClient = login(host, port, username, password);

                String dir = remotePath.substring(0, Math.max(remotePath.lastIndexOf('/'), 0));
                String oldFileName = remotePath.substring(remotePath.lastIndexOf('/') + 1);

                if (!ftpClient.changeWorkingDirectory(dir)) {
                    throw new Exception("无法进入目录: " + dir);
                }

                return ftpClient.rename(oldFileName, newFileName);

            } finally {
                logout(ftpClient);
            }
        }

        /**
         * 递归创建目录
         */
        public static void mkdirs(FTPClient ftpClient, String path) throws IOException {
            String[] dirs = path.split("/");
            StringBuilder current = new StringBuilder();

            for (String dir : dirs) {
                if (dir.isEmpty()) continue;
                current.append("/").append(dir);
                if (!ftpClient.changeWorkingDirectory(current.toString())) {
                    // 目录不存在，尝试创建
                    if (ftpClient.makeDirectory(current.toString())) {
                        log.info("创建目录: " + current);
                    } else {
                        log.error("创建目录失败: " + current);
                    }
                    ftpClient.changeWorkingDirectory(current.toString());
                }
            }
        }
}
