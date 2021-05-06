package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.AuthorityPathDto;
import com.tower.dto.RoleDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.RolePageDto;
import com.tower.entity.AuthorityPath;
import com.tower.entity.Role;
import com.tower.entity.RoleAuthority;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.AuthorityPathService;
import com.tower.service.RoleAuthorityService;
import com.tower.service.RoleService;
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
@RequestMapping("/role")
@Api(value = "角色权限", tags = "角色权限相关请求")
public class RoleController {

    @Resource
    private RoleService roleService;

    @Resource
    private AuthorityPathService authorityPathService;

    @Resource
    private RoleAuthorityService roleAuthorityService;


    @PostMapping("/list")
    @ApiOperation(value = "获得所有角色权限", notes = "获得所有角色权限请求")
    public ResponseDto<RolePageDto> list(@RequestBody RolePageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<Role> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(Role::getId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(Role::getRoleName, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(Role::getRoleDescribe, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(Role::getCreateTime);
        Page<Role> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = roleService.page(page, lambdaQueryWrapper);
        List<RoleDto> roleDtoList = CopyUtil.copyList(page.getRecords(), RoleDto.class);
        pageDto.setList(roleDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<RolePageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加角色权限", notes = "添加角色权限请求")
    public ResponseDto<RoleDto> add(@ApiParam(value = "角色权限信息", required = true)
                                    @RequestBody RoleDto roleDto) {
        requireParam(roleDto);
        Role role = CopyUtil.copy(roleDto, Role.class);
        role.setCreateTime(LocalDateTime.now());
        roleService.save(role);
        ResponseDto<RoleDto> responseDto = new ResponseDto<>();
        roleDto = CopyUtil.copy(role, RoleDto.class);
        responseDto.setContent(roleDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改角色权限", notes = "修改角色权限请求")
    public ResponseDto<RoleDto> edit(@ApiParam(value = "角色权限信息", required = true)
                                     @RequestBody RoleDto roleDto) {
        requireParam(roleDto);
        BusinessUtil.require(roleDto.getId(), BusinessExceptionCode.ID);
        Role role = roleService.getById(roleDto.getId());
        BusinessUtil.assertParam(role != null, "角色权限没找到");
        role.setRoleDescribe(roleDto.getRoleDescribe());
        role.setRoleName(roleDto.getRoleName());
        roleService.saveOrUpdate(role);
        ResponseDto<RoleDto> responseDto = new ResponseDto<>();
        roleDto = CopyUtil.copy(role, RoleDto.class);
        responseDto.setContent(roleDto);
        return responseDto;
    }

    /**
     * 列表查询
     */
    @PostMapping("/authorityAll/{roleId}")
    public ResponseDto<List<AuthorityPathDto>> authorityAll(@PathVariable int roleId) {
        List<AuthorityPath> authorityPaths = authorityPathService.list();
        LambdaQueryWrapper<RoleAuthority> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RoleAuthority::getRoleId, roleId);
        List<RoleAuthority> roleAuthorityList = roleAuthorityService.list(lambdaQueryWrapper);
        List<AuthorityPathDto> pathDtoList = CopyUtil.copyList(authorityPaths, AuthorityPathDto.class);
        for (AuthorityPathDto authorityPathDto : pathDtoList) {
            for (RoleAuthority roleAuthority : roleAuthorityList) {
                if (roleAuthority.getRoleId().equals(roleId) &&
                        roleAuthority.getAuthorityId().equals(authorityPathDto.getId())) {
                    authorityPathDto.setRoleIdTrue(true);
                }
            }
        }
        ResponseDto<List<AuthorityPathDto>> responseDto = new ResponseDto<>();
        responseDto.setContent(pathDtoList);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除角色权限", notes = "删除角色权限请求")
    public ResponseDto<String> delete(@ApiParam(value = "角色权限ID", required = true)
                                      @PathVariable int id) {
        roleService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }

    /**
     * 保存，id有值时更新，无值时新增
     */
    @PostMapping("/saveAuthority")
    public ResponseDto<RoleDto> saveAuthority(@RequestBody RoleDto roleDto) {
        // 保存校验
        ValidatorUtil.require(roleDto.getId(), "角色id");
        ResponseDto<RoleDto> responseDto = new ResponseDto<>();
        LambdaQueryWrapper<RoleAuthority> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RoleAuthority::getRoleId, roleDto.getId());
        roleAuthorityService.remove(lambdaQueryWrapper);
        for (int authorityId : roleDto.getAuthorityId()) {
            RoleAuthority roleAuthority = new RoleAuthority().setAuthorityId(authorityId).setRoleId(roleDto.getId()).setCreateTime(LocalDateTime.now());
            roleAuthorityService.save(roleAuthority);
        }
        responseDto.setContent(roleDto);
        return responseDto;
    }

    /**
     * 校验参数
     *
     * @param roleDto 参数
     */
    private void requireParam(RoleDto roleDto) {
        BusinessUtil.assertParam(roleDto.getRoleName() != null, "角色名称不能为空");
        BusinessUtil.assertParam(roleDto.getRoleDescribe() != null, "角色描述不能为空");
    }
}
