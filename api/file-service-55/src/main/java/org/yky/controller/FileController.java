package org.yky.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yky.common.BaseResponse;
import org.yky.common.ResultUtils;

import java.io.File;

@RestController
@RequestMapping("/file")
public class FileController {

    @PostMapping("/uploadFace")
    public BaseResponse<Void> uploadFace(@RequestParam("file") MultipartFile file,
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

}
