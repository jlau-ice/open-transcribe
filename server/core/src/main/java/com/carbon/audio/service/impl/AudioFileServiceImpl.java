package com.carbon.audio.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.carbon.audio.mapper.AudioFileMapper;
import com.carbon.audio.model.dto.AudioFileQueryRequest;
import com.carbon.audio.model.entity.AudioFile;
import com.carbon.audio.model.vo.AudioFileVO;
import com.carbon.audio.service.AudioFileService;
import com.carbon.audio.service.mq.AudioProducer;
import com.carbon.common.ErrorCode;
import com.carbon.constant.CommonConstant;
import com.carbon.exception.BusinessException;
import com.carbon.exception.ThrowUtils;
import com.carbon.model.dto.minio.MinioInfo;
import com.carbon.model.entity.User;
import com.carbon.service.UserService;
import com.carbon.utils.MinioUtil;
import com.carbon.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.carbon.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 音频文件Service业务层处理
 *
 * @author jkr
 * @date 2025-09-22
 */
@Service
@Transactional
public class AudioFileServiceImpl extends ServiceImpl<AudioFileMapper, AudioFile> implements AudioFileService {

    private final AudioFileMapper audioFileMapper;

    private final UserService userService;

    private final MinioUtil minioUtil;

    private final AudioProducer audioProducer;

    public static final String FILE_PATH = "audio";


    @Autowired
    public AudioFileServiceImpl(AudioFileMapper audioFileMapper,
                                UserService userService,
                                MinioUtil minioUtil,
                                AudioProducer audioProducer) {
        this.audioFileMapper = audioFileMapper;
        this.userService = userService;
        this.minioUtil = minioUtil;
        this.audioProducer = audioProducer;
    }


    @Override
    public AudioFileVO addAudioFile(MultipartFile file, HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        ThrowUtils.throwIf((currentUser == null || currentUser.getId() == null), ErrorCode.NOT_LOGIN_ERROR);
        long userId = currentUser.getId();
        currentUser = userService.getById(userId);
        AudioFile audioFile = new AudioFile();
        audioFile.setUserId(currentUser.getId());
        audioFile.setFileName(file.getOriginalFilename());
        // 调用minio 返回的路径
        MinioInfo upload = minioUtil.upload(file, FILE_PATH);
        audioFile.setFileSize(file.getSize());
        audioFile.setFileType(file.getContentType());
        audioFile.setFilePath(upload.getFileName());
        this.save(audioFile);
        return AudioFileVO.objToVo(audioFile);
    }

    @Override
    public Page<AudioFileVO> listAudioFileByPage(AudioFileQueryRequest request) {
        Page<AudioFile> page = new Page<>(request.getCurrent(), request.getPageSize());
        Page<AudioFile> res = this.page(page, getQueryWrapper(request));
        //return AudioFileVO.objToVo(res);
        Page<AudioFileVO> voPage = new Page<>(res.getCurrent(), res.getSize(), res.getTotal());
        List<AudioFileVO> voList = res.getRecords().stream()
                .map(AudioFileVO::objToVo)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<AudioFileVO> listAudioFile(AudioFileQueryRequest request) {
        request.setSortField("create_time");
        request.setSortOrder(CommonConstant.SORT_ORDER_DESC);
        QueryWrapper<AudioFile> queryWrapper = this.getQueryWrapper(request);
        return this.list(queryWrapper).stream().map(AudioFileVO::objToVo).toList();
    }

    @Override
    public QueryWrapper<AudioFile> getQueryWrapper(AudioFileQueryRequest audioFileQueryRequest) {
        ThrowUtils.throwIf(audioFileQueryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        QueryWrapper<AudioFile> queryWrapper = new QueryWrapper<>();
        Long id = audioFileQueryRequest.getId();
        Long userId = audioFileQueryRequest.getUserId();
        String fileName = audioFileQueryRequest.getFileName();
        String fileType = audioFileQueryRequest.getFileType();
        Integer status = audioFileQueryRequest.getStatus();
        String sortField = audioFileQueryRequest.getSortField();
        String sortOrder = audioFileQueryRequest.getSortOrder();
        queryWrapper.eq(null != id, "id", id);
        queryWrapper.eq(null != userId, "user_id", userId);
        queryWrapper.like(StringUtils.isNotBlank(fileName), "file_name", fileName);
        queryWrapper.like(StringUtils.isNotBlank(fileType), "file_type", fileType);
        queryWrapper.eq(null != status, "status", status);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 根据id查询音频文件
     *
     * @param id id
     * @return AudioFileVO
     */
    @Override
    public AudioFileVO selectAudioFileVOById(Long id) {
        AudioFile audioFile = audioFileMapper.selectById(id);
        return AudioFileVO.objToVo(audioFile);
    }


    /**
     * 删除音频文件
     *
     * @param id 音频文件id
     */
    @Override
    public void deleteById(Long id) {
        AudioFile audioFile = audioFileMapper.selectById(id);
        ThrowUtils.throwIf(audioFile == null, ErrorCode.NOT_FOUND_ERROR, "音频文件不存在");
        boolean b = this.removeById(audioFile.getId());
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR, "删除失败");
        minioUtil.removeObject(audioFile.getFilePath());
    }

    @Override
    public void transcribe(Long id) {
        AudioFile audioFile = audioFileMapper.selectById(id);
        audioProducer.sendAudioInfo(audioFile);
    }

    /**
     * 查询音频文件
     *
     * @param id 音频文件主键
     * @return 音频文件
     */
    @Override
    public AudioFile selectAudioFilesById(Long id) {
        return audioFileMapper.selectAudioFileById(id);
    }

    /**
     * 查询音频文件列表
     *
     * @param audioFile 音频文件
     * @return 音频文件
     */
    @Override
    public List<AudioFile> selectAudioFilesList(AudioFile audioFile) {
        return audioFileMapper.selectAudioFileList(audioFile);
    }

    /**
     * 新增音频文件
     *
     * @param audioFile 音频文件
     * @return 结果
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int insertAudioFiles(AudioFile audioFile) {
        return audioFileMapper.insertAudioFile(audioFile);
    }

    /**
     * 修改音频文件
     *
     * @param audioFile 音频文件
     * @return 结果
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int updateAudioFiles(AudioFile audioFile) {
        return audioFileMapper.updateAudioFile(audioFile);
    }

    /**
     * 批量删除音频文件
     *
     * @param ids 需要删除的音频文件主键
     * @return 结果
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int deleteAudioFilesByIds(List<Long> ids) {
        return audioFileMapper.logicRemoveByIds(ids);
        //return audioFilesMapper.deleteAudioFilesByIds(ids);
    }

    /**
     * 删除音频文件信息
     *
     * @param id 音频文件主键
     * @return 结果
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int deleteAudioFilesById(Long id) {
        return audioFileMapper.logicRemoveById(id);
        //return audioFilesMapper.deleteAudioFilesById(id);
    }

}
