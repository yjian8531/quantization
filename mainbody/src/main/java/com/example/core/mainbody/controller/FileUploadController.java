package com.example.core.mainbody.controller;

import com.example.core.common.utils.ResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @Value("${web.upload-path}")
    private String uploadPath;

    @Value("${web.access-path}")
    private String accessPath;

    @PostMapping("/avatar")
    public ResultMessage uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "文件不能为空");
        }

        String contentType = file.getContentType();
        if (!contentType.startsWith("image/")) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "只能上传图片文件");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String datePath = sdf.format(new Date());
            String dirPath = uploadPath + "/avatar/" + datePath;
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File destFile = new File(dirPath + "/" + fileName);
            file.transferTo(destFile);

            String fileUrl = accessPath + "avatar/" + datePath + "/" + fileName;
            log.info("头像上传成功，URL: {}", fileUrl);
            
            return new ResultMessage(ResultMessage.SUCCEED_CODE, "上传成功", fileUrl);

        } catch (IOException e) {
            log.error("头像上传失败", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "上传失败：" + e.getMessage());
        }
    }
}
