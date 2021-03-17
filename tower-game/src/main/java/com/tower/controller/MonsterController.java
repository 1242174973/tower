package com.tower.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.dto.MonsterDto;
import com.tower.dto.ResponseDto;
import com.tower.entity.Monster;
import com.tower.service.MonsterService;
import com.tower.utils.CopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-03-17
 */
@RestController
@RequestMapping("/monster")
@Api(value = "怪物信息", tags = "怪物信息相关请求")
public class MonsterController {

    @Resource
    private MonsterService monsterService;

    @GetMapping("/monsterList")
    @ApiOperation(value = "获取所有怪物", notes = "无需参数")
    public ResponseDto<List<MonsterDto>> monsterList() {
        ResponseDto<List<MonsterDto>> responseDto = new ResponseDto<>();
        LambdaQueryWrapper<Monster> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<Monster> monsters = monsterService.getBaseMapper().selectList(lambdaQueryWrapper);
        List<MonsterDto> monsterDtoList = CopyUtil.copyList(monsters, MonsterDto.class);
        responseDto.setContent(monsterDtoList);
        return responseDto;
    }
}

