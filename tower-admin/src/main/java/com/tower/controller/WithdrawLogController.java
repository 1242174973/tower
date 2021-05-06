package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.PlayerDto;
import com.tower.dto.UserDto;
import com.tower.dto.WithdrawLogDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.WithdrawLogPageDto;
import com.tower.entity.Player;
import com.tower.entity.User;
import com.tower.entity.WelfareLog;
import com.tower.entity.WithdrawLog;
import com.tower.enums.AuditType;
import com.tower.enums.WelfareModelEnum;
import com.tower.enums.WelfareTypeEnum;
import com.tower.exception.BusinessExceptionCode;
import com.tower.feign.PlayerFeign;
import com.tower.service.PlayerService;
import com.tower.service.WelfareLogService;
import com.tower.service.WithdrawLogService;
import com.tower.utils.*;
import com.tower.variable.RedisVariable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author xxxx
 */
@RestController
@RequestMapping("/withdrawLog")
@Api(value = "提现审核", tags = "提现审核相关请求")
public class WithdrawLogController {

    @Resource
    private WithdrawLogService withdrawLogService;

    @Resource
    private PlayerFeign playerFeign;


    @PostMapping("/list")
    @ApiOperation(value = "获得所有提现审核", notes = "获得所有提现审核请求")
    public ResponseDto<WithdrawLogPageDto> list(@RequestBody WithdrawLogPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<WithdrawLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper.and(wrapper -> wrapper
                    .or(queryWrapper -> queryWrapper.like(WithdrawLog::getOrderId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(WithdrawLog::getUserId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(WithdrawLog::getBankCardName, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(WithdrawLog::getBankCardNum, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(WithdrawLog::getRemit, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(WithdrawLog::getAudit, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(WithdrawLog::getAuditId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(WithdrawLog::getId, pageDto.getSearch()))
            );
        }
        lambdaQueryWrapper.and(wrapper ->
                wrapper.or(w -> w.eq(WithdrawLog::getState, AuditType.AUDIT.getCode()))
                        .or(w -> w.eq(WithdrawLog::getState, AuditType.REMITTANCE.getCode()))
        );
        lambdaQueryWrapper.orderByDesc(WithdrawLog::getCreateTime).orderByAsc(WithdrawLog::getState);
        Page<WithdrawLog> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = withdrawLogService.page(page, lambdaQueryWrapper);
        List<WithdrawLogDto> withdrawLogDtoList = CopyUtil.copyList(page.getRecords(), WithdrawLogDto.class);
        pageDto.setList(withdrawLogDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<WithdrawLogPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/listLog")
    @ApiOperation(value = "获得所有提现记录", notes = "获得所有提现记录")
    public ResponseDto<WithdrawLogPageDto> listLog(@RequestBody WithdrawLogPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<WithdrawLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper.and(wrapper -> wrapper
                    .or(queryWrapper -> queryWrapper.like(WithdrawLog::getOrderId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(WithdrawLog::getUserId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(WithdrawLog::getBankCardName, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(WithdrawLog::getBankCardNum, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(WithdrawLog::getRemit, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(WithdrawLog::getAudit, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(WithdrawLog::getAuditId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(WithdrawLog::getId, pageDto.getSearch()))
            );
        }
        lambdaQueryWrapper.and(wrapper ->
                wrapper.or(w -> w.eq(WithdrawLog::getState, AuditType.SUCCESS.getCode()))
                        .or(w -> w.eq(WithdrawLog::getState, AuditType.ERROR.getCode()))
        );
        lambdaQueryWrapper.orderByDesc(WithdrawLog::getCreateTime).orderByAsc(WithdrawLog::getState);
        Page<WithdrawLog> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = withdrawLogService.page(page, lambdaQueryWrapper);
        List<WithdrawLogDto> withdrawLogDtoList = CopyUtil.copyList(page.getRecords(), WithdrawLogDto.class);
        pageDto.setList(withdrawLogDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<WithdrawLogPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加提现审核", notes = "添加提现审核请求")
    public ResponseDto<WithdrawLogDto> add(@ApiParam(value = "提现审核信息", required = true)
                                           @RequestBody WithdrawLogDto withdrawLogDto) {
        requireParam(withdrawLogDto);
        WithdrawLog withdrawLog = CopyUtil.copy(withdrawLogDto, WithdrawLog.class);
        withdrawLog.setCreateTime(LocalDateTime.now());
        withdrawLogService.save(withdrawLog);
        ResponseDto<WithdrawLogDto> responseDto = new ResponseDto<>();
        withdrawLogDto = CopyUtil.copy(withdrawLog, WithdrawLogDto.class);
        responseDto.setContent(withdrawLogDto);
        return responseDto;
    }


    @PostMapping("/editOk")
    @ApiOperation(value = "修改提现审核", notes = "修改提现审核请求")
    public ResponseDto<WithdrawLogDto> editOk(UserDto user,
                                              @ApiParam(value = "提现审核信息", required = true)
                                              @RequestBody WithdrawLogDto withdrawLogDto) {
        requireParam(withdrawLogDto);
        BusinessUtil.require(withdrawLogDto.getId(), BusinessExceptionCode.ID);
        WithdrawLog withdrawLog = withdrawLogService.getById(withdrawLogDto.getId());
        BusinessUtil.assertParam(withdrawLog != null, "提现审核没找到");
        withdrawLog.setAudit(withdrawLogDto.getAudit());
        withdrawLog.setAuditId(user.getId());
        withdrawLog.setState(AuditType.REMITTANCE.getCode());
        withdrawLog.setAuditTime(LocalDateTime.now());
        withdrawLogService.saveOrUpdate(withdrawLog);
        ResponseDto<WithdrawLogDto> responseDto = new ResponseDto<>();
        withdrawLogDto = CopyUtil.copy(withdrawLog, WithdrawLogDto.class);
        responseDto.setContent(withdrawLogDto);
        return responseDto;
    }

    @PostMapping("/editError")
    @ApiOperation(value = "审核失败", notes = "审核失败请求")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<WithdrawLogDto> editError(UserDto user,
                                                 @ApiParam(value = "提现审核信息", required = true)
                                                 @RequestBody WithdrawLogDto withdrawLogDto) {
        requireParam(withdrawLogDto);
        BusinessUtil.require(withdrawLogDto.getId(), BusinessExceptionCode.ID);
        WithdrawLog withdrawLog = withdrawLogService.getById(withdrawLogDto.getId());
        BusinessUtil.assertParam(withdrawLog != null, "提现审核没找到");
        withdrawLog.setAudit(withdrawLogDto.getAudit());
        withdrawLog.setAuditId(user.getId());
        withdrawLog.setAuditTime(LocalDateTime.now());
        withdrawLog.setState(AuditType.ERROR.getCode());
        withdrawLog.setRemit(BigDecimal.ZERO);
        withdrawLogService.saveOrUpdate(withdrawLog);
        Player player = playerFeign.playerInfo(withdrawLog.getUserId());
        player.setMoney(player.getMoney().add(withdrawLog.getWithdrawMoney()));
        playerFeign.save(player);
        ResponseDto<WithdrawLogDto> responseDto = new ResponseDto<>();
        withdrawLogDto = CopyUtil.copy(withdrawLog, WithdrawLogDto.class);
        responseDto.setContent(withdrawLogDto);
        return responseDto;
    }


    @PostMapping("/remittance")
    @ApiOperation(value = "汇款", notes = "汇款")
    public ResponseDto<WithdrawLogDto> remittance(
            @ApiParam(value = "提现审核信息", required = true)
            @RequestBody WithdrawLogDto withdrawLogDto) {
        requireParam(withdrawLogDto);
        BusinessUtil.require(withdrawLogDto.getId(), BusinessExceptionCode.ID);
        WithdrawLog withdrawLog = withdrawLogService.getById(withdrawLogDto.getId());
        BusinessUtil.assertParam(withdrawLog != null, "提现审核没找到");
        withdrawLog.setRemit(withdrawLogDto.getRemit());
        withdrawLog.setState(AuditType.SUCCESS.getCode());
        withdrawLogService.saveOrUpdate(withdrawLog);
        ResponseDto<WithdrawLogDto> responseDto = new ResponseDto<>();
        withdrawLogDto = CopyUtil.copy(withdrawLog, WithdrawLogDto.class);
        responseDto.setContent(withdrawLogDto);
        return responseDto;
    }


    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除提现审核", notes = "删除提现审核请求")
    public ResponseDto<String> delete(@ApiParam(value = "提现审核ID", required = true)
                                      @PathVariable int id) {
        withdrawLogService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
     * 校验参数
     *
     * @param withdrawLogDto 参数
     */
    private void requireParam(WithdrawLogDto withdrawLogDto) {
        BusinessUtil.require(withdrawLogDto.getUserId(), BusinessExceptionCode.USER_ID);
    }
}
