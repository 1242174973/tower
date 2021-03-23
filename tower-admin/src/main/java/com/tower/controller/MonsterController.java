package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.MonsterDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.MonsterPageDto;
import com.tower.entity.Monster;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.MonsterService;
import com.tower.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 梦-屿-千-寻
 */
@RestController
@RequestMapping("/monster")
@Api(value = "怪物", tags = "怪物相关请求")
public class MonsterController {

    @Resource
    private MonsterService monsterService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有怪物", notes = "获得所有怪物请求")
    public ResponseDto<MonsterPageDto> list(@RequestBody MonsterPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<Monster> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(Monster::getId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(Monster::getMonsterName, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(Monster::getTurretName, pageDto.getSearch()));
        }
        Page<Monster> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = monsterService.page(page, lambdaQueryWrapper);
        List<MonsterDto> monsterDtoList = CopyUtil.copyList(page.getRecords(), MonsterDto.class);
        pageDto.setList(monsterDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<MonsterPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加怪物", notes = "添加怪物请求")
    public ResponseDto<MonsterDto> add(@ApiParam(value = "怪物信息", required = true)
                                       @RequestBody MonsterDto monsterDto) {
        requireParam(monsterDto);
        Monster monster = CopyUtil.copy(monsterDto, Monster.class);
        monster.setCreateTime(LocalDateTime.now());
        monsterService.save(monster);
        ResponseDto<MonsterDto> responseDto = new ResponseDto<>();
        monsterDto = CopyUtil.copy(monster, MonsterDto.class);
        responseDto.setContent(monsterDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改怪物", notes = "修改怪物请求")
    public ResponseDto<MonsterDto> edit(@ApiParam(value = "怪物信息", required = true)
                                        @RequestBody MonsterDto monsterDto) {
        requireParam(monsterDto);
        BusinessUtil.require(monsterDto.getId(), BusinessExceptionCode.ID);
        Monster monster = monsterService.getById(monsterDto.getId());
        BusinessUtil.assertParam(monster != null, "怪物没找到");
        monster.setTurretName(monsterDto.getTurretName());
        monster.setMonsterName(monsterDto.getMonsterName());
        monster.setMultiple(monsterDto.getMultiple());
        monster.setMaxBet(monsterDto.getMaxBet());
        monster.setRates(monsterDto.getRates());
        monsterService.saveOrUpdate(monster);
        ResponseDto<MonsterDto> responseDto = new ResponseDto<>();
        monsterDto = CopyUtil.copy(monster, MonsterDto.class);
        responseDto.setContent(monsterDto);
        return responseDto;
    }


    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除怪物", notes = "删除怪物请求")
    public ResponseDto<String> delete(@ApiParam(value = "怪物ID", required = true)
                                      @PathVariable int id) {
        monsterService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }

    /**
     * 校验参数
     *
     * @param monsterDto 参数
     */
    private void requireParam(MonsterDto monsterDto) {
        BusinessUtil.require(monsterDto.getTurretName(), BusinessExceptionCode.TURRET_NAME);
        BusinessUtil.require(monsterDto.getMonsterName(), BusinessExceptionCode.MONSTER_NAME);
        BusinessUtil.require(monsterDto.getMultiple(), BusinessExceptionCode.MULTIPLE);
        BusinessUtil.require(monsterDto.getMaxBet(), BusinessExceptionCode.MAX_BET);
        BusinessUtil.require(monsterDto.getRates(), BusinessExceptionCode.RATES);
    }
}
