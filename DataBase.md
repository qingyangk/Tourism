#### 数据库SQL：

**1、评论数据--总表**

`CREATE TABLE `comment` (
  `website` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `author` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `date` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `place` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `score` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `pinglun` varchar(3000) COLLATE utf8mb4_bin DEFAULT NULL,
  `biaoshi` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `province` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '省',
  `city` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '城市'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;`

**2、景点数据--总表**

`CREATE TABLE `scenic` (
  `id` int(11) NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `level` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `region` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `province` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `city` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `x` double DEFAULT NULL,
  `y` double DEFAULT NULL,
  `hot` int(11) DEFAULT NULL,
  `score` double DEFAULT NULL,
  `price` float DEFAULT NULL,
  `sales` float DEFAULT NULL,
  `message` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `details` varchar(3000) COLLATE utf8mb4_bin DEFAULT NULL,
  `hotrank` int(11) DEFAULT NULL,
  `scorerank` int(11) DEFAULT NULL,
  `comrank` float DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;`

**3、用户表**

`CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `username` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `sex` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `anaysis` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `like` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;`

**4、游记--总表**

`CREATE TABLE `youji` (
  `region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `爬取时间` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `content` varchar(6000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `发布时间` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `阅读数` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `出发时间` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `dayCount` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `figure` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `price` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `作者` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `作者住址` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `标识` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;`

