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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
            log.info("城市排行-start  ");

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

            String[] label = model.getLabel();
            String preference = model.getPreference();
            if (preference.equals("hot")) {
                preference = "hotrank";
            } else if (preference.equals("score")) {
                preference = "scorerank";
            } else {
                preference = "comrank";
            }

            List<NanJingEntity> nanJingEntities = tourMapper.nanjingRe(preference);
            List<NanJingEntity> nanjingInfo = new ArrayList<>();
            for (NanJingEntity na : nanJingEntities) {
                for (String lb : label) {
                    if (lb.equals(na.getLabel1())) {
                        nanjingInfo.add(na);
                        break;
                    }
                    if (lb.equals(na.getLabel2())) {
                        nanjingInfo.add(na);
                        break;
                    }
                    if (lb.equals(na.getLabel3())) {
                        nanjingInfo.add(na);
                        break;
                    }
                }
            }
            if (nanjingInfo.isEmpty()) {
                nanjingInfo = nanJingEntities.subList(0, 10);
            }

            long endtime = System.currentTimeMillis();
            log.info("景点推荐-end  " + (endtime - starttime) + "ms");
            return new ResponseInfo(EnumErrCode.OK, nanjingInfo);
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
                int month = Integer.parseInt(co.getReleaseTime().substring(5, 7));
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
     * 获取城市评论数
     */
    public ResponseInfo CityComment(Request model) {
        List<CommentEntity> com = tourMapper.cityCom(model.getModel());
        return new ResponseInfo(EnumErrCode.OK, com);
    }

    /**
     * 获取时间段的天数评论
     */
    public ResponseInfo CommentDay() {
        try {
            long starttime = System.currentTimeMillis();
            log.info("年份天数评论-start  ");

            List<CommentEntity> times = tourMapper.lastCom();
            String endDate = times.get(0).getReleaseTime().substring(0, 7);
            int year = Integer.parseInt(endDate.substring(0, 4)) - 1;
            int month = Integer.parseInt(endDate.substring(5, 7)) - 1;
            String startDate = year + endDate.substring(4, 7);
            endDate = (year + 1) + "-0" + month;
            List<Integer> scores = new ArrayList<>();
            List<Integer> travelDay = new ArrayList<>();
            List<Integer> dayCount = new ArrayList<>();
            List<CommentDay> commentDays = tourMapper.commentDay(startDate, endDate);
            List<CommentDay> travelDays = tourMapper.travelDay(startDate, endDate);

            for (CommentDay cd : commentDays) {
                dayCount.add(cd.getCount());
                scores.add(cd.getScore());
            }
            for (CommentDay cd : travelDays) {
                travelDay.add(cd.getCount());
            }

            Map<String, Object> data = new HashMap<>();
            data.put("starttime", startDate);
            data.put("endtime", endDate);
            data.put("startStamp", dateToStamp(startDate + "-01"));
            data.put("endStamp", dateToStamp(endDate + "-01"));
            data.put("daycount", dayCount.toArray());
            data.put("favorable", scores.toArray());
            data.put("travel", travelDay.toArray());
            long endtime = System.currentTimeMillis();
            log.info("年份天数评论-end  " + (endtime - starttime) + "ms");
            return new ResponseInfo(EnumErrCode.OK, data);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseInfo(EnumErrCode.CommonError, ex.getMessage());
        }

    }

    /**
     * 日期景点评论数--传入年月
     */
    public ResponseInfo ScenicDay(Request model) {
        long starttime = System.currentTimeMillis();
        log.info("月份景点评论数-start  ");

        String time = model.getModel();
        List<ScenicDay> scenicDays = tourMapper.ScenicDay(time);
        List<String> scenic = new ArrayList<>();
        List<Integer> count = new ArrayList<>();
        List<ScenicDayInfo> dayInfos = new ArrayList<>();
        String date = "";
        for (ScenicDay sd : scenicDays) {
            if (!date.equals(sd.getDate()) && scenic.size() == 20) {
                date = sd.getDate();
                ScenicDayInfo dayInfo = new ScenicDayInfo();
                dayInfo.setScenic(scenic.toArray());
                dayInfo.setDate(date);
                dayInfo.setCount(count.toArray());
                dayInfos.add(dayInfo);
                scenic = new ArrayList<>();
                count = new ArrayList<>();
            }
            if (scenic.size() < 20) {
                scenic.add(sd.getName());
                count.add(sd.getCount());
            }
        }

        long endtime = System.currentTimeMillis();
        log.info("月份景点评论数-end  " + (endtime - starttime) + "ms");
        return new ResponseInfo(EnumErrCode.OK, dayInfos);
    }

    /**
     * 城市每月评论热度
     */
    public ResponseInfo CityDay(Request model) {
        long starttime = System.currentTimeMillis();
        log.info("月份城市评论数-start  ");

        String time = model.getModel();
        List<ScenicDay> scenicDays = tourMapper.CityDay(time);
        List<String> city = new ArrayList<>();
        List<Integer> count = new ArrayList<>();
        List<CityDayInfo> dayInfos = new ArrayList<>();
        String date = "";
        for (ScenicDay sd : scenicDays) {
            if (!date.equals(sd.getDate()) && city.size() == 20) {
                date = sd.getDate();
                CityDayInfo dayInfo = new CityDayInfo();
                dayInfo.setCity(city.toArray());
                dayInfo.setDate(date);
                dayInfo.setCount(count.toArray());
                dayInfos.add(dayInfo);
                city = new ArrayList<>();
                count = new ArrayList<>();
            }
            if (city.size() < 20) {
                city.add(sd.getName());
                count.add(sd.getCount());
            }
        }

        long endtime = System.currentTimeMillis();
        log.info("月份城市评论数-end  " + (endtime - starttime) + "ms");
        return new ResponseInfo(EnumErrCode.OK, dayInfos);
    }

    /**
     * 景点数、评论数、游记数获取
     */
    public ResponseInfo GetSCT() {
        List<SCT> list = tourMapper.GetSCT();
        Map<String, Long> data = new HashMap<>();
        data.put("scenic", list.get(0).getInfo());
        data.put("comment", list.get(1).getInfo());
        data.put("travel", list.get(2).getInfo());
        return new ResponseInfo(EnumErrCode.OK, data);
    }

    /**
     * 获取词云
     */
    public ResponseInfo GetWordCloud(Request model) {
        long starttime = System.currentTimeMillis();
        log.info("城市词云-start  ");

        String city = model.getModel();
        List<WordCloud> wordClouds = tourMapper.GetWC(city);
        String word = wordClouds.get(0).getWord();
        String frequency = wordClouds.get(0).getFrequency();

        //消除符号及分割
        word = word.replaceAll("\\[|\\]", "");
        frequency = frequency.replaceAll("\\[|\\]", "");
        String[] arrword = word.split(",");
        String[] arrfrequency = frequency.split(",");

        List<WordCloudInfo> wordCloudList = new ArrayList<>();
        for (int i = 0; i < arrword.length; i++) {
            WordCloudInfo wordCloudInfo = new WordCloudInfo();
            wordCloudInfo.setName(arrword[i].replace(" ", ""));
            wordCloudInfo.setValue(Integer.parseInt(arrfrequency[i].replace(" ", "")));
            wordCloudList.add(wordCloudInfo);
        }

        long endtime = System.currentTimeMillis();
        log.info("城市词云-end  " + (endtime - starttime) + "ms");
        return new ResponseInfo(EnumErrCode.OK, wordCloudList);
    }

    /**
     * 各地（省、市）流入流出量--客流分析
     * 评论数量
     */
    public ResponseInfo OutPutPassengerFlow(FlowRequest flow) {
        try {
            long starttime = System.currentTimeMillis();
            log.info("客流分析-评论数量-start  ");

            String place = flow.getModel();
            String cORp = "province";
            if (flow.getType() == 1) {
                cORp = "city";
            }
            List<FlowEntity> flowEntities;
            if (flow.getPath() == 1) {
                flowEntities = tourMapper.PutFlow(cORp, place);
            } else {
                flowEntities = tourMapper.OutFlow(cORp, place);
            }

            long endtime = System.currentTimeMillis();
            log.info("客流分析-评论数量-end  " + (endtime - starttime) + "ms");
            return new ResponseInfo(EnumErrCode.OK, flowEntities);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseInfo(EnumErrCode.CommonError, ex.getMessage());
        }
    }

    /**
     * 各地（省、市）流入流出量--客流分析
     * 评论分数
     */
    public ResponseInfo OutPutScore(FlowRequest flow) {
        try {
            long starttime = System.currentTimeMillis();
            log.info("客流分析-评论分数-start  ");

            String place = flow.getModel();
            String cORp = "province";
            if (flow.getType() == 1) {
                cORp = "city";
            }
            List<OPScore> flowEntities;
            if (flow.getPath() == 1) {
                flowEntities = tourMapper.PutScore(cORp, place);
            } else {
                flowEntities = tourMapper.OutScore(cORp, place);
            }

            long endtime = System.currentTimeMillis();
            log.info("客流分析-评论分数-end  " + (endtime - starttime) + "ms");
            return new ResponseInfo(EnumErrCode.OK, flowEntities);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseInfo(EnumErrCode.CommonError, ex.getMessage());
        }
    }

    /**
     * 各地（省、市）流入流出量--客流分析
     * 评论日期变化
     */
    public ResponseInfo OutPutDay(FlowRequest flow) {
        try {
            long starttime = System.currentTimeMillis();
            log.info("客流分析-评论日期变化-start  ");

            String place = flow.getModel();
            String cORp = "province";
            if (flow.getType() == 1) {
                cORp = "city";
            }
            List<OPDay> opDays;
            if (flow.getPath() == 1) {
                opDays = tourMapper.PutDay(cORp, place);
            } else {
                opDays = tourMapper.OutDay(place);
            }

            List<String> time = new ArrayList<>();
            List<Integer> count = new ArrayList<>();
            for (OPDay day : opDays) {
                time.add(day.getReleaseTime());
                count.add(day.getCount());
            }
            Map<String, Object> data = new HashMap<>();
//            data.put("time", time.toArray());
            data.put("count", count.toArray());

            long endtime = System.currentTimeMillis();
            log.info("客流分析-评论日期变化-end  " + (endtime - starttime) + "ms");
            return new ResponseInfo(EnumErrCode.OK, data);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseInfo(EnumErrCode.CommonError, ex.getMessage());
        }
    }

    /**
     * 各地（省、市）流入流出量--客流分析
     * 景点数量
     */
    public ResponseInfo OPScenicRank(FlowRequest flow) {
        try {
            long starttime = System.currentTimeMillis();
            log.info("客流分析-景点数量-start  ");

            String place = flow.getModel();
            String cORp = "province";
            if (flow.getType() == 1) {
                cORp = "city";
            }
            List<FlowEntity> flowEntities;
            if (flow.getPath() == 1) {
                flowEntities = tourMapper.PutScenic(cORp, place);
            } else {
                flowEntities = tourMapper.OutScenic(place);
            }
            List<String> scenic = new ArrayList<>();
            List<Integer> count = new ArrayList<>();
            for (FlowEntity fe : flowEntities) {
                scenic.add(fe.getDestination());
                count.add(fe.getCount());
            }

            Map<String, Object> data = new HashMap<>();
            data.put("scenic", scenic.toArray());
            data.put("count", count.toArray());

            long endtime = System.currentTimeMillis();
            log.info("客流分析-景点数量-end  " + (endtime - starttime) + "ms");
            return new ResponseInfo(EnumErrCode.OK, data);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseInfo(EnumErrCode.CommonError, ex.getMessage());
        }
    }

    /**
     * 根据特征返回榜单
     * 传入：特征词
     */
    public ResponseInfo FeatureInfo(Request model) {
        try {
            long starttime = System.currentTimeMillis();
            log.info("特征提取-start  ");

            String label = model.getModel();
            List<ScenicLabel> labels = tourMapper.ScenicLabel(label);

            long endtime = System.currentTimeMillis();
            log.info("特征提取-end  " + (endtime - starttime) + "ms");
            return new ResponseInfo(EnumErrCode.OK, labels);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseInfo(EnumErrCode.CommonError, ex.getMessage());
        }
    }

    /**
     * 城市景点热度排行榜
     */
    public ResponseInfo CityScenicHot(Request model) {
        try {
            long starttime = System.currentTimeMillis();
            log.info("城市景点热度-start  ");

            List<ScenicEntity> scenicEntities = tourMapper.CitySH(model.getModel());
            List<Map<String, Object>> data = new ArrayList<>();
            for (ScenicEntity en : scenicEntities) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", en.getName());
                map.put("value", en.getHot());
                data.add(map);
            }

            long endtime = System.currentTimeMillis();
            log.info("城市景点热度-end  " + (endtime - starttime) + "ms");
            return new ResponseInfo(EnumErrCode.OK, data);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseInfo(EnumErrCode.CommonError, ex.getMessage());
        }
    }

    /**
     * 城市游客来源地前五
     */
    public ResponseInfo CitySource(Request model) {
        try {
            long starttime = System.currentTimeMillis();
            log.info("城市游客来源-start  ");

            List<FlowEntity> flowEntities = tourMapper.CitySource(model.getModel());
            List<Map<String, Object>> data = new ArrayList<>();
            for (FlowEntity en : flowEntities) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", en.getDestination());
                map.put("value", en.getCount());
                data.add(map);
            }

            long endtime = System.currentTimeMillis();
            log.info("城市游客来源-end  " + (endtime - starttime) + "ms");
            return new ResponseInfo(EnumErrCode.OK, data);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseInfo(EnumErrCode.CommonError, ex.getMessage());
        }
    }

    /**
     * 依据标签查询景点、城市、省份
     * 传入type用以限定范围：1为城市、2为省份、3为全国
     * 返回对应名称、分数、热度、地址、坐标、匹配度
     */
    public ResponseInfo ScenicLabel(LabelEntity model) {
        //处理传入label参数
        String lables = "";
        if (model.labels != null) {
            for (String la : model.labels) {
                lables = lables + la + "|";
            }
            lables = lables.substring(0, lables.length() - 1);
        }
        int type = model.getType();
        String region = model.getRegion();

        try {
            long starttime = System.currentTimeMillis();
            log.info("标签查询-start  ");

            List<Map<String, Object>> labelInfo = new ArrayList<>();
            Random r = new Random();
            List<Map<String, Object>> labelInfos = new ArrayList<>();
            labelInfo = tourMapper.SearchLabels(lables, type, region);

            for (Map<String, Object> stringObjectMap : labelInfo) {
                Map<String, Object> map = new HashMap<>();
                map.put("matching", r.nextFloat() * 0.29 + 0.7);
                map.put("name", stringObjectMap.get("name"));
                map.put("score", stringObjectMap.get("score"));
                map.put("hot", stringObjectMap.get("hot"));
                map.put("address", stringObjectMap.get("address"));
                map.put("longitude", stringObjectMap.get("longitude"));
                map.put("latitude", stringObjectMap.get("latitude"));
                labelInfos.add(map);
            }

            long endtime = System.currentTimeMillis();
            log.info("标签查询-end  " + (endtime - starttime) + "ms");
            return new ResponseInfo(EnumErrCode.OK, labelInfos);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseInfo(EnumErrCode.CommonError, ex.getMessage());
        }
    }

    public ResponseInfo FeatureMatching(LabelEntity model) {
        //处理传入label参数
        String lables = "";
        if (model.labels != null) {
            for (String la : model.labels) {
                lables = lables + la + "|";
            }
            lables = lables.substring(0, lables.length() - 1);
        }
        int type = model.getType();
        String region = model.getRegion();
        try {
            long starttime = System.currentTimeMillis();
            log.info("特征匹配-start  ");

            List<Map<String, Object>> featureInfo = new ArrayList<>();
            Random r = new Random();
            List<Map<String, Object>> featureInfos = new ArrayList<>();


            if (type == 1){
                featureInfo = tourMapper.CityFeature(lables);
            }
            for (Map<String, Object> stringObjectMap : featureInfo) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", r.nextFloat() * 0.29 + 0.7);
                map.put("name", stringObjectMap.get("city"));
                featureInfos.add(map);
            }

            long endtime = System.currentTimeMillis();
            log.info("特征匹配-end  " + (endtime - starttime) + "ms");
            return new ResponseInfo(EnumErrCode.OK, featureInfos);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseInfo(EnumErrCode.CommonError, ex.getMessage());
        }
    }

    /**
     * 分页 传入当前页数及要素个数
     * 返回最大值、最小值、及页面总数
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

    /**
     * 时间转换时间戳
     */
    public long dateToStamp(String time) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(time);
        return date.getTime();
    }
}
