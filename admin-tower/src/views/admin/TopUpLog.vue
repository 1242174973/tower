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
                <th>订单号</th>
                <th>玩家ID</th>
                <th>充值金额</th>
                <th>实际到账</th>
                <th>实际上分</th>
                <th>当前状态</th>
                <th>审核说明</th>
                <th>审核人</th>
                <th>充值银行</th>
                <th>充值银行卡号</th>
                <th>汇款人</th>
                <th>创建时间</th>
                <th>审核时间</th>
                <th>操作</th>
            </tr>
            </thead>

            <tbody>
            <tr v-for="topUpLog in topUpLogs">
                <td>{{topUpLog.orderId}}</td>
                <td>{{topUpLog.userId}}</td>
                <td>{{topUpLog.topUpMoney}}</td>
                <td>{{topUpLog.remit}}</td>
                <td>{{topUpLog.coin}}</td>
                <td v-show="topUpLog.state===0">审核中</td>
                <td v-show="topUpLog.state===1">上分中</td>
                <td v-show="topUpLog.state===2">成功</td>
                <td v-show="topUpLog.state===10">失败</td>
                <td>{{topUpLog.audit}}</td>
                <td>{{topUpLog.auditId}}</td>
                <td>{{topUpLog.bankCardName}}</td>
                <td>{{topUpLog.bankCardNum}}</td>
                <td>{{topUpLog.payee}}</td>
                <td>{{topUpLog.createTime}}</td>
                <td>{{topUpLog.auditTime}}</td>
                <td>
                    <div class="hidden-sm hidden-xs btn-group">
                        <button v-show="topUpLog.state===0&&Tool.hasResource('/topUpLog/save')" v-on:click="editOk(topUpLog)"
                                class="btn btn-xs btn-info">
                            审核通过
                        </button>
                        <button v-show="topUpLog.state===0&&Tool.hasResource('/topUpLog/save')" v-on:click="editError(topUpLog)"
                                class="btn btn-xs btn-danger">
                            审核失败
                        </button>
                        <button v-show="topUpLog.state===1&&Tool.hasResource('/topUpLog/remittance')" v-on:click="edit(topUpLog)" class="btn btn-xs btn-info">
                            上分
                        </button>
                    </div>
                    <!-- <div class="hidden-sm hidden-xs btn-group">
                         <button v-on:click="edit(topUpLog)" class="btn btn-xs btn-info">
                             <i class="ace-icon fa fa-pencil bigger-120"></i>
                         </button>
                         <button v-on:click="del(topUpLog.id)" class="btn btn-xs btn-danger">
                             <i class="ace-icon fa fa-trash-o bigger-120"></i>
                         </button>
                     </div>-->
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
                                    <input v-model="topUpLog.userId" readonly class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">充值金额</label>
                                <div class="col-sm-10">
                                    <input v-model="topUpLog.topUpMoney" readonly class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">当前状态</label>
                                <div class="col-sm-10">
                                    <input v-show="topUpLog.state===0" value="审核中" readonly class="form-control">
                                    <input v-show="topUpLog.state===1" value="上分中" readonly class="form-control">
                                    <input v-show="topUpLog.state===2" value="成功" readonly class="form-control">
                                    <input v-show="topUpLog.state===10" value="失败" readonly class="form-control">
                                </div>
                            </div>
                            <div v-show="aType!==3" class="form-group">
                                <label class="col-sm-2 control-label">实际到账</label>
                                <div class="col-sm-10">
                                    <input v-model="topUpLog.remit" class="form-control">
                                </div>
                            </div>
                            <div v-show="aType!==3" class="form-group">
                                <label class="col-sm-2 control-label">审核说明</label>
                                <div class="col-sm-10">
                                    <input v-model="topUpLog.audit" class="form-control">
                                </div>
                            </div>
                            <div v-show="aType===3" class="form-group">
                                <label class="col-sm-2 control-label">上分</label>
                                <div class="col-sm-10">
                                    <input v-model="topUpLog.coin" class="form-control">
                                </div>
                            </div>
                            <div v-show="aType===3" class="form-group">
                                <label class="col-sm-2 control-label">实际到账</label>
                                <div class="col-sm-10">
                                    <input v-model="topUpLog.remit" readonly class="form-control">
                                </div>
                            </div>
                            <div v-show="aType===3" class="form-group">
                                <label class="col-sm-2 control-label">审核说明</label>
                                <div class="col-sm-10">
                                    <input v-model="topUpLog.audit" readonly class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">充值银行</label>
                                <div class="col-sm-10">
                                    <input v-model="topUpLog.bankCardName" readonly class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">充值银行卡号</label>
                                <div class="col-sm-10">
                                    <input v-model="topUpLog.bankCardNum" readonly class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">汇款人</label>
                                <div class="col-sm-10">
                                    <input v-model="topUpLog.payee" readonly class="form-control">
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        <button v-show="aType===3" v-on:click="remittance()" type="button" class="btn btn-primary">上分
                        </button>
                        <button v-show="aType===1" v-on:click="save()" type="button" class="btn btn-primary">审核通过
                        </button>
                        <button v-show="aType===2" v-on:click="save()" type="button" class="btn btn-danger">审核失败
                        </button>

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
        name: "topUpLog",
        data: function () {
            return {
                topUpLog: {},
                topUpLogs: [],
                page: 1,
                search: "",
                aType: 0,
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
                _this.topUpLog = {};
                $("#form-modal").modal("show");
            },

            /**
             * 点击【编辑】
             */
            edit(topUpLog) {
                let _this = this;
                _this.topUpLog = $.extend({}, topUpLog);
                $("#form-modal").modal("show");
                _this.aType = 3;
            },
            editOk(topUpLog) {
                let _this = this;
                _this.topUpLog = $.extend({}, topUpLog);
                $("#form-modal").modal("show");
                _this.aType = 1;
            },
            editError(topUpLog) {
                let _this = this;
                _this.topUpLog = $.extend({}, topUpLog);
                $("#form-modal").modal("show");
                _this.aType = 2;
            },
            remittance() {
                let _this = this;
                // 保存校验
                if (1 != 1
                    || !Validator.require(_this.topUpLog.userId, "玩家id")
                    || !Validator.require(_this.topUpLog.coin, "上分分数")
                ) {
                    return;
                }
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/topUpLog/remittance', _this.topUpLog).then((response) => {
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
            },
            /**
             * 列表查询
             */
            list(page) {
                let _this = this;
                _this.page = page;
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/topUpLog/list', {
                    page: page,
                    size: _this.$refs.pagination.size,
                    search: _this.search,
                }).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    _this.topUpLogs = resp.content.list;
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
                    || !Validator.length(_this.topUpLog.audit, "审核说明", 1, 255)
                    || !Validator.require(_this.topUpLog.remit, "实际到账")
                ) {
                    return;
                }
                if (_this.aType === 1) {
                    _this.topUpLog.state = 1;
                } else {
                    _this.topUpLog.state = 10;
                }
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/topUpLog/save', _this.topUpLog).then((response) => {
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
            },

            /**
             * 点击【删除】
             */
            del(id) {
                let _this = this;
                Confirm.show("删除充值记录后不可恢复，确认删除？", function () {
                    Loading.show();
                    _this.$ajax.delete(process.env.VUE_APP_SERVER + '/topUpLog/delete/' + id).then((response) => {
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
