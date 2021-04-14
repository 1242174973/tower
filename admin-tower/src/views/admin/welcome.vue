<template>
    <div>
        <!--        <h1>您好，欢迎进入后台管理</h1>-->
        <div>
            <h3>
                当前游戏状态：
                <span v-show="status===0" style="color: green">运行中</span>
                <span v-show="status===1" style="color: red">维护中</span>
                &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
                <button v-show="status===0&&Tool.hasResource('/index/stop')" v-on:click="stop()" class="btn btn-white btn-default btn-danger bigger ">
                    一键维护
                </button>
                <button v-show="status===1&&Tool.hasResource('/index/start')" v-on:click="start()" class="btn btn-white btn-default btn-info bigger ">
                    一键开启
                </button>
            </h3>
        </div>


        <div v-show="Tool.hasResource('/index/index')" class="col-sm-5">
            <div class="widget-box">
                <div class="widget-header widget-header-flat widget-header-small">
                    <h5 class="widget-title">
                        <i class="ace-icon fa fa-signal"></i>
                        玩家数据
                    </h5>

                    <div class="widget-toolbar no-border">
                        <div class="inline dropdown-hover">
                            <button class="btn btn-minier btn-primary">
                                今日数据
                                <i class="ace-icon fa fa-angle-down icon-on-right bigger-110"></i>
                            </button>

                            <ul class="dropdown-menu dropdown-menu-right dropdown-125 dropdown-lighter dropdown-close dropdown-caret">
                                <li class="active">
                                    <a href="#" class="blue">
                                        <i class="ace-icon fa fa-caret-right bigger-110">&nbsp;</i>
                                        今日数据
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>

                <div class="widget-body">
                    <div class="widget-main">
                        <div id="piechart-placeholder"></div>

                        <div class="hr hr8 hr-double"></div>

                        <div class="clearfix">
                            <div class="grid4">
                                    <span class="grey">
                                       新增玩家
                                    </span>
                                <h4>{{newNum}}</h4>
                                <!--                                <h4 class="bigger pull-right">1,255</h4>-->
                            </div>

                            <div class="grid4">
                                <span class="grey">
                                   活跃玩家
                                </span>
                                <h4>{{active}}</h4>
                            </div>
                            <div class="grid4">
                                    <span class="grey">
                                        不活跃玩家
                                    </span>
                                <h4>{{notActive}}</h4>
                            </div>
                            <div class="grid4">
                                    <span class="grey">
                                        总玩家
                                    </span>
                                <h4>{{newNum+active+notActive}}</h4>
                            </div>

                        </div>
                    </div><!-- /.widget-main -->
                </div><!-- /.widget-body -->
            </div><!-- /.widget-box -->
        </div><!-- /.col -->
    </div>
</template>

<script>
    export default {
        name: "welcome",
        data: function () {
            return {
                newNum: 50,
                active: 20,
                notActive: 30,
                status: 0,
                Tool:Tool,
            }
        },
        mounted: function () {
            // sidebar激活样式方法一
            // this.$parent.activeSidebar("welcome-sidebar");
            let _this = this;
            _this.init();
            let placeholder = $('#piechart-placeholder').css({'width': '90%', 'min-height': '150px'});
            let data = [
                {label: "新增玩家", data: this.newNum, color: "#68BC31"},
                {label: "活跃玩家", data: this.active, color: "#2091CF"},
                {label: "沉默玩家", data: this.notActive, color: "#DA5430"},
            ];

            function drawPieChart(placeholder, data, position) {
                $.plot(placeholder, data, {
                    series: {
                        pie: {
                            show: true,
                            tilt: 0.8,
                            highlight: {
                                opacity: 0.25
                            },
                            stroke: {
                                color: '#fff',
                                width: 2
                            },
                            startAngle: 2
                        }
                    },
                    legend: {
                        show: true,
                        position: position || "ne",
                        labelBoxBorderColor: null,
                        margin: [-30, 15]
                    }
                    ,
                    grid: {
                        hoverable: true,
                        clickable: true
                    }
                })
            }

            drawPieChart(placeholder, data);
            placeholder.data('chart', data);
            placeholder.data('draw', drawPieChart);

            var $tooltip = $("<div class='tooltip top in'><div class='tooltip-inner'></div></div>").hide().appendTo('body');
            var previousPoint = null;


            placeholder.on('plothover', function (event, pos, item) {
                if (item) {
                    if (previousPoint != item.seriesIndex) {
                        previousPoint = item.seriesIndex;
                        var tip = item.series['label'] + " : " + item.series['percent'] + '%';
                        $tooltip.show().children(0).text(tip);
                    }
                    $tooltip.css({top: pos.pageY + 10, left: pos.pageX + 10});
                } else {
                    $tooltip.hide();
                    previousPoint = null;
                }

            });

        },
        methods: {
            init() {
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/index/index').then((response) => {
                    Loading.hide();
                    let resp = response.data;
                    _this.newNum = resp.content.newNum;
                    _this.active = resp.content.active;
                    _this.notActive = resp.content.notActive;
                    _this.status = resp.content.status;
                })
            },
            stop() {
                let  _this=this;
                Confirm.show("维护后游戏将不能提供服务，确认维护？", function () {
                    Loading.show();
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/index/stop').then((response) => {
                        Loading.hide();
                        let resp = response.data;
                        if (resp.success) {
                            _this.init();
                            Toast.success("维护成功！");
                        }
                    })
                });
            },
            start() {
                let  _this=this;
                Confirm.show("开启游戏提供服务，确认开启？", function () {
                    Loading.show();
                    _this.$ajax.post(process.env.VUE_APP_SERVER + '/index/start').then((response) => {
                        Loading.hide();
                        let resp = response.data;
                        if (resp.success) {
                            _this.init();
                            Toast.success("开启成功！");
                        }
                    })
                });
            }
        }
    }
</script>
