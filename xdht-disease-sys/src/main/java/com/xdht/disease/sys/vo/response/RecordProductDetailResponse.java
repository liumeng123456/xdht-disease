package com.xdht.disease.sys.vo.response;

import com.xdht.disease.sys.model.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by L on 2018/6/14.
 */
@Data
public class RecordProductDetailResponse {

    @ApiModelProperty(value = "物料及产品调查表")
    private RecordProduct recordProduct;

    @ApiModelProperty(value = "物料及产品调查表--调查内容")
    private List<RecordProductData> recordProductDataList;

    @ApiModelProperty(value = "物料及产品调查表--部门列表内容")
    private List<SysCompanyOffice> sysCompanyOfficeList;



}
