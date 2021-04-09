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
                <th>主键</th>
                <th>登录名</th>
                <th>昵称</th>
                <th>密码(加密后)</th>
                <th>创建时间</th>
                <th>操作</th>
            </tr>
            </thead>

            <tbody>
            <tr v-for="user in users">
                <td>{{user.id}}</td>
                <td>{{user.loginName}}</td>
                <td>{{user.name}}</td>
                <td>{{user.password}}</td>
                <td>{{user.createTime}}</td>
                <td>
                    <div class="hidden-sm hidden-xs btn-group">
                        <button v-on:click="role(user)" class="btn btn-xs btn-info">
                            角色管理
                        </button>
                        <button v-on:click="edit(user)" class="btn btn-xs btn-info">
                            <i class="ace-icon fa fa-pencil bigger-120"></i>
                        </button>
                        <button v-on:click="del(user.id)" class="btn btn-xs btn-danger">
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
                                <label class="col-sm-2 control-label">登录名</label>
                                <div class="col-sm-10">
                                    <input v-model="user.loginName" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">昵称</label>
                                <div class="col-sm-10">
                                    <input v-model="user.name" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">密码</label>
                                <div class="col-sm-10">
                                    <input v-model="user.password" class="form-control">
                                </div>
                            </div>
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
                                <label class="col-sm-2 control-label">昵称</label>
                                <div class="col-sm-10">
                                    <input v-model="user.name" readonly class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">角色</label>
                                <div class="col-sm-10">
                                    <b v-for="(tGRole,key) in tGRoleAll"><!--v-model="tGRole.id"-->
                                        <input v-model="tGRole.roleIdTrue" type="checkbox" v-bind:id="tGRole.id"
                                               v-bind:value="tGRole.id">
                                        <label v-bind:for="tGRole.id">&ensp;<b>{{tGRole.roleName}}</b></label>&ensp;&ensp;
                                        <br v-show="(key+1)%2==0">
                                    </b>
                                    <!--                                    {{tGRoleAll}}-->
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        <button v-on:click="saveRole()" type="button" class="btn btn-primary">保存</button>
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
        name: "user",
        data: function () {
            return {
                user: {},
                users: [],
                page: 1,
                search: "",
                tGRoleAll: [],
                userId: "",
                //拥有的角色
                tGRoles: [],
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
                _this.user = {};
                $("#form-modal").modal("show");
            },
            /**
             * 点击【保存】
             */
            saveRole() {
                let _this = this;
                // 保存校验
                if (1 != 1
                    || !Validator.require(_this.user.loginName, "登陆名")
                    || !Validator.length(_this.user.loginName, "登陆名", 1, 50)
                ) {
                    return;
                }
                let map = [];
                for (let i = 0; i < _this.tGRoleAll.length; i++) {
                    if (_this.tGRoleAll[i].roleIdTrue) {
                        map.push(_this.tGRoleAll[i].id)
                    }
                }
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/user/saveRole', {
                    roleId: map,
                    id: _this.user.id,
                }).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    if (resp.success) {
                        $("#form-modal2").modal("hide");
                        _this.list(1);
                        Toast.success("保存成功！");
                    } else {
                        Toast.warning(resp.message)
                    }
                })
            },
            /**
             * 点击【编辑】
             */
            edit(user) {
                let _this = this;
                _this.user = $.extend({}, user);
                $("#form-modal").modal("show");
            },
            role(tGUser) {
                let _this = this;
                _this.user = $.extend({}, tGUser);
                //拿到user_id ,前往后台获取角色数据
                _this.userId = tGUser.id;
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/user/roleAll/'+_this.userId).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    _this.tGRoleAll = resp.content;
                })
                $("#form-modal2").modal("show");
            },
            /**
             * 列表查询
             */
            list(page) {
                let _this = this;
                _this.page = page;
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/user/list', {
                    page: page,
                    size: _this.$refs.pagination.size,
                    search: _this.search,
                }).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    _this.users = resp.content.list;
                    _this.$refs.pagination.render(page, resp.content.total);

                })
            },

            /**
             * 点击【保存】
             */
            save() {
                let _this = this;

                // 保存校验
                if (1 != 1
                    || !Validator.require(_this.user.loginName, "登录名")
                    || !Validator.length(_this.user.loginName, "登录名", 1, 255)
                    || !Validator.require(_this.user.name, "昵称")
                    || !Validator.length(_this.user.name, "昵称", 1, 255)
                    || !Validator.require(_this.user.password, "密码")
                    || !Validator.length(_this.user.password, "密码", 1, 255)
                ) {
                    return;
                }
                _this.user.password = hex_md5(_this.user.password + KEY);
                Loading.show();
                if (_this.user.id === undefined) {
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/user/add', _this.user).then((response) => {
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
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/user/edit', _this.user).then((response) => {
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
                Confirm.show("删除后台用户后不可恢复，确认删除？", function () {
                    Loading.show();
                    _this.$ajax.delete(process.env.VUE_APP_SERVER + '/user/delete/' + id).then((response) => {
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
