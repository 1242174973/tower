import Vue from "vue";
import Router from "vue-router";
import Login from "./views/login.vue";
import Admin from "./views/admin.vue";
import Welcome from "./views/admin/welcome.vue";
import Player from "./views/admin/Player"
import Monster from "./views/admin/Monster"
import WelfareLog from "./views/admin/WelfareLog"
import SafeBoxLog from "./views/admin/SafeBoxLog"
import SignIn from "./views/admin/SignIn"
import User from "./views/admin/User"
import UserWithdrawConfig from "./views/admin/UserWithdrawConfig"
import UserBankCard from "./views/admin/UserBankCard"
import WithdrawLog from "./views/admin/WithdrawLog"
import TopUpConfig from "./views/admin/TopUpConfig"
import TopUpLog from "./views/admin/TopUpLog"

Vue.use(Router);

export default new Router({
    mode: "history",
    base: process.env.BASE_URL,
    routes: [{
        path: "*",
        redirect: "/login",
    }, {
        path: "",
        redirect: "/login",
    }, {
        path: "/login",
        component: Login
    }, {
        path: "/",
        name: "admin",
        component: Admin,
        meta: {
            loginRequire: true
        },
        children: [{
            path: "welcome",
            name: "welcome",
            component: Welcome,
        }, {
            path: "game/player",
            name: "game/player",
            component: Player,
        }, {
            path: "game/monster",
            name: "game/monster",
            component: Monster,
        }, {
            path: "log/welfareLog",
            name: "log/welfareLog",
            component: WelfareLog,
        }, {
            path: "log/safeBoxLog",
            name: "log/safeBoxLog",
            component: SafeBoxLog,
        }, {
            path: "welfare/signIn",
            name: "welfare/signIn",
            component: SignIn,
        }, {
            path: "admin/user",
            name: "admin/user",
            component: User,
        }, {
            path: "withdraw/userWithdrawConfig",
            name: "withdraw/userWithdrawConfig",
            component: UserWithdrawConfig,
        },{
            path: "withdraw/userBankCard",
            name: "withdraw/userBankCard",
            component: UserBankCard,
        },{
            path: "withdraw/withdrawLog",
            name: "withdraw/withdrawLog",
            component: WithdrawLog,
        },{
            path: "withdraw/topUpConfig",
            name: "withdraw/topUpConfig",
            component: TopUpConfig,
        },{
            path: "withdraw/topUpLog",
            name: "withdraw/topUpLog",
            component: TopUpLog,
        },

        ]
    }]
})
