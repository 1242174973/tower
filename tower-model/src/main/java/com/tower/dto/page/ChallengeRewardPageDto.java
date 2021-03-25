package com.tower.dto.page;

import com.tower.dto.ChallengeRewardDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class ChallengeRewardPageDto extends PageDto<ChallengeRewardDto>{
    private String search;
}
