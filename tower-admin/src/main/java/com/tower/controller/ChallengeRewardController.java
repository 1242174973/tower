package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.ChallengeRewardDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.ChallengeRewardPageDto;
import com.tower.entity.ChallengeReward;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.ChallengeRewardService;
import com.tower.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author xxxx
 */
@RestController
@RequestMapping("/challengeReward")
@Api(value = "挑战福利", tags = "挑战福利相关请求")
public class ChallengeRewardController {

    @Resource
    private ChallengeRewardService challengeRewardService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有挑战福利", notes = "获得所有挑战福利请求")
    public ResponseDto<ChallengeRewardPageDto> list(@RequestBody ChallengeRewardPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<ChallengeReward> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(ChallengeReward::getUserId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.eq(ChallengeReward::getGetRebate, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.eq(ChallengeReward::getChallenge, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.eq(ChallengeReward::getRebate, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(ChallengeReward::getId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(ChallengeReward::getCreateTime);
        Page<ChallengeReward> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = challengeRewardService.page(page, lambdaQueryWrapper);
        List<ChallengeRewardDto> challengeRewardDtoList = CopyUtil.copyList(page.getRecords(), ChallengeRewardDto.class);
        pageDto.setList(challengeRewardDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<ChallengeRewardPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加挑战福利", notes = "添加挑战福利请求")
    public ResponseDto<ChallengeRewardDto> add(@ApiParam(value = "挑战福利信息", required = true)
                                               @RequestBody ChallengeRewardDto challengeRewardDto) {
        requireParam(challengeRewardDto);
        ChallengeReward challengeReward = CopyUtil.copy(challengeRewardDto, ChallengeReward.class);
        challengeReward.setCreateTime(LocalDateTime.now());
        challengeRewardService.save(challengeReward);
        ResponseDto<ChallengeRewardDto> responseDto = new ResponseDto<>();
        challengeRewardDto = CopyUtil.copy(challengeReward, ChallengeRewardDto.class);
        responseDto.setContent(challengeRewardDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改挑战福利", notes = "修改挑战福利请求")
    public ResponseDto<ChallengeRewardDto> edit(@ApiParam(value = "挑战福利信息", required = true)
                                                @RequestBody ChallengeRewardDto challengeRewardDto) {
        requireParam(challengeRewardDto);
        BusinessUtil.require(challengeRewardDto.getId(), BusinessExceptionCode.ID);
        ChallengeReward challengeReward = challengeRewardService.getById(challengeRewardDto.getId());
        BusinessUtil.assertParam(challengeReward != null, "挑战福利没找到");
        challengeRewardService.saveOrUpdate(challengeReward);
        ResponseDto<ChallengeRewardDto> responseDto = new ResponseDto<>();
        challengeRewardDto = CopyUtil.copy(challengeReward, ChallengeRewardDto.class);
        responseDto.setContent(challengeRewardDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除挑战福利", notes = "删除挑战福利请求")
    public ResponseDto<String> delete(@ApiParam(value = "挑战福利ID", required = true)
                                      @PathVariable int id) {
        challengeRewardService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
     * 校验参数
     *
     * @param challengeRewardDto 参数
     */
    private void requireParam(ChallengeRewardDto challengeRewardDto) {

    }
}
