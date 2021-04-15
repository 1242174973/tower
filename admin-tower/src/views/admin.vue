<template>
    <div>
        <div id="navbar" class="navbar navbar-default          ace-save-state">
            <div class="navbar-container ace-save-state" id="navbar-container">
                <button type="button" class="navbar-toggle menu-toggler pull-left" id="menu-toggler"
                        data-target="#sidebar">
                    <span class="sr-only">Toggle sidebar</span>

                    <span class="icon-bar"></span>

                    <span class="icon-bar"></span>

                    <span class="icon-bar"></span>
                </button>

                <div class="navbar-header pull-left">
                    <a href="index.html" class="navbar-brand">
                        <small>
                            <i class="fa fa-leaf"></i>
                            后台管理
                        </small>
                    </a>
                </div>

                <div class="navbar-buttons navbar-header pull-right" role="navigation">
                    <ul class="nav ace-nav">
                        <li class="light-blue dropdown-modal">
                            <a data-toggle="dropdown" href="#" class="dropdown-toggle">
                                <img class="nav-user-photo" src="../../public/ace/assets/images/avatars/user.jpg"
                                     alt="Jason's Photo"/>
                                <i class="ace-icon fa fa-caret-down"></i>
                            </a>

                            <ul class="user-menu dropdown-menu-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">
                                <li>
                                    <a v-on:click="updatePassword()" href="#">
                                        <i class="icon-cog fa fa-cog"></i>
                                        修改密码
                                    </a>
                                </li>
                                <li class="divider"></li>
                                <li>
                                    <a v-on:click="logout()" href="#">
                                        <i class="ace-icon fa fa-power-off"></i>
                                        退出登录
                                    </a>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="main-container ace-save-state" id="main-container">
            <div id="sidebar" class="sidebar                  ace-save-state">
                <!--responsive-->
                <div class="sidebar-shortcuts" id="sidebar-shortcuts">
                    <div class="sidebar-shortcuts-large" id="sidebar-shortcuts-large">
                        <button class="btn btn-success">
                            <i class="ace-icon fa fa-signal"></i>
                        </button>

                        <button class="btn btn-info">
                            <i class="ace-icon fa fa-pencil"></i>
                        </button>

                        <button class="btn btn-warning">
                            <i class="ace-icon fa fa-users"></i>
                        </button>

                        <button class="btn btn-danger">
                            <i class="ace-icon fa fa-cogs"></i>
                        </button>
                    </div>

                    <div class="sidebar-shortcuts-mini" id="sidebar-shortcuts-mini">
                        <span class="btn btn-success"></span>

                        <span class="btn btn-info"></span>

                        <span class="btn btn-warning"></span>

                        <span class="btn btn-danger"></span>
                    </div>
                </div><!-- /.sidebar-shortcuts -->

                <ul class="nav nav-list">
                    <li class="" id="welcome-sidebar">
                        <router-link to="/welcome">
                            <i class="menu-icon fa fa-tachometer"></i>
                            <span class="menu-text"> 欢迎：{{loginUser.name}} </span>
                        </router-link>

                        <b class="arrow"></b>
                    </li>

                    <li v-show="hasResource('/player/list')||hasResource('/editionEtc/list')||hasResource('/monster/list')||hasResource('/notice/list')"
                        class="">
                        <a href="#" class="dropdown-toggle">
                            <i class="menu-icon fa fa-list"></i>
                            <span class="menu-text"> 游戏管理 </span>
                            <b class="arrow fa fa-angle-down"></b>
                        </a>
                        <b class="arrow"></b>
                        <ul class="submenu">
                            <li v-show="hasResource('/player/list')" class="" id="game-player-sidebar">
                                <router-link to="/game/player">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    玩家管理
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/monster/list')" class="" id="game-monster-sidebar">
                                <router-link to="/game/monster">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    怪物管理
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/notice/list')" class="" id="game-notice-sidebar">
                                <router-link to="/game/notice">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    公告管理
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/editionEtc/list')" class="" id="game-editionEtc-sidebar">
                                <router-link to="/game/editionEtc">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    版本管理
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/customerService/list')" class="" id="game-customerService-sidebar">
                                <router-link to="/game/customerService">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    客服管理
                                </router-link>
                                <b class="arrow"></b>
                            </li>

                        </ul>


                    <li v-show="hasResource('/signIn/list')||hasResource('/salvage/list')||hasResource('/challengeReward/list')"
                        class="">
                        <a href="#" class="dropdown-toggle">
                            <i class="menu-icon fa fa-list"></i>
                            <span class="menu-text"> 福利管理 </span>

                            <b class="arrow fa fa-angle-down"></b>
                        </a>

                        <ul class="submenu">
                            <li v-show="hasResource('/signIn/list')" class="" id="welfare-signIn-sidebar">
                                <router-link to="/welfare/signIn">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    签到福利
                                </router-link>

                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/salvage/list')" class="" id="welfare-salvage-sidebar">
                                <router-link to="/welfare/salvage">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    救援金福利
                                </router-link>

                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/challengeReward/list')" class=""
                                id="welfare-challengeReward-sidebar">
                                <router-link to="/welfare/challengeReward">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    挑战福利
                                </router-link>

                                <b class="arrow"></b>
                            </li>

                        </ul>
                    </li>


                    <li v-show="hasResource('/userWithdrawConfig/list')
                        ||hasResource('/topUpConfig/list')||hasResource('/userBankCard/list')
                        ||hasResource('/withdrawLog/list')||hasResource('/withdrawLog/listLog')
                        ||hasResource('/topUpLog/list')||hasResource('/topUpLog/listLog')"
                        class="">
                        <a href="#" class="dropdown-toggle">
                            <i class="menu-icon fa fa-list"></i>
                            <span class="menu-text"> 充值提现管理 </span>

                            <b class="arrow fa fa-angle-down"></b>
                        </a>

                        <ul class="submenu">
                            <li v-show="hasResource('/userWithdrawConfig/list')" class=""
                                id="withdraw-userWithdrawConfig-sidebar">
                                <router-link to="/withdraw/userWithdrawConfig">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    玩家提现配置
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/topUpConfig/list')" class="" id="withdraw-topUpConfig-sidebar">
                                <router-link to="/withdraw/topUpConfig">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    充值信息
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/userBankCard/list')" class="" id="withdraw-userBankCard-sidebar">
                                <router-link to="/withdraw/userBankCard">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    玩家绑卡信息
                                </router-link>

                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/withdrawLog/list')" class="" id="withdraw-withdrawLog-sidebar">
                                <router-link to="/withdraw/withdrawLog">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    提现审核
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/withdrawLog/listLog')" class="" id="log-withdrawLogLog-sidebar">
                                <router-link to="/log/withdrawLogLog">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    玩家提现记录
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/topUpLog/list')" class="" id="withdraw-topUpLog-sidebar">
                                <router-link to="/withdraw/topUpLog">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    充值审核
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/topUpLog/listLog')" class="" id="withdraw-topUpLogLog-sidebar">
                                <router-link to="/withdraw/topUpLogLog">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    充值审核记录
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                        </ul>
                    </li>


                    <li v-show="hasResource('/welfareLog/list')
                    ||hasResource('/safeBoxLog/list')
                    ||hasResource('/transferLog/list')
                    ||hasResource('/agentRebate/list')
                    ||hasResource('/attackLog/list')
                    ||hasResource('/betLog/list')
                    ||hasResource('/gameLog/list')
                    ||hasResource('/profitLog/list')
                    ||hasResource('/profitRebateLog/list')
                    ||hasResource('/shareLog/list')
                    ||hasResource('/extracLog/list')" class="">
                        <a href="#" class="dropdown-toggle">
                            <i class="menu-icon fa fa-list"></i>
                            <span class="menu-text"> 记录管理 </span>

                            <b class="arrow fa fa-angle-down"></b>
                        </a>

                        <ul class="submenu">
                            <li v-show="hasResource('/welfareLog/list')" class="" id="log-welfareLog-sidebar">
                                <router-link to="/log/welfareLog">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    福利记录
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/safeBoxLog/list')" class="" id="log-safeBoxLog-sidebar">
                                <router-link to="/log/safeBoxLog">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    保险柜存取分记录
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/transferLog/list')" class="" id="log-transferLog-sidebar">
                                <router-link to="/log/transferLog">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    玩家转账记录
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/agentRebate/list')" class="" id="log-agentRebate-sidebar">
                                <router-link to="/log/agentRebate">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    下级返利记录
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/attackLog/list')" class="" id="log-attackLog-sidebar">
                                <router-link to="/log/attackLog">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    开奖记录
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/betLog/list')" class="" id="log-betLog-sidebar">
                                <router-link to="/log/betLog">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    下注记录
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/gameLog/list')" class="" id="log-gameLog-sidebar">
                                <router-link to="/log/gameLog">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    游戏记录
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/profitLog/list')" class="" id="log-profitLog-sidebar">
                                <router-link to="/log/profitLog">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    代理盈利记录
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/profitRebateLog/list')" class="" id="log-profitRebateLog-sidebar">
                                <router-link to="/log/profitRebateLog">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    十日结算记录
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/shareLog/list')" class="" id="log-shareLog-sidebar">
                                <router-link to="/log/shareLog">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    代理分享记录
                                </router-link>
                                <b class="arrow"></b>
                            </li>

                            <li v-show="hasResource('/extracLog/list')" class="" id="log-extracLog-sidebar">
                                <router-link to="/log/extracLog">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    提取记录
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                        </ul>
                    </li>
                    <li v-show="hasResource('/user/list')||hasResource('/role/list')||hasResource('/authorityPath/list')"
                        class="">
                        <a href="#" class="dropdown-toggle">
                            <i class="menu-icon fa fa-list"></i>
                            <span class="menu-text"> 后台管理 </span>
                            <b class="arrow fa fa-angle-down"></b>
                        </a>

                        <ul class="submenu">
                            <li v-show="hasResource('/user/list')" class="" id="admin-user-sidebar">
                                <router-link to="/admin/user">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    后台用户管理
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/role/list')" class="" id="admin-role-sidebar">
                                <router-link to="/admin/role">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    角色权限管理
                                </router-link>
                                <b class="arrow"></b>
                            </li>
                            <li v-show="hasResource('/authorityPath/list')" class="" id="admin-authorityPath-sidebar">
                                <router-link to="/admin/authorityPath">
                                    <i class="menu-icon fa fa-caret-right"></i>
                                    权限路径管理
                                </router-link>
                                <b class="arrow"></b>
                            </li>

                        </ul>
                    </li>


                </ul><!-- /.nav-list -->

                <div class="sidebar-toggle sidebar-collapse" id="sidebar-collapse">
                    <i id="sidebar-toggle-icon" class="ace-icon fa fa-angle-double-left ace-save-state"
                       data-icon1="ace-icon fa fa-angle-double-left" data-icon2="ace-icon fa fa-angle-double-right"></i>
                </div>
            </div>

            <div class="main-content">
                <div class="main-content-inner">
                    <div class="page-content">
                        <div class="row">
                            <div class="col-xs-12">
                                <!-- PAGE CONTENT BEGINS -->
                                <router-view/>
                                <!-- PAGE CONTENT ENDS -->
                            </div><!-- /.col -->
                        </div><!-- /.row -->
                    </div><!-- /.page-content -->
                </div>
            </div><!-- /.main-content -->

            <div class="footer">
                <div class="footer-inner">
                    <div class="footer-content">
						<span class="bigger-120">
<!--							<span class="blue bolder">甲蛙</span>-->
							后台管理 &copy; 2099-2099
						</span>

                        &nbsp; &nbsp;
                        <span class="action-buttons">
							<a href="#">
								<i class="ace-icon fa fa-twitter-square light-blue bigger-150"></i>
							</a>

							<a href="#">
								<i class="ace-icon fa fa-facebook-square text-primary bigger-150"></i>
							</a>

							<a href="#">
								<i class="ace-icon fa fa-rss-square orange bigger-150"></i>
							</a>
						</span>
                    </div>
                </div>
            </div>

            <div id="update-password" class="modal fade" tabindex="-1" role="dialog">
                <div class="modal-dialog" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                    aria-hidden="true">&times;</span></button>
                            <h4 class="modal-title">修改密码</h4>
                        </div>
                        <div class="modal-body">
                            <form class="form-horizontal">
                                <div class="form-group">
                                    <label class="col-sm-2 control-label">原密码</label>
                                    <div class="col-sm-10">
                                        <input v-model="oldPassword" placeholder="请输入你的原密码" class="form-control">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="col-sm-2 control-label">新密码</label>
                                    <div class="col-sm-10">
                                        <input v-model="newPassword" placeholder="请输入你的新密码" class="form-control">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="col-sm-2 control-label">新密码</label>
                                    <div class="col-sm-10">
                                        <input v-model="newPassword2" placeholder="请再输入一次你的新密码" class="form-control">
                                    </div>
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                            <button v-on:click="updatePasswordSubmit()" type="button" class="btn btn-primary">保存
                            </button>
                        </div>
                    </div><!-- /.modal-content -->
                </div><!-- /.modal-dialog -->
            </div><!-- /.modal -->

            <a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse">
                <i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i>
            </a>
        </div><!-- /.main-container -->
    </div>
</template>

<script>
    export default {
        name: "admin",
        data: function () {
            return {
                loginUser: {},
                oldPassword: "",
                newPassword: "",
                newPassword2: ""
            }
        },
        mounted: function () {
            let _this = this;
            $("body").removeClass("login-layout light-login");
            $("body").attr("class", "no-skin");
            // console.log("admin");
            // sidebar激活样式方法二
            _this.activeSidebar(_this.$route.name.replace("/", "-") + "-sidebar");

            $.getScript('/ace/assets/js/ace.min.js');

            _this.loginUser = Tool.getLoginUser();

            if (!_this.hasResourceRouter(_this.$route.name)) {
                // _this.$router.push("/login");
            }
        },
        watch: {
            $route: {
                handler: function (val, oldVal) {
                    // sidebar激活样式方法二
                    console.log("---->页面跳转：", val, oldVal);
                    let _this = this;

                    // if (!_this.hasResourceRouter(val.name)) {
                    //     _this.$router.push("/login");
                    //     return;
                    // }

                    _this.$nextTick(function () {  //页面加载完成后执行
                        _this.activeSidebar(_this.$route.name.replace("/", "-") + "-sidebar");
                    })
                }
            }
        },
        methods: {
            /**
             * 查找是否有权限
             * @param router
             */
            hasResourceRouter(router) {
                let _this = this;
                let resources = Tool.getLoginUser().resources;
                if (Tool.isEmpty(resources)) {
                    return false;
                }
                for (let i = 0; i < resources.length; i++) {
                    if (router === resources[i].page) {
                        return true;
                    }
                }
                return false;
            },
            /**
             * 查找是否有权限
             * @param id
             */
            hasResource(id) {
                return Tool.hasResource(id);
            },

            login() {
                this.$router.push("/admin")
            },
            updatePassword() {
                $("#update-password").modal("show");
            },
            updatePasswordSubmit() {
                let _this = this;
                // 保存校验
                if (1 != 1
                    || !Validator.require(_this.oldPassword, "旧密码")
                    || !Validator.require(_this.newPassword, "新密码")
                    || !Validator.length(_this.newPassword, "新密码", 8, 20)
                    || !Validator.require(_this.newPassword2, "第二次输入的新密码")
                    || !(Validator.requireValue(_this.newPassword != _this.newPassword2, "两次密码输入不一致"))
                ) {
                    return;
                }
                Loading.show();
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/login/updatePassword/' + hex_md5(_this.oldPassword + KEY) + "/" + hex_md5(_this.newPassword + KEY)).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    if (resp.success) {
                        $("#form-modal").modal("hide");
                        Toast.success("修改成功！");
                        _this.logout();
                    } else {
                        Toast.warning(resp.message)
                    }
                })
            },
            /**
             * 菜单激活样式，id是当前点击的菜单的id
             * @param id
             */
            activeSidebar: function (id) {
                // 兄弟菜单去掉active样式，自身增加active样式
                $("#" + id).siblings().removeClass("active");
                $("#" + id).siblings().find("li").removeClass("active");
                $("#" + id).addClass("active");

                // 如果有父菜单，父菜单的兄弟菜单去掉open active，父菜单增加open active
                let parentLi = $("#" + id).parents("li");
                if (parentLi) {
                    parentLi.siblings().removeClass("open active");
                    parentLi.siblings().find("li").removeClass("active");
                    parentLi.addClass("open active");
                }
            },

            logout() {
                let _this = this;
                Loading.show();
                _this.$ajax.get(process.env.VUE_APP_SERVER + '/login/logout/' + _this.loginUser.token).then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    if (resp.success) {
                        Tool.setLoginUser(null);
                        _this.$router.push("/login")
                    } else {
                        Toast.warning(resp.message)
                    }
                });
            },
        }
    }
</script>
