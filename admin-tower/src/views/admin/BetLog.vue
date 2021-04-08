<template>
    <div>
        <p>
            <!--            <button v-on:click="add()" class="btn btn-white btn-default btn-round">-->
            <!--                <i class="ace-icon fa fa-edit"></i>-->
            <!--                新增-->
            <!--            </button>-->
            <!--            &nbsp;-->
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
                <th>下注记录</th>
                <th>记录id</th>
                <th>玩家id</th>
                <th>1号下注分数</th>
                <th>2号下注分数</th>
                <th>3号下注分数</th>
                <th>4号下注分数</th>
                <th>5号下注分数</th>
                <th>6号下注分数</th>
                <th>7号下注分数</th>
                <th>8号下注分数</th>
                <th>中奖分数</th>
                <th>开奖号码</th>
                <th>状态</th>
                <th>下注时间</th>
                <th>开奖时间</th>
                <th>操作</th>
            </tr>
            </thead>

            <tbody>
            <tr v-for="betLog in betLogs">
                <td>{{betLog.id}}</td>
                <td>{{betLog.orderId}}</td>
                <td>{{betLog.userId}}</td>
                <td>{{betLog.oneBet}}</td>
                <td>{{betLog.twoBet}}</td>
                <td>{{betLog.threeBet}}</td>
                <td>{{betLog.fourBet}}</td>
                <td>{{betLog.fiveBet}}</td>
                <td>{{betLog.sixBet}}</td>
                <td>{{betLog.sevenBet}}</td>
                <td>{{betLog.eightBet}}</td>
                <td>{{betLog.resultCoin}}</td>
                <td>{{betLog.resultMonster}}</td>
                <td v-show="betLog.status===1">未开奖</td>
                <td v-show="betLog.status===2">已开奖</td>
                <td v-show="betLog.status===3">撤销下注</td>
                <td>{{betLog.createTime}}</td>
                <td>{{betLog.resultTime}}</td>
                <td>
                    <div class="hidden-sm hidden-xs btn-group">
<!--                        <button v-on:click="edit(betLog)" class="btn btn-xs btn-info">-->
<!--                            <i class="ace-icon fa fa-pencil bigger-120"></i>-->
<!--                        </button>-->
                        <button v-on:click="del(betLog.id)" class="btn btn-xs btn-danger">
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
                                <label class="col-sm-2 control-label">记录id</label>
                                <div class="col-sm-10">
                                    <input v-model="betLog.orderId" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">玩家id</label>
                                <div class="col-sm-10">
                                    <input v-model="betLog.userId" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">1号下注分数</label>
                                <div class="col-sm-10">
                                    <input v-model="betLog.oneBet" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">2号下注分数</label>
                                <div class="col-sm-10">
                                    <input v-model="betLog.twoBet" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">3号下注分数</label>
                                <div class="col-sm-10">
                                    <input v-model="betLog.threeBet" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">4号下注分数</label>
                                <div class="col-sm-10">
                                    <input v-model="betLog.fourBet" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">5号下注分数</label>
                                <div class="col-sm-10">
                                    <input v-model="betLog.fiveBet" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">6号下注分数</label>
                                <div class="col-sm-10">
                                    <input v-model="betLog.sixBet" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">7号下注分数</label>
                                <div class="col-sm-10">
                                    <input v-model="betLog.sevenBet" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">8号下注分数</label>
                                <div class="col-sm-10">
                                    <input v-model="betLog.eightBet" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">中奖分数</label>
                                <div class="col-sm-10">
                                    <input v-model="betLog.resultCoin" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">开奖号码</label>
                                <div class="col-sm-10">
                                    <input v-model="betLog.resultMonster" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">状态（1、未开奖、2、已开奖 3、撤销下注）</label>
                                <div class="col-sm-10">
                                    <input v-model="betLog.status" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">下注时间</label>
                                <div class="col-sm-10">
                                    <input v-model="betLog.createTime" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">开奖时间</label>
                                <div class="col-sm-10">
                                    <input v-model="betLog.resultTime" class="form-control">
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
        name: "betLog",
        data: function () {
            return {
                betLog: {},
                betLogs: [],
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
                _this.betLog = {};
                $("#form-modal").modal("show");
            },

            /**
             * 点击【编辑】
             */
            edit(betLog) {
                let _this = this;
                _this.betLog = $.extend({}, betLog);
                $("#form-modal").modal("show");
            },

            /**
             * 列表查询
             */
            list(page) {
                let _this = this;
                _this.page = page;
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/betLog/list', {
                    page: page,
                    size: _this.$refs.pagination.size,
                    search: _this.search,
                }).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    _this.betLogs = resp.content.list;
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
                    || !Validator.require(_this.betLog.orderId, "记录id")
                    || !Validator.length(_this.betLog.orderId, "记录id", 1, 255)
                    || !Validator.require(_this.betLog.userId, "玩家id")
                    || !Validator.require(_this.betLog.status, "状态（1、未开奖、2、已开奖 3、撤销下注）")
                    || !Validator.require(_this.betLog.createTime, "下注时间")
                ) {
                    return;
                }

                Loading.show();
                if (_this.betLog.id === undefined) {
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/betLog/add', _this.betLog).then((response) => {
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
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/betLog/edit', _this.betLog).then((response) => {
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
                Confirm.show("删除下注记录后不可恢复，确认删除？", function () {
                    Loading.show();
                    _this.$ajax.delete(process.env.VUE_APP_SERVER + '/betLog/delete/' + id).then((response) => {
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
