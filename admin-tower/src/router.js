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
import WithdrawLogLog from "./views/admin/WithdrawLogLog"
import TopUpConfig from "./views/admin/TopUpConfig"
import TopUpLog from "./views/admin/TopUpLog"
import TransferLog from "./views/admin/TransferLog"
import ChallengeReward from "./views/admin/ChallengeReward"
import Salvage from "./views/admin/Salvage"
import AgentRebate from "./views/admin/AgentRebate"
import AttackLog from "./views/admin/AttackLog"
import BetLog from "./views/admin/BetLog"
import GameLog from "./views/admin/GameLog"
import ProfitLog from "./views/admin/ProfitLog"


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
            path: "log/transferLog",
            name: "log/transferLog",
            component: TransferLog,
        }, {
            path: "log/withdrawLogLog",
            name: "log/withdrawLogLog",
            component: WithdrawLogLog,
        },{
            path: "log/agentRebate",
            name: "log/agentRebate",
            component: AgentRebate,
        },{
            path: "log/attackLog",
            name: "log/attackLog",
            component: AttackLog,
        },{
            path: "log/betLog",
            name: "log/betLog",
            component: BetLog,
        },{
            path: "log/gameLog",
            name: "log/gameLog",
            component: GameLog,
        },{
            path: "log/profitLog",
            name: "log/profitLog",
            component: ProfitLog,
        },

         {
            path: "welfare/signIn",
            name: "welfare/signIn",
            component: SignIn,
        }, {
            path: "welfare/challengeReward",
            name: "welfare/challengeReward",
            component: ChallengeReward,
        }, {
            path: "welfare/salvage",
            name: "welfare/salvage",
            component: Salvage,
        },

            {
                path: "admin/user",
                name: "admin/user",
                component: User,
            }, {
                path: "withdraw/userWithdrawConfig",
                name: "withdraw/userWithdrawConfig",
                component: UserWithdrawConfig,
            }, {
                path: "withdraw/userBankCard",
                name: "withdraw/userBankCard",
                component: UserBankCard,
            }, {
                path: "withdraw/withdrawLog",
                name: "withdraw/withdrawLog",
                component: WithdrawLog,
            }, {
                path: "withdraw/topUpConfig",
                name: "withdraw/topUpConfig",
                component: TopUpConfig,
            }, {
                path: "withdraw/topUpLog",
                name: "withdraw/topUpLog",
                component: TopUpLog,
            },

        ]
    }]
})
