CREATE TABLE `ws_test`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增编号',
    `store_id`       int(11) DEFAULT NULL COMMENT '门店ID',
    `mer_code`       varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '商户号',
    `child_mer_code` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '子商户号',
    `meal_time`      text COLLATE utf8_bin COMMENT '用户时间JSON',
    `create_time`    varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '创建时间',
    `create_user`    varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '创建人',
    `update_time`    varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '更新时间',
    `update_user`    varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='门店表配置表';