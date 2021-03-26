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
                <th>提现订单</th>
                <th>玩家id</th>
                <th>提现金额</th>
                <th>手续费金额</th>
                <th>实际汇款</th>
                <th>当前状态</th>
                <th>审核说明</th>
                <th>审核人id</th>
                <th>提现银行</th>
                <th>银行卡号</th>
                <th>创建时间</th>
                <th>审核时间</th>
                <th>操作</th>
            </tr>
            </thead>

            <tbody>
            <tr v-for="withdrawLog in withdrawLogs">
                <td>{{withdrawLog.orderId}}</td>
                <td>{{withdrawLog.userId}}</td>
                <td>{{withdrawLog.withdrawMoney}}</td>
                <td>{{withdrawLog.serviceCharge}}</td>
                <td>{{withdrawLog.remit}}</td>
                <td v-show="withdrawLog.state===0">审核中</td>
                <td v-show="withdrawLog.state===1">汇款中</td>
                <td v-show="withdrawLog.state===2">成功</td>
                <td v-show="withdrawLog.state===10">失败</td>
                <td>{{withdrawLog.audit}}</td>
                <td>{{withdrawLog.auditId}}</td>
                <td>{{withdrawLog.bankCardName}}</td>
                <td>{{withdrawLog.bankCardNum}}</td>
                <td>{{withdrawLog.createTime}}</td>
                <td>{{withdrawLog.auditTime}}</td>
                <td>
                    <div class="hidden-sm hidden-xs btn-group">
                        <!--                        <button v-on:click="edit(withdrawLog)" class="btn btn-xs btn-info">-->
                        <!--                            <i class="ace-icon fa fa-pencil bigger-120"></i>-->
                        <!--                        </button>-->
                        <!--                        <button v-on:click="del(withdrawLog.id)" class="btn btn-xs btn-danger">-->
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
                                <label class="col-sm-2 control-label">玩家id</label>
                                <div class="col-sm-10">
                                    <input v-model="withdrawLog.userId" readonly class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">提现金额</label>
                                <div class="col-sm-10">
                                    <input v-model="withdrawLog.withdrawMoney" readonly class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">手续费金额</label>
                                <div class="col-sm-10">
                                    <input v-model="withdrawLog.serviceCharge" readonly class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">当前状态</label>
                                <div class="col-sm-10">
                                    <input v-show="withdrawLog.state==0" readonly value="审核中" class="form-control">
                                    <input v-show="withdrawLog.state==1" readonly value="汇款中" class="form-control">
                                    <input v-show="withdrawLog.state==2" readonly value="成功" class="form-control">
                                    <input v-show="withdrawLog.state==10" readonly value="失败" class="form-control">
                                </div>
                            </div>
                            <div v-show="withdrawLog.state==1" class="form-group">
                                <label class="col-sm-2 control-label">实际汇款</label>
                                <div class="col-sm-10">
                                    <input v-model="withdrawLog.remit" class="form-control">
                                </div>
                            </div>
                            <div v-show="withdrawLog.state==0" class="form-group">
                                <label class="col-sm-2 control-label">审核说明</label>
                                <div class="col-sm-10">
                                    <input v-model="withdrawLog.audit" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">提现银行</label>
                                <div class="col-sm-10">
                                    <input v-model="withdrawLog.bankCardName" readonly class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">银行卡号</label>
                                <div class="col-sm-10">
                                    <input v-model="withdrawLog.bankCardNum" readonly class="form-control">
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        <button v-show="withdrawLog.aType===1" v-on:click="save()" type="button" class="btn btn-primary">审核通过</button>
                        <button v-show="withdrawLog.aType===2" v-on:click="save()" type="button" class="btn btn-danger">审核失败</button>
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
        name: "withdrawLog",
        data: function () {
            return {
                withdrawLog: {},
                withdrawLogs: [],
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
                _this.withdrawLog = {};
                $("#form-modal").modal("show");
            },

            /**
             * 点击【编辑】
             */
            edit(withdrawLog) {
                let _this = this;
                _this.withdrawLog = $.extend({}, withdrawLog);
                $("#form-modal").modal("show");
            },
            /**
             * 点击【编辑】
             */
            editOk(withdrawLog) {
                let _this = this;
                _this.withdrawLog = $.extend({}, withdrawLog);
                _this.withdrawLog.aType=1;
                $("#form-modal").modal("show");
            },
            /**
             * 点击【编辑】
             */
            editError(withdrawLog) {
                let _this = this;
                _this.withdrawLog = $.extend({}, withdrawLog);
                $("#form-modal").modal("show");
                _this.withdrawLog.aType=2;
            },
            /**
             * 列表查询
             */
            list(page) {
                let _this = this;
                _this.page = page;
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/withdrawLog/listLog', {
                    page: page,
                    size: _this.$refs.pagination.size,
                    search: _this.search,
                }).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    _this.withdrawLogs = resp.content.list;
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
                    || !Validator.require(_this.withdrawLog.userId, "玩家id")
                    || !Validator.require(_this.withdrawLog.audit, "审核说明")
                    || !Validator.length(_this.withdrawLog.audit, "审核说明", 1, 255)
                    || !Validator.require(_this.withdrawLog.aType, "分辨参数")
                ) {
                    return;
                }

                Loading.show();
                if (_this.withdrawLog.aType === 1) {
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/withdrawLog/editOk', _this.withdrawLog).then((response) => {
                        Loading.hide();
                        let resp = response.data;
                        if (resp.success) {
                            $("#form-modal").modal("hide");
                            _this.list(_this.page);
                            Toast.success("操作成功！");
                        } else {
                            Toast.warning(resp.message);
                        }
                    })
                } else {
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/withdrawLog/editError', _this.withdrawLog).then((response) => {
                        Loading.hide();
                        let resp = response.data;
                        if (resp.success) {
                            $("#form-modal").modal("hide");
                            _this.list(_this.page);
                            Toast.success("操作成功！");
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
                Confirm.show("删除提现审核后不可恢复，确认删除？", function () {
                    Loading.show();
                    _this.$ajax.delete(process.env.VUE_APP_SERVER + '/withdrawLog/delete/' + id).then((response) => {
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
