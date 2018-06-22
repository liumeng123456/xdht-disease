package com.xdht.disease.sys.service.impl;

import com.github.pagehelper.PageHelper;
import com.xdht.disease.common.core.AbstractService;
import com.xdht.disease.common.core.PageResult;
import com.xdht.disease.sys.constant.SysEnum;
import com.xdht.disease.sys.dao.RecordPostPersonnelMapper;
import com.xdht.disease.sys.model.RecordPostPersonnel;
import com.xdht.disease.sys.model.RecordPostPersonnelData;
import com.xdht.disease.sys.service.RecordPostPersonnelDataService;
import com.xdht.disease.sys.service.RecordPostPersonnelService;
import com.xdht.disease.sys.vo.request.RecordPostPersonnelInputRequest;
import com.xdht.disease.sys.vo.request.RecordPostPersonnelRequest;
import com.xdht.disease.sys.vo.response.RecordPostPersonnelDetailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by lzf on 2018/06/05.
 */
@Service
@Transactional
public class RecordPostPersonnelServiceImpl extends AbstractService<RecordPostPersonnel> implements RecordPostPersonnelService {

    @Autowired
    private RecordPostPersonnelMapper recordPostPersonnelMapper;
    @Autowired
    private RecordPostPersonnelDataService recordPostPersonnelDataService;

    @Override
    public List<RecordPostPersonnel> queryList(RecordPostPersonnelRequest recordRequest) {
        Condition condition = new Condition(RecordPostPersonnel.class);
        condition.createCriteria() .andEqualTo("id", recordRequest.getId())
                .andEqualTo("postPersonnelNo",recordRequest.getPostPersonnelNo());
        if (recordRequest.getVerificationResult() != null) {
            condition.getOredCriteria().get(0).andLike("verificationResult","%"+recordRequest.getVerificationResult()+"%");
        }
        if (recordRequest.getStatus() != null){
            condition.getOredCriteria().get(0).andEqualTo("status",recordRequest.getStatus());
        }
        return this.recordPostPersonnelMapper.selectByCondition(condition);
    }

    @Override
    public PageResult<RecordPostPersonnel> queryListPage(RecordPostPersonnelRequest recordRequest) {
        Condition condition = new Condition(RecordPostPersonnel.class);
        condition.createCriteria() .andEqualTo("id", recordRequest.getId());
        if (recordRequest.getPostPersonnelNo() != null) {
            condition.getOredCriteria().get(0).andLike("postPersonnelNo","%"+recordRequest.getPostPersonnelNo()+"%");
        }
        if (recordRequest.getVerificationResult() != null) {
            condition.getOredCriteria().get(0).andLike("verificationResult","%"+recordRequest.getVerificationResult()+"%");
        }
        condition.getOredCriteria().get(0).andEqualTo("status", SysEnum.StatusEnum.STATUS_NORMAL.getCode());
        PageHelper.startPage(recordRequest.getPageNumber(), recordRequest.getPageSize());
        List<RecordPostPersonnel> dataList = this.recordPostPersonnelMapper.selectByCondition(condition);
        Integer count = this.selectCountByCondition(condition);
        PageResult<RecordPostPersonnel> pageList = new  PageResult<RecordPostPersonnel>();
        pageList.setTotal(count);
        pageList.setDataList(dataList);
        return pageList;
    }

    @Override
    public RecordPostPersonnel add(RecordPostPersonnelInputRequest recordPostPersonnelInputRequest) {
        RecordPostPersonnel recordPostPersonnel = recordPostPersonnelInputRequest.getRecordPostPersonnel();
        recordPostPersonnel.setStatus(SysEnum.StatusEnum.STATUS_NORMAL.getCode());
        this.insertUseGeneratedKeys(recordPostPersonnel);
        List<RecordPostPersonnelData> recordPostPersonnelDataList = new LinkedList<>();
        for ( RecordPostPersonnelData recordPostPersonnelData : recordPostPersonnelInputRequest.getRecordPostPersonnelDataList()) {
            recordPostPersonnelData.setRelationId(recordPostPersonnel.getId());
            recordPostPersonnelDataList.add(recordPostPersonnelData);
        }
        this.recordPostPersonnelDataService.insertList(recordPostPersonnelDataList);
        return recordPostPersonnel;
    }

    @Override
    public RecordPostPersonnel delete(Long id) {
        RecordPostPersonnel recordPostPersonnel = this.recordPostPersonnelMapper.selectByPrimaryKey(id);
        recordPostPersonnel.setStatus(SysEnum.StatusEnum.STATUS_DELETE.getCode());
        this.recordPostPersonnelMapper.updateByPrimaryKeySelective(recordPostPersonnel);
        return recordPostPersonnel;
    }

    @Override
    public RecordPostPersonnel update(RecordPostPersonnelInputRequest recordPostPersonnelInputRequest) {
        RecordPostPersonnel recordPostPersonnel = recordPostPersonnelInputRequest.getRecordPostPersonnel();
        this.recordPostPersonnelMapper.updateByPrimaryKeySelective(recordPostPersonnel);
        List<RecordPostPersonnelData> recordPostPersonnelDataList = new LinkedList<>();
        for ( RecordPostPersonnelData recordPostPersonnelData : recordPostPersonnelInputRequest.getRecordPostPersonnelDataList() ) {
            if (recordPostPersonnelData.getId() == null ){
                recordPostPersonnelData.setRelationId(recordPostPersonnel.getId());
                recordPostPersonnelDataList.add(recordPostPersonnelData);
            }
            this.recordPostPersonnelDataService.updateByPrimaryKeySelective(recordPostPersonnelData);
        }
        if (recordPostPersonnelDataList.size()>0){
            this.recordPostPersonnelDataService.insertList(recordPostPersonnelDataList);
        }
        return recordPostPersonnel;
    }

    @Override
    public RecordPostPersonnelDetailResponse queryPostPersonnelDetail(Long id) {
        RecordPostPersonnelDetailResponse response = new RecordPostPersonnelDetailResponse();
        //根据sceneId 获取表的数据
        RecordPostPersonnel recordPostPersonnel = new RecordPostPersonnel();
        recordPostPersonnel.setSceneId(id);
        recordPostPersonnel = this.selectOne(recordPostPersonnel);
        if (recordPostPersonnel != null) {
            Long recordId = recordPostPersonnel.getId();
            response.setRecordPostPersonnel(recordPostPersonnel);
            Condition condition = new Condition(RecordPostPersonnelData.class);
            condition.createCriteria() .andEqualTo("relationId", recordId);
            List<RecordPostPersonnelData> recordPostPersonnelDataList = this.recordPostPersonnelDataService.selectByCondition(condition);
            if (recordPostPersonnelDataList.size()>0){
                response.setRecordPostPersonnelDataList(recordPostPersonnelDataList);
            }
        }
        return response;
    }
}