<template>
    <div>
        <p>
           <!-- <button v-on:click="add()" class="btn btn-white btn-default btn-round">
                <i class="ace-icon fa fa-edit"></i>
                新增
            </button>-->
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
                <button v-on:click="list(1)" class="form-control">搜索玩家</button>
            </div>
        </div>
        <br>
        <br>
        <pagination ref="pagination" v-bind:list="list" v-bind:itemCount="8"></pagination>

        <table id="simple-table" class="table  table-bordered table-hover">
            <thead>
            <tr>
                                        <th>福利记录表</th>
                        <th>玩家Id</th>
                        <th>福利值</th>
                        <th>福利类型</th>
                        <th>获取方式</th>
                        <th>获取时间</th>
                <th>操作</th>
            </tr>
            </thead>

            <tbody>
            <tr v-for="welfareLog in welfareLogs">
                            <td>{{welfareLog.id}}</td>
                            <td>{{welfareLog.userId}}</td>
                            <td>{{welfareLog.welfare}}</td>
                            <td v-show="welfareLog.welfareType===1">游戏币</td>
                            <td v-show="welfareLog.mode===1">签到</td>
                            <td>{{welfareLog.createTime}}</td>
                <td>
                    <div class="hidden-sm hidden-xs btn-group">
                        <!--<button v-on:click="edit(welfareLog)" class="btn btn-xs btn-info">
                            <i class="ace-icon fa fa-pencil bigger-120"></i>
                        </button>-->
                        <button v-on:click="del(welfareLog.id)" class="btn btn-xs btn-danger">
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
                                            <label class="col-sm-2 control-label">玩家Id</label>
                                            <div class="col-sm-10">
                                                <input v-model="welfareLog.userId" class="form-control">
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label class="col-sm-2 control-label">福利值</label>
                                            <div class="col-sm-10">
                                                <input v-model="welfareLog.welfare" class="form-control">
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label class="col-sm-2 control-label">福利类型</label>
                                            <div class="col-sm-10">
                                                <input v-model="welfareLog.welfareType" class="form-control">
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label class="col-sm-2 control-label">获取方式</label>
                                            <div class="col-sm-10">
                                                <input v-model="welfareLog.mode" class="form-control">
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label class="col-sm-2 control-label">获取时间</label>
                                            <div class="col-sm-10">
                                                <input v-model="welfareLog.createTime" class="form-control">
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
        name: "welfareLog",
        data: function () {
            return {
            welfareLog: {},
            welfareLogs: [],
            page: 1,
            search: "",
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
                _this.welfareLog = {};
                $("#form-modal").modal("show");
            },

            /**
             * 点击【编辑】
             */
            edit(welfareLog) {
                let _this = this;
                _this.welfareLog = $.extend({}, welfareLog);
                $("#form-modal").modal("show");
            },

            /**
             * 列表查询
             */
            list(page) {
                let _this = this;
                _this.page = page;
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/welfareLog/list', {
                    page: page,
                    size: _this.$refs.pagination.size,
                    search: _this.search,
                }).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    _this.welfareLogs = resp.content.list;
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
                    || !Validator.require(_this.welfareLog.userId, "玩家Id")
                    || !Validator.require(_this.welfareLog.welfare, "福利值")
                    || !Validator.require(_this.welfareLog.welfareType, "福利类型")
                    || !Validator.require(_this.welfareLog.mode, "获取方式")
                    || !Validator.require(_this.welfareLog.createTime, "获取时间")
                ) {
                    return;
                }

                Loading.show();
                if (_this.welfareLog.id === undefined) {
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/welfareLog/add', _this.welfareLog).then((response) => {
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
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/welfareLog/edit', _this.welfareLog).then((response) => {
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
            Confirm.show("删除福利记录后不可恢复，确认删除？", function () {
                Loading.show();
                _this.$ajax.delete(process.env.VUE_APP_SERVER + '/welfareLog/delete/' + id).then((response) => {
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
