<template>
    <div>
        <p>
            <button v-show="Tool.hasResource('/signIn/add')" v-on:click="add()" class="btn btn-white btn-default btn-round">
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
                <th>签到福利</th>
                <th>VIP等级</th>
                <th>第几天签到</th>
                <th>奖励数额</th>
                <th>奖励类型</th>
                <th>创建时间</th>
                <th>操作</th>
            </tr>
            </thead>

            <tbody>
            <tr v-for="signIn in signIns">
                <td>{{signIn.id}}</td>
                <td>{{signIn.vipLevel}}</td>
                <td>{{signIn.day}}</td>
                <td>{{signIn.award}}</td>
                <td v-show="signIn.awardType===1">游戏币</td>
                <td>{{signIn.createTime}}</td>
                <td>
                    <div class="hidden-sm hidden-xs btn-group">
                        <button v-show="Tool.hasResource('/signIn/edit')" v-on:click="edit(signIn)" class="btn btn-xs btn-info">
                            <i class="ace-icon fa fa-pencil bigger-120"></i>
                        </button>
                        <button v-show="Tool.hasResource('/signIn/delete/')" v-on:click="del(signIn.id)" class="btn btn-xs btn-danger">
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
                                <label class="col-sm-2 control-label">VIP等级</label>
                                <div class="col-sm-10">
                                    <input v-model="signIn.vipLevel" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">第几天签到</label>
                                <div class="col-sm-10">
                                    <input v-model="signIn.day" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">奖励数额</label>
                                <div class="col-sm-10">
                                    <input v-model="signIn.award" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">奖励类型</label>
                                <div class="col-sm-10">
                                    <select  v-model="signIn.awardType" >
                                        <option value="1"  >游戏币</option>
                                    </select>
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
    </div>
</template>

<script>
    import Pagination from "../../components/pagination";

    export default {
        components: {Pagination},
        name: "signIn",
        data: function () {
            return {
                signIn: {},
                signIns: [],
                page: 1,
                search: "",
                Tool:Tool,
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
                _this.signIn = {};
                _this.signIn.awardType="1";
                $("#form-modal").modal("show");
            },

            /**
             * 点击【编辑】
             */
            edit(signIn) {
                let _this = this;
                _this.signIn = $.extend({}, signIn);
                $("#form-modal").modal("show");
            },

            /**
             * 列表查询
             */
            list(page) {
                let _this = this;
                _this.page = page;
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/signIn/list', {
                    page: page,
                    size: _this.$refs.pagination.size,
                    search: _this.search,
                }).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    _this.signIns = resp.content.list;
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
                    || !Validator.require(_this.signIn.vipLevel, "VIP等级")
                    || !Validator.require(_this.signIn.day, "第几天签到")
                    || !Validator.require(_this.signIn.award, "奖励数额")
                    || !Validator.require(_this.signIn.awardType, "奖励类型")
                ) {
                    return;
                }

                Loading.show();
                if (_this.signIn.id === undefined) {
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/signIn/add', _this.signIn).then((response) => {
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
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/signIn/edit', _this.signIn).then((response) => {
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
                Confirm.show("删除签到福利后不可恢复，确认删除？", function () {
                    Loading.show();
                    _this.$ajax.delete(process.env.VUE_APP_SERVER + '/signIn/delete/' + id).then((response) => {
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
