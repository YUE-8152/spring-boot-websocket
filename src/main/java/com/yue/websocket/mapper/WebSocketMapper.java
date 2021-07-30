package com.yue.websocket.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @ProjectName: spring-boot-websocket
 * @Package: com.yue.websocket.mapper
 * @ClassName: WebSocketMapper
 * @Author: YUE
 * @Description:
 * @Date: 2021/7/30 9:23
 * @Version: 1.0
 */
@Mapper
public interface WebSocketMapper {
    Integer selectCount();

    int insert(Map<String, Object> map);
}
