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
                                        <th>公告</th>
                        <th>内容</th>
                        <th>发布时间</th>
                <th>操作</th>
            </tr>
            </thead>

            <tbody>
            <tr v-for="notice in notices">
                            <td>{{notice.id}}</td>
                            <td>{{notice.content}}</td>
                            <td>{{notice.createTime}}</td>
                <td>
                    <div class="hidden-sm hidden-xs btn-group">
                        <button v-on:click="edit(notice)" class="btn btn-xs btn-info">
                            <i class="ace-icon fa fa-pencil bigger-120"></i>
                        </button>
<!--                        <button v-on:click="del(notice.id)" class="btn btn-xs btn-danger">-->
<!--                            <i class="ace-icon fa fa-trash-o bigger-120"></i>-->
<!--                        </button>-->
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
                                            <label class="col-sm-2 control-label">内容</label>
                                            <div class="col-sm-10">
                                                <input v-model="notice.content" class="form-control">
                                            </div>
                                        </div>
<!--                                        <div class="form-group">-->
<!--                                            <label class="col-sm-2 control-label">发布时间</label>-->
<!--                                            <div class="col-sm-10">-->
<!--                                                <input v-model="notice.createTime" class="form-control">-->
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
    </div>
</template>

<script>
    import Pagination from "../../components/pagination";

    export default {
        components: {Pagination},
        name: "notice",
        data: function () {
            return {
            notice: {},
            notices: [],
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
                _this.notice = {};
                $("#form-modal").modal("show");
            },

            /**
             * 点击【编辑】
             */
            edit(notice) {
                let _this = this;
                _this.notice = $.extend({}, notice);
                $("#form-modal").modal("show");
            },

            /**
             * 列表查询
             */
            list(page) {
                let _this = this;
                _this.page = page;
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/notice/list', {
                    page: page,
                    size: _this.$refs.pagination.size,
                    search: _this.search,
                }).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    _this.notices = resp.content.list;
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
                    || !Validator.require(_this.notice.content, "内容")
                    || !Validator.length(_this.notice.content, "内容", 1, 255)
                    // || !Validator.require(_this.notice.createTime, "发布时间")
                ) {
                    return;
                }

                Loading.show();
                if (_this.notice.id === undefined) {
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/notice/add', _this.notice).then((response) => {
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
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/notice/edit', _this.notice).then((response) => {
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
            Confirm.show("删除公告后不可恢复，确认删除？", function () {
                Loading.show();
                _this.$ajax.delete(process.env.VUE_APP_SERVER + '/notice/delete/' + id).then((response) => {
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
