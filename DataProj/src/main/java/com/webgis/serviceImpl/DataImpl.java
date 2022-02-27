package com.webgis.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webgis.ResponseInfo;
import com.webgis.entity.EnumErrCode;
import com.webgis.entity.Info.ScenicInfo;
import com.webgis.entity.Info.SearchInfo;
import com.webgis.entity.ScenicEntity;
import com.webgis.mapper.ScenicMapper;
import com.webgis.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 景点信息提取接口
 */

@Slf4j
@Service(DataImpl.SERVICE_BEAN_NAME)
public class DataImpl implements DataService {
    public final static String SERVICE_BEAN_NAME = "DataService";

    @Resource
    ScenicMapper scenicMapper;

    public ResponseInfo queryScenic() {
        QueryWrapper<ScenicEntity> qw = new QueryWrapper<>();

        try {
            List<ScenicEntity> scenicEntity = scenicMapper.selectList(qw);

            /**
             * 此处取消分页
             */
//            Map<String, Integer> map = getPage(scenicEntity.size(), 1);
//
//            List<ScenicInfo> ScInfos = new ArrayList<>();
//            int count = 0;
//            int minCount = map.get("min");
//            int maxCount = map.get("max");
//            for (ScenicEntity entity : scenicEntity) {
//                count++;
//                if (count > minCount && count <= maxCount) {
//                    ScenicInfo scInfo = new ScenicInfo();
//                    scInfo.message = entity.getMessage();
//                    scInfo.address = entity.getAddress();
//                    scInfo.name = entity.getName();
//                    scInfo.X = entity.getX();
//                    scInfo.Y = entity.getY();
//                    scInfo.id = entity.getId();
//                    ScInfos.add(scInfo);
//                }
//            }
//
//            SearchInfo searchInfo = new SearchInfo();
//            searchInfo.setTotal(scenicEntity.size());
//            searchInfo.setScInfo(ScInfos);
//            searchInfo.setPages(map.get("pages"));

            return new ResponseInfo(EnumErrCode.OK, scenicEntity);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseInfo(EnumErrCode.CommonError, ex.getMessage());
        }
    }

    /**
     * 分页 传入当前页数及要素个数
     * 返回最大值、最小值、及页面总数
     *
     * @param size
     * @param pagenum
     * @return
     */
    public Map<String, Integer> getPage(int size, int pagenum) {
        //符合条件要素个数
        int featureCount = size;
        //要素最小值
        int minCount = 0;
        //要素最大值
        int maxCount = featureCount;
        //计数
        int count = 0;
        int pageSize = 9;
        int PageNum = pagenum;
        int pages;
        if (maxCount > pageSize) {
            if (maxCount % pageSize == 0) {
                pages = maxCount / pageSize;
            } else {
                pages = maxCount / pageSize + 1;
            }
        } else {
            pages = 1;
        }
        if (pageSize != 0 && PageNum != 0) {
            minCount = (PageNum - 1) * pageSize;  //要素最小值
            maxCount = PageNum * pageSize;  /* 要素最大值 */
        }
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("min", minCount);
        map.put("max", maxCount);
        map.put("pages", pages);
        return map;
    }

}
