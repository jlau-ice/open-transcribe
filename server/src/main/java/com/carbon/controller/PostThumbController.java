package com.carbon.controller;

import com.carbon.common.BaseResponse;
import com.carbon.common.ErrorCode;
import com.carbon.common.ResultUtils;
import com.carbon.exception.BusinessException;
import com.carbon.model.dto.postthumb.PostThumbAddRequest;
import com.carbon.model.entity.User;
import com.carbon.service.PostThumbService;
import com.carbon.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 帖子点赞接口
 *
 */
@RestController
@RequestMapping("/post_thumb")
@Slf4j
public class PostThumbController {

    private final PostThumbService postThumbService;

    private final UserService userService;

    @Autowired
    public PostThumbController(PostThumbService postThumbService, UserService userService) {
        this.postThumbService = postThumbService;
        this.userService = userService;
    }

    /**
     * 点赞 / 取消点赞
     *
     * @param postThumbAddRequest
     * @param request
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doThumb(@RequestBody PostThumbAddRequest postThumbAddRequest,
            HttpServletRequest request) {
        if (postThumbAddRequest == null || postThumbAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        long postId = postThumbAddRequest.getPostId();
        int result = postThumbService.doPostThumb(postId, loginUser);
        return ResultUtils.success(result);
    }

}
