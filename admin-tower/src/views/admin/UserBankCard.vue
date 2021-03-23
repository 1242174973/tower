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
                <th>银行卡绑定信息</th>
                <th>玩家id</th>
                <th>银行卡持卡人</th>
                <th>银行卡号</th>
                <th>银行</th>
                <th>支行</th>
                <th>省</th>
                <th>市</th>
                <th>创建时间</th>
                <th>操作</th>
            </tr>
            </thead>

            <tbody>
            <tr v-for="userBankCard in userBankCards">
                <td>{{userBankCard.id}}</td>
                <td>{{userBankCard.userId}}</td>
                <td>{{userBankCard.bankCardName}}</td>
                <td>{{userBankCard.bankCardNum}}</td>
                <td>{{userBankCard.bank}}</td>
                <td>{{userBankCard.subBranch}}</td>
                <td>{{userBankCard.province}}</td>
                <td>{{userBankCard.city}}</td>
                <td>{{userBankCard.createTime}}</td>
                <td>
                    <div class="hidden-sm hidden-xs btn-group">
<!--                        <button v-on:click="edit(userBankCard)" class="btn btn-xs btn-info">-->
<!--                            <i class="ace-icon fa fa-pencil bigger-120"></i>-->
<!--                        </button>-->
<!--                        <button v-on:click="del(userBankCard.id)" class="btn btn-xs btn-danger">-->
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
                                    <input v-model="userBankCard.userId" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">银行卡持卡人</label>
                                <div class="col-sm-10">
                                    <input v-model="userBankCard.bankCardName" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">银行卡号</label>
                                <div class="col-sm-10">
                                    <input v-model="userBankCard.bankCardNum" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">银行</label>
                                <div class="col-sm-10">
                                    <input v-model="userBankCard.bank" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">支行</label>
                                <div class="col-sm-10">
                                    <input v-model="userBankCard.subBranch" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">省</label>
                                <div class="col-sm-10">
                                    <input v-model="userBankCard.province" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">市</label>
                                <div class="col-sm-10">
                                    <input v-model="userBankCard.city" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">创建时间</label>
                                <div class="col-sm-10">
                                    <input v-model="userBankCard.createTime" class="form-control">
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
        name: "userBankCard",
        data: function () {
            return {
                userBankCard: {},
                userBankCards: [],
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
                _this.userBankCard = {};
                $("#form-modal").modal("show");
            },

            /**
             * 点击【编辑】
             */
            edit(userBankCard) {
                let _this = this;
                _this.userBankCard = $.extend({}, userBankCard);
                $("#form-modal").modal("show");
            },

            /**
             * 列表查询
             */
            list(page) {
                let _this = this;
                _this.page = page;
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/userBankCard/list', {
                    page: page,
                    size: _this.$refs.pagination.size,
                    search: _this.search,
                }).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    _this.userBankCards = resp.content.list;
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
                    || !Validator.require(_this.userBankCard.userId, "玩家id")
                    || !Validator.require(_this.userBankCard.bankCardName, "银行卡持卡人")
                    || !Validator.length(_this.userBankCard.bankCardName, "银行卡持卡人", 1, 255)
                    || !Validator.require(_this.userBankCard.bankCardNum, "银行卡号")
                    || !Validator.length(_this.userBankCard.bankCardNum, "银行卡号", 1, 255)
                    || !Validator.require(_this.userBankCard.bank, "银行")
                    || !Validator.length(_this.userBankCard.bank, "银行", 1, 255)
                    || !Validator.require(_this.userBankCard.subBranch, "支行")
                    || !Validator.length(_this.userBankCard.subBranch, "支行", 1, 255)
                    || !Validator.require(_this.userBankCard.province, "省")
                    || !Validator.length(_this.userBankCard.province, "省", 1, 255)
                    || !Validator.require(_this.userBankCard.city, "市")
                    || !Validator.length(_this.userBankCard.city, "市", 1, 255)
                    || !Validator.require(_this.userBankCard.createTime, "创建时间")
                ) {
                    return;
                }

                Loading.show();
                if (_this.userBankCard.id === undefined) {
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/userBankCard/add', _this.userBankCard).then((response) => {
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
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/userBankCard/edit', _this.userBankCard).then((response) => {
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
                Confirm.show("删除玩家绑卡信息后不可恢复，确认删除？", function () {
                    Loading.show();
                    _this.$ajax.delete(process.env.VUE_APP_SERVER + '/userBankCard/delete/' + id).then((response) => {
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
