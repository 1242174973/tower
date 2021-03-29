package com.tower.controller.feign;

import com.tower.core.utils.PlayerUtils;
import com.tower.entity.Player;
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
@Api(value = "数据请求", tags = "数据相关请求" ,hidden = true)
@Slf4j
public class SnatchFeignController {

    @PostMapping("/data")
    @ApiOperation(value = "接收发送的数据", notes = "参数 数据")
    public void save(@RequestBody Object data) {
        System.out.println(data);
    }
}
