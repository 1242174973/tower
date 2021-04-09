<template>
    <div>
        <p>
            <button v-on:click="add()" class="btn btn-white btn-default btn-round">
                <i class="ace-icon fa fa-edit"></i>
                新增
            </button>
            &nbsp;
            <button v-on:click="list(page)" class="btn btn-white btn-default btn-round">
                <i class="ace-icon fa fa-refresh"></i>
                刷新
            </button>
        </p>
        <p>
        <div class="form-group">
            <div class="col-sm-3">
                <label>
                    <input class="form-control" v-model="search"/>
                </label>
            </div>
            <div class="col-sm-2">
                <button v-on:click="list(1)" class="form-control">搜索</button>
            </div>
        </div>
        <br>
        <br>
        <pagination ref="pagination" v-bind:list="list" v-bind:itemCount="8"></pagination>

        <table id="simple-table" class="table  table-bordered table-hover">
            <thead>
            <tr>
                <th>角色表</th>
                <th>角色昵称</th>
                <th>角色描述</th>
                <th>创建时间</th>
                <th>操作</th>
            </tr>
            </thead>

            <tbody>
            <tr v-for="role in roles">
                <td>{{role.id}}</td>
                <td>{{role.roleName}}</td>
                <td>{{role.roleDescribe}}</td>
                <td>{{role.createTime}}</td>
                <td>
                    <div class="hidden-sm hidden-xs btn-group">
                        <button v-on:click="authority(role)" class="btn btn-xs btn-info">
                            权限管理
                        </button>
                        <button v-on:click="edit(role)" class="btn btn-xs btn-info">
                            <i class="ace-icon fa fa-pencil bigger-120"></i>
                        </button>
                        <button v-on:click="del(role.id)" class="btn btn-xs btn-danger">
                            <i class="ace-icon fa fa-trash-o bigger-120"></i>
                        </button>
                    </div>
                </td>
            </tr>
            </tbody>
        </table>

        <div id="form-modal" class="modal fade" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">表单</h4>
                    </div>
                    <div class="modal-body">
                        <form class="form-horizontal">
                            <div class="form-group">
                                <label class="col-sm-2 control-label">角色昵称</label>
                                <div class="col-sm-10">
                                    <input v-model="role.roleName" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">角色描述</label>
                                <div class="col-sm-10">
                                    <input v-model="role.roleDescribe" class="form-control">
                                </div>
                            </div>
                            <!--                                        <div class="form-group">-->
                            <!--                                            <label class="col-sm-2 control-label">创建时间</label>-->
                            <!--                                            <div class="col-sm-10">-->
                            <!--                                                <input v-model="role.createTime" class="form-control">-->
                            <!--                                            </div>-->
                            <!--                                        </div>-->
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        <button v-on:click="save()" type="button" class="btn btn-primary">保存</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!-- /.modal -->
        <div id="form-modal2" class="modal fade" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">表单</h4>
                    </div>
                    <div class="modal-body">
                        <form class="form-horizontal">
                            <div class="form-group">
                                <label class="col-sm-2 control-label">角色</label>
                                <div class="col-sm-10">
                                    <input v-model="role.roleName" readonly class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">权限路径</label>
                                <div class="col-sm-10">
                                    <b v-for="(tGAuthority,key) in tGAuthorityAll"><!--v-model="tGRole.id"-->
                                        <input v-model="tGAuthority.roleIdTrue" type="checkbox"
                                               v-bind:id="tGAuthority.id"
                                               v-bind:value="tGAuthority.id">
                                        <label v-bind:for="tGAuthority.id">&ensp;<b>{{tGAuthority.describe}}&nbsp;&nbsp;&nbsp;</b></label>&ensp;&ensp;
                                        <br v-show="(key+1)%2==0">
                                    </b>
                                    <!--                                    {{tGRoleAll}}-->
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        <button v-on:click="saveAuthority()" type="button" class="btn btn-primary">保存</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!-- /.modal -->
    </div>
</template>

<script>
    import Pagination from "../../components/pagination";

    export default {
        components: {Pagination},
        name: "role",
        data: function () {
            return {
                role: {},
                roles: [],
                page: 1,
                search: "",
                tGAuthorityAll: [],
                roleId: "",
                Tool: Tool,
            }
        },
        mounted: function () {
            let _this = this;
            _this.$refs.pagination.size = 10;
            _this.list(1);
            // sidebar激活样式方法一

        },
        methods: {
            /**
             * 点击【新增】
             */
            add() {
                let _this = this;
                _this.role = {};
                $("#form-modal").modal("show");
            },

            /**
             * 点击【编辑】
             */
            edit(role) {
                let _this = this;
                _this.role = $.extend({}, role);
                $("#form-modal").modal("show");
            },
            /**
             * 点击【保存】
             */
            saveAuthority(){
                let _this = this;
                let map = [];
                for (let i = 0; i < _this.tGAuthorityAll.length; i++) {
                    if (_this.tGAuthorityAll[i].roleIdTrue) {
                        map.push(_this.tGAuthorityAll[i].id)
                    }
                }
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/role/saveAuthority',{
                    authorityId: map,
                    id: _this.role.id,
                }).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    if (resp.success) {
                        $("#form-modal2").modal("hide");
                        _this.list(_this.page);
                        Toast.success("保存成功！");
                    } else {
                        Toast.warning(resp.message)
                    }
                })
            },
            /**
             * 列表查询
             */
            list(page) {
                let _this = this;
                _this.page = page;
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/role/list', {
                    page: page,
                    size: _this.$refs.pagination.size,
                    search: _this.search,
                }).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    _this.roles = resp.content.list;
                    _this.$refs.pagination.render(page, resp.content.total);

                })
            },
            authority(tGRole) {
                let _this = this;
                _this.role = $.extend({}, tGRole);
                Loading.show();
                _this.roleId = tGRole.id;
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/role/authorityAll/' + _this.roleId).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    _this.tGAuthorityAll = resp.content;
                });
                $("#form-modal2").modal("show");
            },
            /**
             * 点击【保存】
             */
            save() {
                let _this = this;

                // 保存校验
                if (1 != 1
                    || !Validator.require(_this.role.roleName, "角色昵称")
                    || !Validator.length(_this.role.roleName, "角色昵称", 1, 255)
                    || !Validator.length(_this.role.roleDescribe, "角色描述", 1, 255)
                ) {
                    return;
                }

                Loading.show();
                if (_this.role.id === undefined) {
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/role/add', _this.role).then((response) => {
                        Loading.hide();
                        let resp = response.data;
                        if (resp.success) {
                            $("#form-modal").modal("hide");
                            _this.list(_this.page);
                            Toast.success("添加成功！");
                        } else {
                            Toast.warning(resp.message);
                        }
                    })
                } else {
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/role/edit', _this.role).then((response) => {
                        Loading.hide();
                        let resp = response.data;
                        if (resp.success) {
                            $("#form-modal").modal("hide");
                            _this.list(_this.page);
                            Toast.success("保存成功！");
                        } else {
                            Toast.warning(resp.message);
                        }
                    })
                }
            },

            /**
             * 点击【删除】
             */
            del(id) {
                let _this = this;
                Confirm.show("删除角色权限后不可恢复，确认删除？", function () {
                    Loading.show();
                    _this.$ajax.delete(process.env.VUE_APP_SERVER + '/role/delete/' + id).then((response) => {
                        Loading.hide();
                        let resp = response.data;
                        if (resp.success) {
                            _this.list(_this.page);
                            Toast.success("删除成功！");
                        }
                    })
                });
            }
        }
    }
</script>
