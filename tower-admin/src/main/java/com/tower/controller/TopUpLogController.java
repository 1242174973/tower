package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.TopUpLogDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.TopUpLogPageDto;
import com.tower.entity.Player;
import com.tower.entity.ProfitLog;
import com.tower.entity.TopUpLog;
import com.tower.entity.User;
import com.tower.enums.AuditType;
import com.tower.exception.BusinessExceptionCode;
import com.tower.feign.PlayerFeign;
import com.tower.service.ProfitLogService;
import com.tower.service.TopUpLogService;
import com.tower.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 梦-屿-千-寻
 */
@RestController
@RequestMapping("/topUpLog")
@Api(value = "充值记录", tags = "充值记录相关请求")
public class TopUpLogController {

    @Resource
    private TopUpLogService topUpLogService;

    @Resource
    private PlayerFeign playerFeign;


    @Resource
    private ProfitLogService profitLogService;

    @PostMapping("/listLog")
    @ApiOperation(value = "获得所有充值记录", notes = "获得所有充值记录请求")
    public ResponseDto<TopUpLogPageDto> listLog(@RequestBody TopUpLogPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<TopUpLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(TopUpLog::getUserId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpLog::getBankCardNum, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpLog::getBankCardName, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpLog::getPayee, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpLog::getOrderId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpLog::getRemit, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpLog::getAudit, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpLog::getAuditId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpLog::getId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.and(wrapper ->
                wrapper.or(w -> w.eq(TopUpLog::getState, AuditType.SUCCESS.getCode()))
                        .or(w -> w.eq(TopUpLog::getState, AuditType.ERROR.getCode()))
        );
        lambdaQueryWrapper.orderByDesc(TopUpLog::getCreateTime);
        Page<TopUpLog> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = topUpLogService.page(page, lambdaQueryWrapper);
        List<TopUpLogDto> topUpLogDtoList = CopyUtil.copyList(page.getRecords(), TopUpLogDto.class);
        pageDto.setList(topUpLogDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<TopUpLogPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/list")
    @ApiOperation(value = "获得所有充值记录", notes = "获得所有充值记录请求")
    public ResponseDto<TopUpLogPageDto> list(@RequestBody TopUpLogPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<TopUpLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(TopUpLog::getUserId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpLog::getBankCardNum, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpLog::getBankCardName, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpLog::getPayee, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpLog::getOrderId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpLog::getRemit, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpLog::getAudit, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpLog::getAuditId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpLog::getId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.and(wrapper ->
                wrapper.or(w -> w.eq(TopUpLog::getState, AuditType.AUDIT.getCode()))
                        .or(w -> w.eq(TopUpLog::getState, AuditType.REMITTANCE.getCode()))
        );
        lambdaQueryWrapper.orderByDesc(TopUpLog::getCreateTime);
        Page<TopUpLog> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = topUpLogService.page(page, lambdaQueryWrapper);
        List<TopUpLogDto> topUpLogDtoList = CopyUtil.copyList(page.getRecords(), TopUpLogDto.class);
        pageDto.setList(topUpLogDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<TopUpLogPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/save")
    @ApiOperation(value = "审核充值记录", notes = "审核充值记录请求")
    public ResponseDto<String> save(User user, @ApiParam(value = "充值记录信息", required = true)
    @RequestBody TopUpLogDto topUpLogDto) {
        TopUpLog topUpLog = topUpLogService.getById(topUpLogDto.getId());
        BusinessUtil.assertParam(topUpLog != null, "充值信息没找到");
        BusinessUtil.assertParam(topUpLog.getState().equals(0), "充值信息不在审核状态");
        topUpLog.setState(topUpLogDto.getState());
        if (topUpLog.getState().equals(10)) {
            topUpLog.setCoin(BigDecimal.valueOf(0));
        }
        topUpLog.setAudit(topUpLogDto.getAudit());
        topUpLog.setAuditId(user.getId());
        topUpLog.setAuditTime(LocalDateTime.now());
        topUpLog.setRemit(topUpLogDto.getRemit());
        topUpLogService.updateById(topUpLog);
        return new ResponseDto<>();
    }

    @PostMapping("/remittance")
    @ApiOperation(value = "上分", notes = "上分")
    public ResponseDto<String> remittance(@ApiParam(value = "上分", required = true)
                                          @RequestBody TopUpLogDto topUpLogDto) {
        TopUpLog topUpLog = topUpLogService.getById(topUpLogDto.getId());
        BusinessUtil.assertParam(topUpLog != null, "充值信息没找到");
        BusinessUtil.assertParam(topUpLog.getState().equals(1), "充值信息不在审核通过状态");
        BusinessUtil.assertParam(topUpLogDto.getCoin().doubleValue() >= 0, "分数不能小于0");
        topUpLog.setState(2);
        topUpLog.setCoin(topUpLogDto.getCoin());
        Player player = playerFeign.getPlayer(topUpLog.getUserId());
        BusinessUtil.assertParam(player != null, "玩家找不到");
        player.setMoney(player.getMoney().add(topUpLogDto.getCoin()));
        List<Player> playerList = new ArrayList<>();
        getAllSuper(player, playerList);
        for (Player superPlayer : playerList) {
            Player lower = getLower(superPlayer, playerList);
            if (lower != null && superPlayer.getRebate().doubleValue() > lower.getRebate().doubleValue()) {
                double rebate = superPlayer.getRebate().doubleValue() - lower.getRebate().doubleValue();
                double removeCoin = (topUpLogDto.getCoin().doubleValue() / 100 * 5) / 2.8 * rebate;
                superPlayer.setCanAward(superPlayer.getCanAward().subtract(BigDecimal.valueOf(removeCoin)));
                superPlayer.setTotalAward(superPlayer.getTotalAward().subtract(BigDecimal.valueOf(removeCoin)));
                ProfitLog profitLog = new ProfitLog().setCreateTime(LocalDateTime.now()).setOrderId("0").setProfitCoin(-removeCoin).setUserId(superPlayer.getId());
                profitLogService.save(profitLog);
                playerFeign.save(superPlayer);
            }
        }
        playerFeign.save(player);
        topUpLogService.updateById(topUpLog);
        return new ResponseDto<>();
    }


    @PostMapping("/add")
    @ApiOperation(value = "添加充值记录", notes = "添加充值记录请求")
    public ResponseDto<TopUpLogDto> add(@ApiParam(value = "充值记录信息", required = true)
                                        @RequestBody TopUpLogDto topUpLogDto) {
        requireParam(topUpLogDto);
        TopUpLog topUpLog = CopyUtil.copy(topUpLogDto, TopUpLog.class);
        topUpLog.setCreateTime(LocalDateTime.now());
        topUpLogService.save(topUpLog);
        ResponseDto<TopUpLogDto> responseDto = new ResponseDto<>();
        topUpLogDto = CopyUtil.copy(topUpLog, TopUpLogDto.class);
        responseDto.setContent(topUpLogDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改充值记录", notes = "修改充值记录请求")
    public ResponseDto<TopUpLogDto> edit(@ApiParam(value = "充值记录信息", required = true)
                                         @RequestBody TopUpLogDto topUpLogDto) {
        requireParam(topUpLogDto);
        BusinessUtil.require(topUpLogDto.getId(), BusinessExceptionCode.ID);
        TopUpLog topUpLog = topUpLogService.getById(topUpLogDto.getId());
        BusinessUtil.assertParam(topUpLog != null, "充值记录没找到");
        topUpLogService.saveOrUpdate(topUpLog);
        ResponseDto<TopUpLogDto> responseDto = new ResponseDto<>();
        topUpLogDto = CopyUtil.copy(topUpLog, TopUpLogDto.class);
        responseDto.setContent(topUpLogDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除充值记录", notes = "删除充值记录请求")
    public ResponseDto<String> delete(@ApiParam(value = "充值记录ID", required = true)
                                      @PathVariable int id) {
        topUpLogService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }

    /**
     * 获得所有上级
     *
     * @param player 玩家
     */
    private void getAllSuper(Player player, List<Player> playerList) {
        if (player == null) {
            return;
        }
        if (!player.getSuperId().equals(0)) {
            getAllSuper(playerFeign.getPlayer(player.getSuperId()), playerList);
        }
        playerList.add(player);
    }

    /**
     * 获得list中的下级
     *
     * @param p          玩家
     * @param playerList 所有玩家列表
     * @return 下级
     */
    private Player getLower(Player p, List<Player> playerList) {
        for (Player player : playerList) {
            if (player.getSuperId().equals(p.getId())) {
                return player;
            }
        }
        return null;
    }

    /**
     * 校验参数
     *
     * @param topUpLogDto 参数
     */
    private void requireParam(TopUpLogDto topUpLogDto) {

    }
}
