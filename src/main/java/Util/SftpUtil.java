package Util;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.jcraft.jsch.*;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class SftpUtil {

//    private final static Logger  logger = Logger.getLogger(SftpUtil.class);
    protected final static int CLIENT_TIMEOUT = 1000 * 20;
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(SftpUtil.class);

    // 登录
    public static ChannelSftp login(String host, int port,String username, String password ) throws Exception {
        Session session;
        try{
            JSch jSch = new JSch();
            session = jSch.getSession(username,host,port);
            session.setTimeout(CLIENT_TIMEOUT);
            session.setPassword(password);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            log.info("开始连接SFTP服务器");
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftp = (ChannelSftp) channel;
            return sftp;
        }catch (JSchException e){
            log.error("连接失败 " + e);
           throw new Exception("连接服务器异常");
        }
    }
    //关闭连接
    public static void logout(ChannelSftp sftp){
        if(sftp != null){
            Session session = null;
            try {
                session = sftp.getSession();
                if(sftp.isConnected()){
                    sftp.disconnect();
                }
            }catch (JSchException e){
                System.out.println("获取Session失败 "+e);
            }finally {
                if(session != null){
                    session.disconnect();
                }
            }
        }
    }

    //下载
    public static void download(ChannelSftp sftp, String remoteFilePath,
                                String remoteFileName, OutputStream outputStream) throws Exception {
        if(sftp == null){
            System.out.println("Sftp服务器连接失败");
            return;
        }
       try {
           sftp.cd(remoteFilePath);
           sftp.get(remoteFileName, outputStream);
       }catch(Exception e){
           System.out.println("下载失败"+e);
           throw new Exception("FTP服务器异常");
       }
    }

    //文件上传
    public static boolean upload(String host, int port, String username,
                              String password, String basePath,
                              String sftpFileName, InputStream input)throws Exception{
        log.info("Sftp开始上传");
        boolean success = true;
        ChannelSftp sftp = login(host, port, username, password);
        mkdir(sftp, basePath);
        try{
            sftp.put(input, sftpFileName);
        }catch(SftpException e){
            log.error("上传文件失败"+e);
            success = false;
        }finally {
            logout(sftp);
        }
        log.info("文件上传完成");
        return success;
    }


    //删除文件
    public static boolean delete(String host, int port, String username,
                                 String password, String filePath) throws Exception{
        boolean success = true;
        ChannelSftp sftp = null;
        try{
            sftp = login(host, port, username, password);
            String directory = filePath.substring(0,filePath.lastIndexOf('/'));
            sftp.cd(directory);
            String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
            sftp.rm(fileName);
            log.info("成功删除",fileName);
        }catch (Exception e){
            log.error("删除失败 "+e);
            success = false;
        }finally {
            logout(sftp);
        }
        return success;
    }

    //查询目录文件
    public static boolean isFileExist(String host, int port, String username, String password, String remotePath){
        boolean success = true;
        ChannelSftp sftp = null;
        try{
            sftp = login(host, port, username, password);
            sftp.cd(remotePath);
            String directory = remotePath.substring(0,remotePath.lastIndexOf(File.separator));
            String fileName = remotePath.substring(remotePath.lastIndexOf(File.separator)+1);
            Vector<?> vector = sftp.ls(directory);
            for (Object object : vector) {
                ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) object;
                if (fileName.equals(lsEntry.getFilename())) {
                    success = true;
                    break;
                }
            }
        }catch(Exception e){
            log.error("查询失败"+e);
            success = false;
        }finally {
            logout(sftp);
        }
        return success;
    }


    //列出目录文件
    public static List<String> fileList(String host, int port, String username, String password, String remotePath){
        List<String> filelist = new ArrayList<String>();
        ChannelSftp sftp = null;
        try{
            sftp = login(host, port, username, password);
            Vector<?> files = sftp.ls(remotePath);
            for (Object object : files) {
                ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) object;
                filelist.add(lsEntry.getFilename());
            }
        }catch (Exception e){
            log.error("查询失败"+e);
        }finally {
            logout(sftp);
        }
        return filelist;
    }

    //重命名
    public static boolean rename(String host, int port, String username, String password, String remotePath,String newFileName){
        boolean success = true;
        ChannelSftp sftp = null;
        try {
            sftp = login(host, port, username, password);
            String directory = remotePath.substring(0,remotePath.lastIndexOf(File.separator));
            String oldFileName = remotePath.substring(remotePath.lastIndexOf(File.separator)+1);
            mkdir(sftp, directory);
            sftp.cd(directory);
            sftp.rename(oldFileName, newFileName);
        }catch (Exception e){
            log.error("重命名失败 "+ e);
            success = false;
        } finally {
            logout(sftp);
        }
        return success;
    }

    //递归创建文件夹
    public static void mkdir(ChannelSftp sftp,String basePath)throws Exception {
        try {
            sftp.cd(basePath);//TODO
        } catch (SftpException e) {
            List<String> dirs = StrUtil.split(basePath, "/");
            String tempPath = "";
            for (String dir : dirs) {
                if (StrUtil.isBlank(dir)) {
                    continue;
                }
                tempPath += StrPool.SLASH + dir;
                try {
                    sftp.cd(tempPath);
                } catch (SftpException e1) {
                    try {
                        sftp.mkdir(tempPath);
                        sftp.cd(tempPath);
                    } catch (SftpException e2) {
                        log.error("创建文件夹失败");
                        throw new Exception(e2);
                    }
                }
                try {
                    sftp.cd(tempPath);
                } catch (SftpException e1) {
                    log.error("服务器异常 " + e1);
                    throw new Exception(e1);
                }
            }
        }
    }
}