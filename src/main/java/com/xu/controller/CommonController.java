package com.xu.controller;

import com.xu.common.Result;
import com.xu.exception.BusinessException;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.FieldPosition;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
@Api(tags = "通用相关接口")
public class CommonController {
    @Value("${reggie.dish-images-dir}")
    private String imagesDir;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        //file是一个临时文件，需要进行转储
        log.info(file.toString());

        //原始文件格式
        String filename = file.getOriginalFilename();
        String suffix = filename.substring(filename.indexOf("."));

        //使用UUID生成新文件名不重复
        String uuidName = UUID.randomUUID().toString() + suffix;
        log.info("uuidName: " + uuidName);

        //目录不存在时，创建新目录
        File dir = new File(imagesDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        //创建新文件并进行转储
        log.info("dir.getAbsolutePath() + uuidName: " + dir.getAbsolutePath() + uuidName);
        File file1 = new File(this.imagesDir + uuidName);
        file.transferTo(file1);

        //返回新文件名，前端要求
        return Result.success(uuidName);
    }


    @GetMapping("/download")
    public void download(HttpServletResponse response, String name) throws IOException {
        //设置响应类型
        response.setContentType("image/jpeg");

        //写入文件流
        File file = new File(this.imagesDir + name);
        if (!(file.exists())) {
            //找不到对应文件时抛出异常
            throw new BusinessException("找不到对应图片！", 0);
        }
        FileInputStream inputStream = new FileInputStream(file);
        ServletOutputStream outputStream = response.getOutputStream();
        int copy = IOUtils.copy(inputStream, outputStream);
    }
}
