import Vue from "vue";
import Router from "vue-router";
import Login from "./views/login.vue";
import Admin from "./views/admin.vue";
import Welcome from "./views/admin/welcome.vue";
import Player from "./views/admin/Player"
import Monster from "./views/admin/Monster"
import WelfareLog from "./views/admin/WelfareLog"
import SafeBoxLog from "./views/admin/SafeBoxLog"

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
        },{
            path: "log/welfareLog",
            name: "log/welfareLog",
            component: WelfareLog,
        },
            {
                path: "log/safeBoxLog",
                name: "log/safeBoxLog",
                component: SafeBoxLog,
            },
        ]
    }]
})
