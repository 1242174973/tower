package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.RoleDto;
import com.tower.dto.UserDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.UserPageDto;
import com.tower.entity.Role;
import com.tower.entity.User;
import com.tower.entity.UserRole;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.RoleService;
import com.tower.service.UserRoleService;
import com.tower.service.UserService;
import com.tower.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author xxxx
 */
@RestController
@RequestMapping("/user")
@Api(value = "后台用户", tags = "后台用户相关请求")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private RoleService roleService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有后台用户", notes = "获得所有后台用户请求")
    public ResponseDto<UserPageDto> list(@RequestBody UserPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(User::getId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(User::getCreateTime);
        Page<User> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = userService.page(page, lambdaQueryWrapper);
        List<UserDto> userDtoList = CopyUtil.copyList(page.getRecords(), UserDto.class);
        pageDto.setList(userDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<UserPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    /**
     * 保存，id有值时更新，无值时新增
     */
    @PostMapping("/saveRole")
    public ResponseDto<UserDto> saveRole(@RequestBody UserDto userDto) {
        // 保存校验
        ValidatorUtil.require(userDto.getId(), "玩家id");
        ResponseDto<UserDto> responseDto = new ResponseDto<>();
        LambdaQueryWrapper<UserRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserRole::getUserId, userDto.getId());
        userRoleService.remove(lambdaQueryWrapper);
        for (int roleId : userDto.getRoleId()) {
            UserRole roleAuthority = new UserRole()
                    .setUserId(userDto.getId()).setRoleId(roleId)
                    .setCreateTime(LocalDateTime.now());
            userRoleService.save(roleAuthority);
        }
        responseDto.setContent(userDto);
        return responseDto;
    }

    /**
     * 列表查询
     */
    @PostMapping("/roleAll/{userId}")
    public ResponseDto<List<RoleDto>> roleAll(@PathVariable int userId) {
        List<Role> roleList = roleService.list();
        LambdaQueryWrapper<UserRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoleList = userRoleService.list(lambdaQueryWrapper);
        List<RoleDto> roleDtoList = CopyUtil.copyList(roleList, RoleDto.class);
        for (RoleDto role : roleDtoList) {
            for (UserRole userRole : userRoleList) {
                if (userRole.getUserId().equals(userId) &&
                        userRole.getRoleId().equals(role.getId())) {
                    role.setRoleIdTrue(true);
                }
            }
        }
        ResponseDto<List<RoleDto>> responseDto = new ResponseDto<>();
        responseDto.setContent(roleDtoList);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加后台用户", notes = "添加后台用户请求")
    public ResponseDto<UserDto> add(@ApiParam(value = "后台用户信息", required = true)
                                    @RequestBody UserDto userDto) {
        requireParam(userDto);
        userDto.setPassword(DigestUtils.md5DigestAsHex(userDto.getPassword().getBytes()));
        User user = CopyUtil.copy(userDto, User.class);
        user.setCreateTime(LocalDateTime.now());
        userService.save(user);
        ResponseDto<UserDto> responseDto = new ResponseDto<>();
        userDto = CopyUtil.copy(user, UserDto.class);
        responseDto.setContent(userDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改后台用户", notes = "修改后台用户请求")
    public ResponseDto<UserDto> edit(@ApiParam(value = "后台用户信息", required = true)
                                     @RequestBody UserDto userDto) {
        requireParam(userDto);
        BusinessUtil.require(userDto.getId(), BusinessExceptionCode.ID);
        userDto.setPassword(DigestUtils.md5DigestAsHex(userDto.getPassword().getBytes()));
        User user = userService.getById(userDto.getId());
        BusinessUtil.assertParam(user != null, "后台用户没找到");
        user.setName(userDto.getName());
        user.setLoginName(userDto.getLoginName());
        user.setPassword(userDto.getPassword());
        userService.saveOrUpdate(user);
        ResponseDto<UserDto> responseDto = new ResponseDto<>();
        userDto = CopyUtil.copy(user, UserDto.class);
        responseDto.setContent(userDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除后台用户", notes = "删除后台用户请求")
    public ResponseDto<String> delete(@ApiParam(value = "后台用户ID", required = true)
                                      @PathVariable int id) {
        userService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
     * 校验参数
     *
     * @param userDto 参数
     */
    private void requireParam(UserDto userDto) {
        BusinessUtil.require(userDto.getLoginName(), BusinessExceptionCode.LOGIN_NAME);
        BusinessUtil.require(userDto.getName(), BusinessExceptionCode.NAME);
        BusinessUtil.require(userDto.getPassword(), BusinessExceptionCode.PASSWORD);
    }
}
