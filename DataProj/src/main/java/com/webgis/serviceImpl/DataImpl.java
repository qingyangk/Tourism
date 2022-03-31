package com.webgis.serviceImpl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webgis.ResponseInfo;
import com.webgis.entity.Info.*;
import com.webgis.entity.Travel;
import com.webgis.entity.*;
import com.webgis.entity.table.*;
import com.webgis.mapper.ScenicMapper;
import com.webgis.mapper.TourMapper;
import com.webgis.mapper.TravelMapper;
import com.webgis.service.DataService;
import lombok.extern.slf4j.Slf4j;
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

    @Resource
    ScenicMapper scenicMapper;

    @Resource
    TourMapper tourMapper;

    @Resource
    TravelMapper travelMapper;

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
            qw.orderByAsc("comrank");
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
            qw.like("Name", sql).or().like("city", sql);
            qw.orderByAsc("comrank");
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
            //判断是否在传入的多边形内
            boolean judge = polygonIn(size, vertx, verty, entity.getX(), entity.getY());
            if (judge) {
                pointInfo.add(entity);
            }
        }
        return new ResponseInfo(EnumErrCode.OK, pointInfo);
    }

    /**
     * 数据处理接口、用于处理数据库中sql语句不好写的数据
     *
     * @param page
     * @return
     */
    public ResponseInfo disData(SearchEntity page) {
        try {
            /**
             * 将景点表与评论表对应 删除评论数不足5的景点
             */
            if (page.getPage() == 1) {
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
            }
            /**
             * 在评论表中添加省、市字段值
             */
            if (page.getPage() == 2) {
                //查询所有景点
                List<ScenicEntity> scenicEntity = tourMapper.queryAll();
                for (ScenicEntity entity : scenicEntity) {
                    //用景点名称去找寻评论
                    List<CoPC> copc = tourMapper.coPC(entity.getName());
                    if (copc.size() == 0)
                        continue;
                    else {
                        //添加省、城市
                        tourMapper.upPC(entity.getName(), entity.getProvince(), entity.getCity());
                        String info = entity.getName() + " " + entity.getProvince() + " " + entity.getCity();
                        log.info("add " + info);
                    }
                }
            }
            /**
             * 从评论表中获取分与热度
             */
            if (page.getPage() == 3) {
                List<ScenicEntity> scenicEntity = tourMapper.queryAll();
                for (ScenicEntity sc : scenicEntity) {
                    List<CoScore> coScore = tourMapper.coName(sc.getName());
                    int hot = coScore.size();
                    if (hot < 1) {
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
            }
            /**
             * 新表添加城市，及计算城市评论数
             */
            if (page.getPage() == 4) {
                List<Map<String, Object>> citys = tourMapper.cRank();
                int id = 1;
                for (Map<String, Object> cr : citys) {
                    String city = (String) cr.get("city");
                    int cityCount = tourMapper.cCount(city);
                    tourMapper.inCC(id, city, cityCount);
                    id++;
                }
            }
            /**
             * 赋于城市景点个数及分数
             */
            if (page.getPage() == 5) {
                //查询所有城市
                List<CityRank> cityRanks = tourMapper.getcity();
                log.info("查询到所有城市");
                for (CityRank cr : cityRanks) {
                    String city = cr.getCity();
                    //查询该城市的景点信息
                    List<ScenicEntity> scenics = tourMapper.cScenicCount(city);
                    log.info("查询到该城市的信息--" + city);
                    if (scenics.size() == 0) {
                        continue;
                    }
                    double scorenum = 0;
                    for (ScenicEntity se : scenics) {
                        scorenum += se.getScore();
                    }
                    double score = scorenum / scenics.size();
                    //更新城市景点个数及平均分数
                    tourMapper.upCityScore(city, scenics.size(), score);
                    log.info("赋值于该城市景点数量、分数:" + city + scenics.size() + "、" + score);
                }

            }
            /**
             * 将经纬度坐标转换成geojson--景点
             */
            if (page.getPage() == 6) {
                //获取点信息
                List<PointEntity> points = tourMapper.getPoint();
                //将点信息转换成geojson
                List<GeoFeaScenic> geoFeaScenicList = new ArrayList<>();
                for (PointEntity pe : points) {
                    geometry geometry = new geometry();
                    GeoFeaScenic GeoFeaScenic = new GeoFeaScenic();
                    double[] xy = new double[]{pe.getX(), pe.getY()};
                    geometry.setCoordinates(xy);

                    GeoFeaScenic.setGeometry(geometry);
                    GeoFeaScenic.setProperties(pe);
                    geoFeaScenicList.add(GeoFeaScenic);
                }
                return new ResponseInfo(EnumErrCode.OK, geoFeaScenicList);
            }
            /**
             * 在评论表中添加经纬度
             */
            if (page.getPage() == 7) {
                //查询所有景点获取经纬度
                List<ScenicEntity> scenicEntity = tourMapper.queryLL();
                for (ScenicEntity entity : scenicEntity) {
                    //用景点名称去找寻评论
                    List<CoPC> copc = tourMapper.coPC(entity.getName());
                    if (copc.size() == 0)
                        continue;
                    else {
                        //添加经纬度
                        double longitude = entity.getX();
                        double latitude = entity.getY();
                        tourMapper.upLaLg(longitude, latitude, entity.getName());
                        String info = entity.getName() + " " + longitude + " " + latitude;
                        log.info("add " + info);
                    }
                }
            }
            /**
             * 将经纬度坐标转换成geojson--评论
             */
            if (page.getPage() == 8) {
                //获取点信息
                List<ComPoint> points = tourMapper.comPoint();
                //将点信息转换成geojson
                List<GeoFeaComment> geoFeaComments = new ArrayList<>();
                for (ComPoint pe : points) {
                    geometry geometry = new geometry();
                    GeoFeaComment geoFeaComment = new GeoFeaComment();
                    double[] xy = new double[]{pe.getLongitude(), pe.getLatitude()};
                    geometry.setCoordinates(xy);

                    geoFeaComment.setGeometry(geometry);
                    geoFeaComment.setProperties(pe);
                    geoFeaComments.add(geoFeaComment);
                }
                return new ResponseInfo(EnumErrCode.OK, geoFeaComments);
            }

            if (page.getPage() == 9) {

            }
            return new ResponseInfo(EnumErrCode.OK, null);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseInfo(EnumErrCode.CommonError, ex.getMessage());
        }
    }

    /**
     * 游记查询
     */
    public ResponseInfo queryTravel(Travel model) {
        try {
            long starttime = System.currentTimeMillis();
            log.info("游记查询-start  " + model);

            int count = model.getCount();
            QueryWrapper<TravelEntity> qw = new QueryWrapper<>();
            qw.like("region", model.getCity());
            List<TravelEntity> travelEntity = travelMapper.selectList(qw);
            TravelEntity entity = new TravelEntity();
            if (count > travelEntity.size()) {
                int random = new Random().nextInt(travelEntity.size());
                entity = travelEntity.get(random);
                System.out.println("当前游记随机数：" + random);
            } else {
                entity = travelEntity.get(count);
            }

            long endtime = System.currentTimeMillis();
            log.info("游记查询-end  " + (endtime - starttime) + "ms");
            return new ResponseInfo(EnumErrCode.OK, entity);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseInfo(EnumErrCode.CommonError, ex.getMessage());
        }
    }

    /**
     * 获取景点排行--传入type
     *
     * @return
     */
    public ResponseInfo ScenicRank(Request model) {
        String type = model.getModel();
        if (type.equals("score"))
            type = "scorerank";
        else if (type.equals("hot"))
            type = "hotrank";
        else type = "comrank";
        try {
            long starttime = System.currentTimeMillis();
            log.info("景点排行-start  ");

            QueryWrapper<ScenicEntity> qw = new QueryWrapper<>();
            qw.orderByAsc(type);
//            qw.ne("level", "null");
            qw.last("limit 10");
            List<ScenicEntity> entity = scenicMapper.selectList(qw);
            List<RankInfo> rankInfos = new ArrayList<>();
            for (ScenicEntity en : entity) {
                RankInfo rankInfo = new RankInfo();
                rankInfo.setName(en.getName());
                rankInfo.setCity(en.getCity());
                rankInfo.setComrank(en.getComrank());
                rankInfo.setHot(en.getHot());
                rankInfo.setScore(en.getScore());
                String level = en.getLevel();
                if (level.equals("null")) {
                    level = "无";
                }
                rankInfo.setLevel(level);
                rankInfos.add(rankInfo);
            }

            long endtime = System.currentTimeMillis();
            log.info("景点排行-end  " + (endtime - starttime) + "ms");

            return new ResponseInfo(EnumErrCode.OK, rankInfos);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseInfo(EnumErrCode.CommonError, ex.getMessage());
        }
    }

    /**
     * 获取城市排行--以景点数量进行排名
     *
     * @return
     */
    public ResponseInfo CityRank() {
        try {
            long starttime = System.currentTimeMillis();
            log.info("城市排名查询-start  ");

            List<CityRank> cityRanks = tourMapper.cityRank();

            long endtime = System.currentTimeMillis();
            log.info("城市排行-end  " + (endtime - starttime) + "ms");

            return new ResponseInfo(EnumErrCode.OK, cityRanks);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseInfo(EnumErrCode.CommonError, ex.getMessage());
        }
    }

    /**
     * 景点推荐
     */
    public ResponseInfo Recommend(Recommend model) {
        try {
            long starttime = System.currentTimeMillis();
            log.info("景点推荐-start  ");
            QueryWrapper<ScenicEntity> qw = new QueryWrapper<>();
            String sql = model.getLabel();
            qw.like("label", sql).or().like("name", sql);
            qw.orderByAsc("comrank");
            qw.last("limit 10");
            List<ScenicEntity> scenicEntities = scenicMapper.selectList(qw);

            long endtime = System.currentTimeMillis();
            log.info("景点推荐-end  " + (endtime - starttime) + "ms");
            return new ResponseInfo(EnumErrCode.OK, scenicEntities);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseInfo(EnumErrCode.CommonError, ex.getMessage());
        }
    }

    /**
     * 依据id查询景点表所有内容
     */
    public ResponseInfo ScenicID(ID model) {
        long starttime = System.currentTimeMillis();
        log.info("景点查询-start  ");

        int id = model.getId();
        QueryWrapper<ScenicEntity> qw = new QueryWrapper<>();
        qw.eq("id", id);
        List<ScenicEntity> scenic = scenicMapper.selectList(qw);
        List<CommentEntity> comment = new ArrayList<>();
        if (scenic.size() != 0) {
            String name = scenic.get(0).getName();
            comment = tourMapper.getComment(name);
        }
        AllInfo allInfo = new AllInfo();
        allInfo.setScenicEntity(scenic.get(0));
        allInfo.setCommentEntity(comment);

        long endtime = System.currentTimeMillis();
        log.info("景点查询-end  " + (endtime - starttime) + "ms");
        return new ResponseInfo(EnumErrCode.OK, allInfo);
    }

    /**
     * 景点评论数据月份统计
     */
    public ResponseInfo ComMonth(Request model) {
        long starttime = System.currentTimeMillis();
        log.info("景点月份评论-start  ");

        String name = model.getModel();
        int[] comcount = new int[12];
        double[] scoreavg = new double[12];
        List<CommentEntity> comment = tourMapper.getCom(name);
        for (int i = 1; i <= 12; i++) {
            int sum = 0;
            double score = 0;
            for (CommentEntity co : comment) {
                int month = Integer.parseInt(co.getDate().substring(5, 7));
                if (i != month)
                    continue;
                sum++;
                score += co.getScore();
            }
            scoreavg[i - 1] = score / sum;
            comcount[i - 1] = sum;
        }
        CommentMonth commentMonth = new CommentMonth();
        commentMonth.setComcount(comcount);
        commentMonth.setScoreavg(scoreavg);

        long endtime = System.currentTimeMillis();
        log.info("景点月份评论-end  " + (endtime - starttime) + "ms");
        return new ResponseInfo(EnumErrCode.OK, commentMonth);
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