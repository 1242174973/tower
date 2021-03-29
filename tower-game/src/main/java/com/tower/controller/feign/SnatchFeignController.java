package com.tower.controller.feign;

import com.tower.core.utils.PlayerUtils;
import com.tower.entity.Player;
import com.tower.json.AttackInfo;
import com.tower.json.DataLog;
import com.tower.json.StartInfo;
import com.tower.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/27 11:42
 */
@RestController
@RequestMapping("/feign/snatch")
@Api(value = "数据请求", tags = "数据相关请求", hidden = true)
@Slf4j
public class SnatchFeignController {

    @PostMapping("/data")
    @ApiOperation(value = "接收发送的数据", notes = "参数 数据")
    public void dataLog(@RequestBody String dataStr) {
        dataStr = dataStr.substring(1, dataStr.length() - 1).replace("\\\"", "\"");
        System.out.println("收到 记录数据 " + JsonUtils.jsonToPojo(dataStr, DataLog.class));
    }

    @PostMapping("/start")
    @ApiOperation(value = "接收开始请求", notes = "参数 数据")
    public void start(@RequestBody String startStr) {
        startStr = startStr.substring(1, startStr.length() - 1).replace("\\\"", "\"");
        System.err.println("开始" + JsonUtils.jsonToPojo(startStr, StartInfo.class));
    }

    @PostMapping("/attack")
    @ApiOperation(value = "接收出怪数据", notes = "参数 数据")
    public void attack(@RequestBody String attackStr) {
        attackStr = attackStr.substring(1, attackStr.length() - 1).replace("\\\"", "\"");
        System.err.println("出怪" + JsonUtils.jsonToPojo(attackStr, AttackInfo.class));
    }
}
