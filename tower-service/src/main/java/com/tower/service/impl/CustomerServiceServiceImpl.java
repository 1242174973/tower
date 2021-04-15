package com.tower.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tower.entity.CustomerService;
import com.tower.mapper.CustomerServiceMapper;
import com.tower.service.CustomerServiceService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-04-15
 */
@Service
public class CustomerServiceServiceImpl extends ServiceImpl<CustomerServiceMapper, CustomerService> implements CustomerServiceService {

}
