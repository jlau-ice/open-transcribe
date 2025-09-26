package com.carbon.result.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.carbon.result.mapper.TranscribeResultMapper;
import com.carbon.result.model.entity.TranscribeResult;
import com.carbon.result.service.TranscribeResultService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TranscribeResultServiceImpl extends ServiceImpl<TranscribeResultMapper, TranscribeResult> implements TranscribeResultService {

}
