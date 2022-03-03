package com.webgis.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webgis.ResponseInfo;
import com.webgis.entity.*;
import com.webgis.entity.Info.ScenicInfo;
import com.webgis.entity.Info.FormInfo;
import com.webgis.mapper.ScenicMapper;
import com.webgis.mapper.TourMapper;
import com.webgis.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
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

    @Resource
    TourMapper tourMapper;

    /**
     * 表格信息展示接口
     *
     * @param model
     * @return
     */
    public ResponseInfo queryScenic(PageEntity model) {
        try {
            long starttime = System.currentTimeMillis();
            log.info("表格数据-start  " + model);

            int pageNum = model.getPage();
            int count = model.getCount();
            Page<ScenicEntity> page = new Page<>(pageNum, count);
            QueryWrapper<ScenicEntity> qw = new QueryWrapper<>();
            Page<ScenicEntity> scenicInfo = scenicMapper.selectPage(page, qw);

            List<ScenicEntity> records = scenicInfo.getRecords();

            FormInfo formInfo = new FormInfo();
            formInfo.setScInfo(records);
            formInfo.setTotal(scenicInfo.getTotal());
            formInfo.setPages(scenicInfo.getPages());

            long endtime = System.currentTimeMillis();
            log.info("表格数据-end  " + (endtime - starttime) + "ms");
            return new ResponseInfo(EnumErrCode.OK, formInfo);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseInfo(EnumErrCode.CommonError, ex.getMessage());
        }
    }

    /**
     * 表格信息搜索接口
     *
     * @param model
     */
    public ResponseInfo searchScenic(SearchEntity model) {
        String sql = model.getSearch();
        try {
            long starttime = System.currentTimeMillis();
            log.info("表格搜索-start  " + model);

            int pageNum = model.getPage();
            int count = model.getCount();
            Page<ScenicEntity> page = new Page<>(pageNum, count);
            QueryWrapper<ScenicEntity> qw = new QueryWrapper<>();
            qw.like("Name", sql);
            Page<ScenicEntity> searchInfo = scenicMapper.selectPage(page, qw);

            List<ScenicEntity> records = searchInfo.getRecords();

            FormInfo formInfo = new FormInfo();
            formInfo.setScInfo(records);
            formInfo.setTotal(searchInfo.getTotal());
            formInfo.setPages(searchInfo.getPages());

            long endtime = System.currentTimeMillis();
            log.info("表格搜索-end  " + (endtime - starttime) + "ms");
            return new ResponseInfo(EnumErrCode.OK, formInfo);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseInfo(EnumErrCode.CommonError, ex.getMessage());
        }
    }

    /**
     * 空间数据
     */
    public ResponseInfo spaceIn(Point model) {
        List<double[]> point = model.getPoint();
        for (int i = 0; i < point.size(); i++) {

        }
        return new ResponseInfo(EnumErrCode.OK, null);
    }

    /**
     * 分页 传入当前页数及要素个数
     * 返回最大值、最小值、及页面总数
     *
     * @param size
     * @param pagenum
     * @return
     */
    public Map<String, Integer> getPage(int size, int pagenum, int count) {
        //符合条件要素个数
        int featureCount = size;
        //要素最小值
        int minCount = 0;
        //要素最大值
        int maxCount = featureCount;
        //计数
        //int count = 0;
        int pageSize = count;
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

    /**
     * 空间交集查询--点与面
     *
     * @param nvert 多边形的点数
     * @param vertx 多边形x坐标的数组
     * @param verty 多边形y坐标的数组
     * @param testx 测试点x的坐标
     * @param testy 测试点y的坐标
     * @return
     */
    public int polygonIn(int nvert, double[] vertx, double[] verty, double testx, double testy) {
        int i, j, c = 0;
        for (i = 0, j = nvert - 1; i < nvert; j = i++) {
            if (((verty[i] > testy) != (verty[j] > testy)) &&
                    (testx < (vertx[j] - vertx[i]) * (testy - verty[i]) / (verty[j] - verty[i]) + vertx[i]))
                c = 1;//条件都满足是，布尔为truec = !c;
        }
        return c;
    }
}

//老兵之凋零，残破的代码
//            Page<ScenicEntity> obj = new Page<ScenicEntity>(2, 8);
//            IPage<ScenicEntity> formInfo = scenicMapper.selectPage(obj, qw);
//            Map<String, Integer> map = getPage(scenicEntity.size(), model.getPage(), model.getCount());
//            List<ScenicInfo> ScInfos = new ArrayList<>();
//            int count = 0;
//            int minCount = map.get("min");
//            int maxCount = map.get("max");
//            for (ScenicEntity entity : scenicEntity) {
//                count++;
//                if (count > minCount && count <= maxCount) {
//                    ScenicInfo scInfo = new ScenicInfo();
//                    scInfo.message = entity.getJianjie();
//                    scInfo.address = entity.getAddress();
//                    scInfo.name = entity.getName();
//                    scInfo.X = entity.getX();
//                    scInfo.Y = entity.getY();
//                    scInfo.id = entity.getId();
//                    ScInfos.add(scInfo);
//                }
//            }