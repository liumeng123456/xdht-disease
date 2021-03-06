package com.xdht.disease.sys.service.impl;

import com.xdht.disease.common.core.AbstractService;
import com.xdht.disease.sys.dao.RecordHealthManagementDataMapper;
import com.xdht.disease.sys.model.RecordHealthManagementData;
import com.xdht.disease.sys.service.RecordHealthManagementDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


/**
 * Created by lzf on 2018/06/05.
 */
@Service
@Transactional
public class RecordHealthManagementDataServiceImpl extends AbstractService<RecordHealthManagementData> implements RecordHealthManagementDataService{

    @Autowired
    private RecordHealthManagementDataMapper recordHealthManagementDataMapper;

    @Override
    public List<Map<String, Object>> queryRecordDataByHealthManagementId(Long id) {
        return this.recordHealthManagementDataMapper.selectRecordDataByHealthManagementId(id);
    }
}
