<template>
    <div>
        <p>
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
                <th>ID</th>
                <th>账号</th>
                <th>昵称</th>
                <th>是否代理</th>
                <th>vip</th>
                <th>vip升级经验</th>
                <th>余额</th>
                <th>保险柜余额</th>
                <th>签到天数</th>
                <th>总签到天数</th>
                <th>推广码</th>
                <th>上级ID</th>
                <th>返利比例</th>
                <th>税点</th>
                <th>总奖励</th>
                <th>可提现奖励</th>
                <th>创建时间</th>
                <th>签到时间</th>
                <th>操作</th>
            </tr>
            </thead>

            <tbody>
            <tr v-for="player in playerList">
                <td>{{player.id}}</td>
                <td>{{player.account}}</td>
                <td>{{player.nickName}}</td>
                <td v-show="player.isAgent===1">是</td>
                <td v-show="player.isAgent===0">否</td>
                <td>{{player.vip}}</td>
                <td>{{player.experience}}</td>
                <td>{{player.money}}</td>
                <td>{{player.safeBox}}</td>
                <td>{{player.signIn}}</td>
                <td>{{player.totalSignIn}}</td>
                <td>{{player.spread}}</td>
                <td>{{player.superId}}</td>
                <td>{{player.rebate}}</td>
                <td>{{player.tax}}</td>
                <td>{{player.totalAward}}</td>
                <td>{{player.canAward}}</td>
                <td>{{player.createTime}}</td>
                <td>{{player.signInTime}}</td>

                <td>
                    <div class="hidden-sm hidden-xs btn-group">
                        <button v-show="Tool.hasResource('/player/editCoin/')" v-on:click="editCoin(player,true)"
                                class="btn btn-xs btn-info">
                            上分
                        </button>
                        <button v-show="Tool.hasResource('/player/editCoin/')" v-on:click="editCoin(player,false)"
                                class="btn btn-xs btn-info">
                            下分
                        </button>
                        <button v-show="Tool.hasResource('/editPassword/')" v-on:click="editPassword(player)"
                                class="btn btn-xs btn-info">
                            修改密码
                        </button>
                        <button v-show="Tool.hasResource('/player/edit')" v-on:click="edit(player)"
                                class="btn btn-xs btn-info">
                            <i class="ace-icon fa fa-pencil bigger-120"></i>
                        </button>
                        <button v-show="Tool.hasResource('/player/delete/')" v-on:click="del(player.id)"
                                class="btn btn-xs btn-danger">
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
                        <h4 class="modal-title">{{title}}</h4>
                    </div>
                    <div class="modal-body">
                        <form class="form-horizontal">
                            <div class="form-group">
                                <label class="col-sm-2 control-label">玩家ID</label>
                                <div class="col-sm-10">
                                    <label>
                                        <input v-model="player.id" readonly="readonly" class="form-control">
                                    </label>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">昵称</label>
                                <div class="col-sm-10">
                                    <label>
                                        <input v-model="player.nickName" readonly="readonly" class="form-control">
                                    </label>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">vip</label>
                                <div class="col-sm-10">
                                    <label>
                                        <input v-model="player.vip" class="form-control">
                                    </label>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">vip经验</label>
                                <div class="col-sm-10">
                                    <label>
                                        <input v-model="player.experience" class="form-control">
                                    </label>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">余额</label>
                                <div class="col-sm-10">
                                    <label>
                                        <input v-model="player.money" class="form-control">
                                    </label>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">保险柜余额</label>
                                <div class="col-sm-10">
                                    <label>
                                        <input v-model="player.safeBox" class="form-control">
                                    </label>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">签到天数</label>
                                <div class="col-sm-10">
                                    <label>
                                        <input v-model="player.signIn" class="form-control">
                                    </label>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">总签到天数</label>
                                <div class="col-sm-10">
                                    <label>
                                        <input v-model="player.totalSignIn" class="form-control">
                                    </label>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">返点</label>
                                <div class="col-sm-10">
                                    <label>
                                        <input v-model="player.tax" class="form-control">
                                    </label>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">返利</label>
                                <div class="col-sm-10">
                                    <label>
                                        <input v-model="player.rebate" class="form-control">
                                    </label>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        <button v-on:click="update()" type="button" class="btn btn-primary">保存</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!-- /.modal -->
        <div id="form-modal2" class="modal fade" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">{{title}}</h4>
                    </div>
                    <div class="modal-body">
                        <form class="form-horizontal">
                            <div class="form-group">
                                <label class="col-sm-2 control-label">玩家ID</label>
                                <div class="col-sm-10">
                                    <label>
                                        <input v-model="player.id" readonly="readonly" class="form-control">
                                    </label>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">余额</label>
                                <div class="col-sm-10">
                                    <label>
                                        <input v-model="player.money" readonly class="form-control">
                                    </label>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">{{title}}分数</label>
                                <div class="col-sm-10">
                                    <label>
                                        <input v-model="addMoney" class="form-control">
                                    </label>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        <button v-on:click="editCoinSubmit()" type="button" class="btn btn-primary">{{title}}</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!-- /.modal -->

        <div id="form-modal3" class="modal fade" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">{{title}}</h4>
                    </div>
                    <div class="modal-body">
                        <form class="form-horizontal">
                            <div class="form-group">
                                <label class="col-sm-2 control-label">玩家ID</label>
                                <div class="col-sm-10">
                                    <label>
                                        <input v-model="player.id" readonly="readonly" class="form-control">
                                    </label>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">玩家昵称</label>
                                <div class="col-sm-10">
                                    <label>
                                        <input v-model="player.nickName" readonly="readonly" class="form-control">
                                    </label>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">新密码</label>
                                <div class="col-sm-10">
                                    <label>
                                        <input v-model="password" class="form-control">
                                    </label>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        <button v-on:click="editPasswordSubmit()" type="button" class="btn btn-primary">{{title}}
                        </button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!-- /.modal -->
    </div>
</template>

<script>
    import Pagination from "../../components/pagination";
    import player from "../../components/player";

    export default {
        components: {Pagination},
        name: "business-tGPlayerClub",
        data: function () {
            return {
                player: {},
                playerList: [],
                title: "",
                vAddCoin: "",
                search: "",
                page: "",
                addMoney: "",
                isAdd: "",
                Tool: Tool,
                password: "",
            }
        },
        mounted: function () {
            let _this = this;
            _this.$refs.pagination.size = 10;
            _this.list(1);
        },
        methods: {
            editCoin(player, add) {
                this.addMoney = "";
                if (add) {
                    //上分
                    this.title = "上分";
                    this.isAdd = true;
                } else {
                    //下分
                    this.title = "下分";
                    this.isAdd = false;
                }
                let _this = this;
                _this.player = $.extend({}, player);
                $("#form-modal2").modal("show");
            },
            editPassword(player) {
                this.title = "修改密码";
                let _this = this;
                _this.password = "";
                _this.player = $.extend({}, player);
                $("#form-modal3").modal("show");
            },
            editCoinSubmit() {
                let _this = this;
                // 保存校验
                if (1 !== 1
                    || !Validator.require(_this.player.id, "玩家ID")
                    || !Validator.require(_this.addMoney, "添加分数")
                ) {
                    return;
                }
                if (!_this.isAdd) {
                    _this.addMoney = -_this.addMoney;
                }
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/player/editCoin/' + _this.player.id + "/" + _this.addMoney).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    if (resp.success) {
                        $("#form-modal2").modal("hide");
                        _this.list(_this.page);
                        Toast.success("保存成功！");
                    } else {
                        Toast.warning(resp.message)
                    }
                })
            },
            editPasswordSubmit() {
                let _this = this;
                // 保存校验
                if (1 !== 1
                    || !Validator.require(_this.player.id, "玩家ID")
                    || !Validator.require(_this.password, "新密码")
                ) {
                    return;
                }
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/player/editPassword/' + _this.player.id + "/" + _this.password).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    if (resp.success) {
                        $("#form-modal3").modal("hide");
                        _this.list(_this.page);
                        Toast.success("保存成功！");
                    } else {
                        Toast.warning(resp.message)
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
                console.log("当前search" + this.search);
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/player/list', {
                    page: page,
                    size: _this.$refs.pagination.size,
                    search: _this.search,
                }).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    _this.playerList = resp.content.list;
                    _this.$refs.pagination.render(page, resp.content.total);
                    _this.search = resp.content.search;
                })
            },
            /**
             * 点击修改
             */
            edit(player) {
                this.title = "修改玩家";
                let _this = this;
                _this.player = $.extend({}, player);
                $("#form-modal").modal("show");
            },
            /**
             * 修改提交
             */
            update() {
                let _this = this;
                // 保存校验
                if (1 !== 1
                    || !Validator.require(_this.player.id, "玩家ID")
                    || !Validator.requireValue(_this.player.vip < 0, "vip等级不能小于0")
                    || !Validator.requireValue(_this.player.experience < 0, "vip经验不能小于0")
                    || !Validator.requireValue(_this.player.money < 0, "余额不能小于0")
                    || !Validator.requireValue(_this.player.safeBox < 0, "保险柜余额不能小于0")
                    || !Validator.requireValue(_this.player.signIn < 0, "签到不能小于0")
                    || !Validator.requireValue(_this.player.totalSignIn < 0, "总签到不能小于0")
                    || !Validator.requireValue(_this.player.tax < 0 || _this.player.tax > 100, "返点不能小于0也不能大于100")
                    || !Validator.requireValue(_this.player.rebate < 0 || _this.player.rebate > 100, "返利不能小于0也不能大于100")
                ) {
                    return;
                }
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/player/edit', _this.player).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    if (resp.success) {
                        $("#form-modal").modal("hide");
                        _this.list(_this.page);
                        Toast.success("保存成功！");
                    } else {
                        Toast.warning(resp.message)
                    }
                })
            },
            /**
             * 点击【删除】
             */
            del(id) {
                let _this = this;
                Confirm.show("删除后不可恢复，确认删除？", function () {
                    Loading.show();
                    _this.$ajax.delete(process.env.VUE_APP_SERVER + '/player/delete/' + id).then((response) => {
                        Loading.hide();
                        let resp = response.data;
                        if (resp.success) {
                            _this.list(_this.page);
                            Toast.success("删除成功！");
                        }else{
                            Toast.error(resp.message);
                        }
                    })
                });
            }
        }
    }
</script>
<style>
</style>
