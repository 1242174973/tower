package com.tower.service.my.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tower.entity.ChallengeReward;
import com.tower.mapper.my.MyChallengeRewardMapper;
import com.tower.service.my.MyChallengeRewardService;
import com.tower.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-03-24
 */
@Service
public class MyChallengeRewardServiceImpl extends ServiceImpl<MyChallengeRewardMapper, ChallengeReward> implements MyChallengeRewardService {

    @Override
    public Integer selectTotalRebate(int userId) {
        return baseMapper.selectTotalRebate(userId);
    }

    @Override
    public int selectTotalGet(int userId) {
        return baseMapper.selectTotalGet(userId);
    }

    @Override
    public void settlement() {
        baseMapper.settlement();
    }

    @Override
    public void insertToday(int id) {
        LambdaQueryWrapper<ChallengeReward> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChallengeReward::getUserId, id)
                .ge(ChallengeReward::getCreateTime, DateUtils.getDate(0))
                .le(ChallengeReward::getCreateTime, DateUtils.getDate(1));
        ChallengeReward challengeReward = getOne(queryWrapper);
        if (challengeReward == null) {
            challengeReward = new ChallengeReward().setCreateTime(LocalDateTime.now()).setUserId(id)
                    .setStatus(0).setChallenge(BigDecimal.ZERO).setGetRebate(BigDecimal.ZERO).setRebate(BigDecimal.ZERO);
            save(challengeReward);
        }
    }

    @Override
    public void getChallengeReward(List<Integer> rewardIds) {
        baseMapper.getChallengeReward(rewardIds);
    }
}
