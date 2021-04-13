<template>
    <div>
        <p>
            <!--         <button v-on:click="add()" class="btn btn-white btn-default btn-round">
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
                <button v-on:click="list(1)" class="form-control">搜索</button>
            </div>
        </div>
        <br>
        <br>
        <pagination ref="pagination" v-bind:list="list" v-bind:itemCount="8"></pagination>

        <table id="simple-table" class="table  table-bordered table-hover">
            <thead>
            <tr>
                <th>版本信息</th>
                <th>平台类型</th>
                <th>强制更新版本</th>
                <th>最新版</th>
                <th>升级描述</th>
                <th>下载地址</th>
                <th>二维码地址</th>
                <th>版本更新时间</th>
                <th>操作</th>
            </tr>
            </thead>

            <tbody>
            <tr v-for="editionEtc in editionEtcs">
                <td>{{editionEtc.id}}</td>
                <td v-show="editionEtc.platform===1">IOS</td>
                <td v-show="editionEtc.platform===2">安卓</td>
                <td>{{editionEtc.forceVersion}}</td>
                <td>{{editionEtc.newest}}</td>
                <td>{{editionEtc.description}}</td>
                <td>{{editionEtc.url}}</td>
                <td>{{editionEtc.dimensional}}</td>
                <td>{{editionEtc.createTime}}</td>
                <td>
                    <div class="hidden-sm hidden-xs btn-group">
                        <button v-on:click="edit(editionEtc)" class="btn btn-xs btn-info">
                            <i class="ace-icon fa fa-pencil bigger-120"></i>
                        </button>
<!--                        <button v-on:click="del(editionEtc.id)" class="btn btn-xs btn-danger">-->
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
<!--                            <div class="form-group">-->
<!--                                <label class="col-sm-2 control-label">平台类型(1:IOS,2:Android)</label>-->
<!--                                <div class="col-sm-10">-->
<!--                                    <input v-model="editionEtc.platform" class="form-control">-->
<!--                                </div>-->
<!--                            </div>-->
                            <div class="form-group">
                                <label class="col-sm-2 control-label">强制更新版本</label>
                                <div class="col-sm-10">
                                    <input v-model="editionEtc.forceVersion" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">最新版</label>
                                <div class="col-sm-10">
                                    <input v-model="editionEtc.newest" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">升级描述</label>
                                <div class="col-sm-10">
                                    <input v-model="editionEtc.description" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">下载地址</label>
                                <div class="col-sm-10">
                                    <input v-model="editionEtc.url" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">二维码地址</label>
                                <div class="col-sm-10">
                                    <input v-model="editionEtc.dimensional" class="form-control">
                                </div>
                            </div>
<!--                            <div class="form-group">-->
<!--                                <label class="col-sm-2 control-label">版本更新时间</label>-->
<!--                                <div class="col-sm-10">-->
<!--                                    <input v-model="editionEtc.createTime" class="form-control">-->
<!--                                </div>-->
<!--                            </div>-->
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
        name: "editionEtc",
        data: function () {
            return {
                editionEtc: {},
                editionEtcs: [],
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
                _this.editionEtc = {};
                $("#form-modal").modal("show");
            },

            /**
             * 点击【编辑】
             */
            edit(editionEtc) {
                let _this = this;
                _this.editionEtc = $.extend({}, editionEtc);
                $("#form-modal").modal("show");
            },

            /**
             * 列表查询
             */
            list(page) {
                let _this = this;
                _this.page = page;
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/editionEtc/list', {
                    page: page,
                    size: _this.$refs.pagination.size,
                    search: _this.search,
                }).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    _this.editionEtcs = resp.content.list;
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
                    || !Validator.require(_this.editionEtc.platform, "平台类型(1:IOS,2:Android)")
                    || !Validator.require(_this.editionEtc.forceVersion, "强制更新版本")
                    || !Validator.require(_this.editionEtc.newest, "最新版")
                    || !Validator.length(_this.editionEtc.description, "升级描述", 1, 255)
                    || !Validator.length(_this.editionEtc.url, "下载地址", 1, 255)
                    || !Validator.length(_this.editionEtc.dimensional, "二维码地址", 1, 255)
                    || !Validator.require(_this.editionEtc.createTime, "版本更新时间")
                ) {
                    return;
                }

                Loading.show();
                if (_this.editionEtc.id === undefined) {
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/editionEtc/add', _this.editionEtc).then((response) => {
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
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/editionEtc/edit', _this.editionEtc).then((response) => {
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
                Confirm.show("删除版本管理后不可恢复，确认删除？", function () {
                    Loading.show();
                    _this.$ajax.delete(process.env.VUE_APP_SERVER + '/editionEtc/delete/' + id).then((response) => {
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
