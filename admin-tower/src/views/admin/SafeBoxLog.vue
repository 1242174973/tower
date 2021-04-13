<template>
    <div>
        <p>
            <!--            <button v-on:click="add()" class="btn btn-white btn-default btn-round">-->
            <!--                <i class="ace-icon fa fa-edit"></i>-->
            <!--                新增-->
            <!--            </button>-->
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
                <th>记录值</th>
                <th>玩家ID</th>
                <th>存取分</th>
                <th>存取时间</th>
                <th>操作</th>
            </tr>
            </thead>

            <tbody>
            <tr v-for="safeBoxLog in safeBoxLogs">
                <td>{{safeBoxLog.id}}</td>
                <td>{{safeBoxLog.userId}}</td>
                <td>{{safeBoxLog.withdraw}}</td>
                <td>{{safeBoxLog.createTime}}</td>
                <td>
                    <div class="hidden-sm hidden-xs btn-group">
                        <!--                      <button v-on:click="edit(safeBoxLog)" class="btn btn-xs btn-info">
                                                  <i class="ace-icon fa fa-pencil bigger-120"></i>
                                              </button>-->
                        <button v-show="Tool.hasResource('/safeBoxLog/delete/')" v-on:click="del(safeBoxLog.id)" class="btn btn-xs btn-danger">
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
                                <label class="col-sm-2 control-label">玩家ID</label>
                                <div class="col-sm-10">
                                    <input v-model="safeBoxLog.userId" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">存取分</label>
                                <div class="col-sm-10">
                                    <input v-model="safeBoxLog.withdraw" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">存取时间</label>
                                <div class="col-sm-10">
                                    <input v-model="safeBoxLog.createTime" class="form-control">
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
        name: "safeBoxLog",
        data: function () {
            return {
                safeBoxLog: {},
                safeBoxLogs: [],
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
                _this.safeBoxLog = {};
                $("#form-modal").modal("show");
            },

            /**
             * 点击【编辑】
             */
            edit(safeBoxLog) {
                let _this = this;
                _this.safeBoxLog = $.extend({}, safeBoxLog);
                $("#form-modal").modal("show");
            },

            /**
             * 列表查询
             */
            list(page) {
                let _this = this;
                _this.page = page;
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/safeBoxLog/list', {
                    page: page,
                    size: _this.$refs.pagination.size,
                    search: _this.search,
                }).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    _this.safeBoxLogs = resp.content.list;
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
                    || !Validator.require(_this.safeBoxLog.userId, "玩家ID")
                    || !Validator.require(_this.safeBoxLog.withdraw, "存取分")
                    || !Validator.require(_this.safeBoxLog.createTime, "存取时间")
                ) {
                    return;
                }

                Loading.show();
                if (_this.safeBoxLog.id === undefined) {
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/safeBoxLog/add', _this.safeBoxLog).then((response) => {
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
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/safeBoxLog/edit', _this.safeBoxLog).then((response) => {
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
                Confirm.show("删除保险柜存取分记录后不可恢复，确认删除？", function () {
                    Loading.show();
                    _this.$ajax.delete(process.env.VUE_APP_SERVER + '/safeBoxLog/delete/' + id).then((response) => {
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
