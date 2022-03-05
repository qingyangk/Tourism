package com.webgis.serviceImpl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webgis.ResponseInfo;
import com.webgis.entity.*;
import com.webgis.entity.Info.FormInfo;
import com.webgis.entity.Info.PointInfo;
import com.webgis.entity.table.CoScore;
import com.webgis.entity.table.PointEntity;
import com.webgis.entity.table.ScenicEntity;
import com.webgis.mapper.ScenicMapper;
import com.webgis.mapper.TourMapper;
import com.webgis.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 景点信息提取接口
 */


@Slf4j
@Service(DataImpl.SERVICE_BEAN_NAME)
@DS("MySql")
public class DataImpl implements DataService {
    public final static String SERVICE_BEAN_NAME = "DataService";

    //@Autowired
    @Resource
    ScenicMapper scenicMapper;

    //@Autowired
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
        double[][] point = model.getPoint();
        int size = point.length;

        double[] vertx = new double[size];
        double[] verty = new double[size];
        for (int i = 0; i < size; i++) {
            vertx[i] = point[i][0];
            verty[i] = point[i][1];
        }
        Arrays.sort(vertx);
        Arrays.sort(verty);
        double xmax = vertx[size - 1];
        double xmin = vertx[0];
        double ymax = verty[size - 1];
        double ymin = verty[0];
        List<ScenicEntity> pointEntity = tourMapper.queryPoint(xmax, xmin, ymax, ymin);

        List<ScenicEntity> pointInfo = new ArrayList<>();
        for (ScenicEntity entity : pointEntity) {
            boolean judge = polygonIn(size, vertx, verty, entity.getX(), entity.getY());
            if (judge) {
                pointInfo.add(entity);
            }
        }
        return new ResponseInfo(EnumErrCode.OK, pointInfo);
    }

    public ResponseInfo disData(PageEntity page) {
//        QueryWrapper<ScenicEntity> qw = new QueryWrapper<>();
        List<ScenicEntity> scenicEntity = tourMapper.queryAll();

        for (ScenicEntity sc : scenicEntity) {
            List<CoScore> coScore = tourMapper.coName(sc.getName());
            int hot = coScore.size();
            if (hot <= 5) {
                //删除该记录
                tourMapper.deSC(sc.getId());
                log.info("delete " + sc.getId() + " " + sc.getName());
                continue;
            } else {
                double fen = 0;
                for (CoScore cs : coScore) {
                    fen += cs.getScore();
                }
                fen = (double) fen / hot;
                //将分和hot插入记录，
                tourMapper.upSH(sc.getId(), hot, fen);
                log.info("update " + sc.getId() + " " + hot + " " + fen);
            }
        }
        return new ResponseInfo(EnumErrCode.OK, "ok", null);
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
    public boolean polygonIn(int nvert, double[] vertx, double[] verty, double testx, double testy) {
        int i, j;
        boolean c = false;
        for (i = 0, j = nvert - 1; i < nvert; j = i++) {
            if (((verty[i] > testy) != (verty[j] > testy)) &&
                    (testx < (vertx[j] - vertx[i]) * (testy - verty[i]) / (verty[j] - verty[i]) + vertx[i]))
                c = true;//条件都满足是，布尔为truec = !c;
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