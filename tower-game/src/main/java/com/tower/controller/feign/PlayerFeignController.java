package com.tower.controller.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.TowerApplication;
import com.tower.core.utils.PlayerUtils;
import com.tower.entity.*;
import com.tower.service.*;
import com.tower.service.my.MyChallengeRewardService;
import com.tower.service.my.MySalvageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/26 16:35
 */
@RestController
@RequestMapping("/feign/player")
@Api(value = "玩家请求", tags = "玩家相关请求", hidden = true)
public class PlayerFeignController {

    @Resource
    private AgentRebateService agentRebateService;
    @Resource
    private BetLogService betLogService;
    @Resource
    private MyChallengeRewardService challengeRewardService;
    @Resource
    private ExtracLogService extracLogService;
    @Resource
    private GameLogService gameLogService;
    @Resource
    private ProfitLogService profitLogService;
    @Resource
    private ProfitRebateLogService profitRebateLogService;
    @Resource
    private SafeBoxLogService safeBoxLogService;
    @Resource
    private MySalvageService salvageService;
    @Resource
    private ShareLogService shareLogService;
    @Resource
    private TopUpLogService topUpLogService;
    @Resource
    private TransferLogService transferLogService;
    @Resource
    private WelfareLogService welfareLogService;
    @Resource
    private WithdrawLogService withdrawLogService;
    @Resource
    private PlayerService playerService;


    @PostMapping("/save")
    @ApiOperation(value = "保存玩家信息", notes = "参数 玩家信息对象")
    public void save(@RequestBody Player player) {
        PlayerUtils.savePlayer(player);
    }

    @GetMapping("/playerInfo/{id}")
    @ApiOperation(value = "获得玩家信息", notes = "参数 玩家ID")
    public Player playerInfo(@PathVariable int id) {
        return PlayerUtils.getPlayer(id);
    }

    @GetMapping("/getStatus")
    @ApiOperation(value = "获得游戏状态", notes = "无需参数")
    public int getStatus() {
        return TowerApplication.status;
    }

    @GetMapping("/setStatus/{status}")
    @ApiOperation(value = "获得玩家信息", notes = "参数 玩家ID")
    public void setStatus(@PathVariable int status) {
        TowerApplication.status = status;
    }

    @ApiOperation(value = "获得玩家信息", notes = "参数 玩家ID")
    @GetMapping("/getPlayer/{playerId}")
    public Player getPlayer(@PathVariable int playerId) {
        return PlayerUtils.getPlayer(playerId);
    }

    @ApiOperation(value = "清空数据", notes = "参数 密码")
    @GetMapping("/removeAll/{password}")
    public void removeAll(@PathVariable String password) {
        if (!"qwe123456".equals(password)) {
            return;
        }
        agentRebateService.remove(new LambdaQueryWrapper<AgentRebate>().gt(AgentRebate::getId, 0));
        betLogService.remove(new LambdaQueryWrapper<BetLog>().gt(BetLog::getId, 0));
        challengeRewardService.remove(new LambdaQueryWrapper<ChallengeReward>().gt(ChallengeReward::getId, 0));
        extracLogService.remove(new LambdaQueryWrapper<ExtracLog>().gt(ExtracLog::getId, 0));
        gameLogService.remove(new LambdaQueryWrapper<GameLog>().gt(GameLog::getId, 0));
        profitLogService.remove(new LambdaQueryWrapper<ProfitLog>().gt(ProfitLog::getId, 0));
        profitRebateLogService.remove(new LambdaQueryWrapper<ProfitRebateLog>().gt(ProfitRebateLog::getId, 0));
        safeBoxLogService.remove(new LambdaQueryWrapper<SafeBoxLog>().gt(SafeBoxLog::getId, 0));
        salvageService.remove(new LambdaQueryWrapper<Salvage>().gt(Salvage::getId, 0));
        shareLogService.remove(new LambdaQueryWrapper<ShareLog>().gt(ShareLog::getId, 0));
        topUpLogService.remove(new LambdaQueryWrapper<TopUpLog>().gt(TopUpLog::getId, 0));
        transferLogService.remove(new LambdaQueryWrapper<TransferLog>().gt(TransferLog::getId, 0));
        welfareLogService.remove(new LambdaQueryWrapper<WelfareLog>().gt(WelfareLog::getId, 0));
        withdrawLogService.remove(new LambdaQueryWrapper<WithdrawLog>().gt(WithdrawLog::getId, 0));
        playerService.resetAward();
        List<Player> players = playerService.list();
        for (Player player : players) {
            challengeRewardService.insertToday(player.getId());
            salvageService.insertToday(player.getId());
        }
    }

}
