package com.hyx.shortdrama.controller;

import cn.hutool.core.io.FileUtil;
import com.hyx.shortdrama.common.BaseResponse;
import com.hyx.shortdrama.common.ErrorCode;
import com.hyx.shortdrama.common.ResultUtils;
import com.hyx.shortdrama.constant.FileConstant;
import com.hyx.shortdrama.exception.BusinessException;
import com.hyx.shortdrama.manager.CosManager;
import com.hyx.shortdrama.model.dto.file.UploadFileRequest;
import com.hyx.shortdrama.model.entity.User;
import com.hyx.shortdrama.model.enums.FileUploadBizEnum;
import com.hyx.shortdrama.service.UserService;
import java.io.File;
import java.util.Arrays;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件接口
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    /**
     * 文件上传
     *
     * @param multipartFile
     * @param uploadFileRequest
     * @param request
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
            UploadFileRequest uploadFileRequest, HttpServletRequest request) {
        String biz = uploadFileRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        validFile(multipartFile, fileUploadBizEnum);
//        User loginUser = userService.getLoginUser(request);
        User loginUser = userService.getLoginUserPermitNull(request);
        long uid = (loginUser != null && loginUser.getId() != null)? loginUser.getId() : 0;

        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
//        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), filename);
        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), uid, filename);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            // 返回可访问地址
            return ResultUtils.success(FileConstant.COS_HOST + filepath);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Tải lên thất bại, vui lòng thử lại");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * 校验文件
     *
     * @param multipartFile
     * @param fileUploadBizEnum 业务类型
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_M = 1024 * 1024L;
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Kích thước tệp không được vượt quá 1MB");
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Định dạng tệp không hợp lệ, chỉ chấp nhận: jpeg, jpg, svg, png, webp");
            }
        }else if (FileUploadBizEnum.VIDEO.equals(fileUploadBizEnum)) {
            final long MAX_VIDEO = 500L * ONE_M;
            if (fileSize > MAX_VIDEO) throw new BusinessException(ErrorCode.PARAMS_ERROR, "Video quá lớn, kích thước tối đa 500MB");
            if (!Arrays.asList("mp4","mov","m3u8").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Định dạng video không hợp lệ, chỉ hỗ trợ mp4, mov, m3u8");
            }
        }
    }
}
