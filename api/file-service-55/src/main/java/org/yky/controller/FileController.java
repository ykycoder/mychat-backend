package org.yky.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yky.MinIOConfig;
import org.yky.MinIOUtils;
import org.yky.common.BaseResponse;
import org.yky.common.ResultUtils;
import org.yky.exception.ErrorCode;
import org.yky.exception.ThrowUtils;

import java.io.File;

@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private MinIOConfig minIOConfig;

    @PostMapping("/uploadFace1")
    public BaseResponse<Void> uploadFace1(@RequestParam("file") MultipartFile file,
                              String userId,
                              HttpServletRequest request) throws Exception {
        String filename = file.getOriginalFilename();
        String suffixName = filename.substring(filename.lastIndexOf("."));
        String newFileName = userId + suffixName;
        String rootPath = "D:\\temp" + File.separator;
        String filePath = rootPath + File.separator + "face" + File.separator + newFileName;
        File newFile = new File(filePath);
        if (!newFile.getParentFile().exists()) {
            newFile.getParentFile().mkdirs();
        }
        file.transferTo(newFile);
        return ResultUtils.success(null);
    }

    @PostMapping("/uploadFace")
    public BaseResponse<String> uploadFace(@RequestParam("file") MultipartFile file,
                                          String userId,
                                          HttpServletRequest request) throws Exception {
        ThrowUtils.throwIf(StringUtils.isBlank(userId), ErrorCode.OPERATION_ERROR, "文件上传失败！");
        String filename = file.getOriginalFilename();
        ThrowUtils.throwIf(StringUtils.isBlank(filename), ErrorCode.OPERATION_ERROR, "文件上传失败！");
        filename = "face" + "/" + userId + "/" + filename;
        MinIOUtils.uploadFile(minIOConfig.getBucketName(), filename, file.getInputStream());
        String faceUrl = minIOConfig.getFileHost() + "/" + minIOConfig.getBucketName() + "/" + filename;
        return ResultUtils.success(faceUrl);
    }

}
