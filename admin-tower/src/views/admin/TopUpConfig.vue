<template>
    <div>
        <p>
            <button v-show="Tool.hasResource('/topUpConfig/add')" v-on:click="add()" class="btn btn-white btn-default btn-round">
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
                <th>充值配置</th>
                <th>充值类型</th>
                <th>转账方式</th>
                <th>银行卡名</th>
                <th>收款人</th>
                <th>银行卡号</th>
                <th>支行</th>
                <th>最小充值</th>
                <th>最大充值</th>
                <th>创建时间</th>
                <th>操作</th>
            </tr>
            </thead>

            <tbody>
            <tr v-for="topUpConfig in topUpConfigs">
                <td>{{topUpConfig.id}}</td>
                <td v-show="topUpConfig.type===1">官方</td>
                <td v-show="topUpConfig.type===2">支付宝</td>
                <td v-show="topUpConfig.type===3">银联</td>
                <td>{{topUpConfig.model}}</td>
                <td>{{topUpConfig.bankCardName}}</td>
                <td>{{topUpConfig.payee}}</td>
                <td>{{topUpConfig.bankCardNum}}</td>
                <td>{{topUpConfig.subBranch}}</td>
                <td>{{topUpConfig.minTopUp}}</td>
                <td>{{topUpConfig.maxTopUp}}</td>
                <td>{{topUpConfig.createTime}}</td>
                <td>
                    <div class="hidden-sm hidden-xs btn-group">
                        <button v-show="Tool.hasResource('/topUpConfig/edit')" v-on:click="edit(topUpConfig)" class="btn btn-xs btn-info">
                            <i class="ace-icon fa fa-pencil bigger-120"></i>
                        </button>
                        <button v-show="Tool.hasResource('/topUpConfig/delete/')" v-on:click="del(topUpConfig.id)" class="btn btn-xs btn-danger">
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
                                <label class="col-sm-2 control-label">充值类型</label>
                                <div class="col-sm-10">
                                    <select v-model="topUpConfig.type">
                                        <option value="1">官方</option>
                                        <option value="2">支付宝</option>
                                        <option value="3">银联</option>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">转账方式</label>
                                <div class="col-sm-10">
                                    <input v-model="topUpConfig.model" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">银行卡名</label>
                                <div class="col-sm-10">
                                    <input v-model="topUpConfig.bankCardName" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">收款人</label>
                                <div class="col-sm-10">
                                    <input v-model="topUpConfig.payee" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">银行卡号</label>
                                <div class="col-sm-10">
                                    <input v-model="topUpConfig.bankCardNum" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">支行</label>
                                <div class="col-sm-10">
                                    <input v-model="topUpConfig.subBranch" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">最小充值</label>
                                <div class="col-sm-10">
                                    <input v-model="topUpConfig.minTopUp" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">最大充值</label>
                                <div class="col-sm-10">
                                    <input v-model="topUpConfig.maxTopUp" class="form-control">
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
        name: "topUpConfig",
        data: function () {
            return {
                topUpConfig: {},
                topUpConfigs: [],
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
                _this.topUpConfig = {};
                _this.topUpConfig.type = 1;
                $("#form-modal").modal("show");
            },

            /**
             * 点击【编辑】
             */
            edit(topUpConfig) {
                let _this = this;
                _this.topUpConfig = $.extend({}, topUpConfig);
                $("#form-modal").modal("show");
            },

            /**
             * 列表查询
             */
            list(page) {
                let _this = this;
                _this.page = page;
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/topUpConfig/list', {
                    page: page,
                    size: _this.$refs.pagination.size,
                    search: _this.search,
                }).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    _this.topUpConfigs = resp.content.list;
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
                    || !Validator.require(_this.topUpConfig.type, "充值类型（1、官方、2支付宝、银联）")
                    || !Validator.require(_this.topUpConfig.model, "转账方式")
                    || !Validator.require(_this.topUpConfig.minTopUp, "最小充值")
                    || !Validator.require(_this.topUpConfig.maxTopUp, "最大充值")
                    || !Validator.length(_this.topUpConfig.model, "转账方式", 1, 255)
                    || !Validator.length(_this.topUpConfig.bankCardName, "银行卡名", 1, 255)
                    || !Validator.length(_this.topUpConfig.payee, "收款人", 1, 255)
                    || !Validator.length(_this.topUpConfig.bankCardNum, "银行卡号", 1, 255)
                    || !Validator.length(_this.topUpConfig.subBranch, "支行", 1, 255)
                ) {
                    return;
                }

                Loading.show();
                if (_this.topUpConfig.id === undefined) {
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/topUpConfig/add', _this.topUpConfig).then((response) => {
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
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/topUpConfig/edit', _this.topUpConfig).then((response) => {
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
                Confirm.show("删除充值信息后不可恢复，确认删除？", function () {
                    Loading.show();
                    _this.$ajax.delete(process.env.VUE_APP_SERVER + '/topUpConfig/delete/' + id).then((response) => {
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
