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
                <th>分享返利表</th>
                <th>分享人id</th>
                <th>收益人id</th>
                <th>分享金额</th>
                <th>分享时间</th>
                <th>操作</th>
            </tr>
            </thead>

            <tbody>
            <tr v-for="shareLog in shareLogs">
                <td>{{shareLog.id}}</td>
                <td>{{shareLog.shareId}}</td>
                <td>{{shareLog.yieldId}}</td>
                <td>{{shareLog.money}}</td>
                <td>{{shareLog.createTime}}</td>
                <td>
                    <div class="hidden-sm hidden-xs btn-group">
                        <!--                        <button v-on:click="edit(shareLog)" class="btn btn-xs btn-info">-->
                        <!--                            <i class="ace-icon fa fa-pencil bigger-120"></i>-->
                        <!--                        </button>-->
                        <button v-show="Tool.hasResource('/shareLog/delete/')" v-on:click="del(shareLog.id)" class="btn btn-xs btn-danger">
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
                                <label class="col-sm-2 control-label">分享人id</label>
                                <div class="col-sm-10">
                                    <input v-model="shareLog.shareId" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">收益人id</label>
                                <div class="col-sm-10">
                                    <input v-model="shareLog.yieldId" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">分享金额</label>
                                <div class="col-sm-10">
                                    <input v-model="shareLog.money" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">分享时间</label>
                                <div class="col-sm-10">
                                    <input v-model="shareLog.createTime" class="form-control">
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
        name: "shareLog",
        data: function () {
            return {
                shareLog: {},
                shareLogs: [],
                page: 1,
                search: "",
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
                _this.shareLog = {};
                $("#form-modal").modal("show");
            },

            /**
             * 点击【编辑】
             */
            edit(shareLog) {
                let _this = this;
                _this.shareLog = $.extend({}, shareLog);
                $("#form-modal").modal("show");
            },

            /**
             * 列表查询
             */
            list(page) {
                let _this = this;
                _this.page = page;
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/shareLog/list', {
                    page: page,
                    size: _this.$refs.pagination.size,
                    search: _this.search,
                }).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    _this.shareLogs = resp.content.list;
                    _this.$refs.pagination.render(page, resp.content.total);

                })
            },

            /**
             * 点击【保存】
             */
            save() {
                let _this = this;


                Loading.show();
                if (_this.shareLog.id === undefined) {
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/shareLog/add', _this.shareLog).then((response) => {
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
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/shareLog/edit', _this.shareLog).then((response) => {
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
                Confirm.show("删除分享记录后不可恢复，确认删除？", function () {
                    Loading.show();
                    _this.$ajax.delete(process.env.VUE_APP_SERVER + '/shareLog/delete/' + id).then((response) => {
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
