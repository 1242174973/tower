package com.tower.core.constant;

import io.netty.util.AttributeKey;

public interface GameConst {
	public final static AttributeKey<Long> ATTR_USER_ID= AttributeKey.newInstance("UserId");
	public final static AttributeKey<String> ATTR_ROOM_ID= AttributeKey.newInstance("RoomId");
	public final static AttributeKey<Long> ATTR_HEART_TIME= AttributeKey.newInstance("HeartTime");

	public static long UPDATE_INFO_TIME = 12*60*60*1000;

	//闯关游戏 玩法configId
	public static int MISSION_GAME_CONFIG_ID = 900;

    public static class AccountType{
		public static int GUEST = 0;
		public static int PHONE_NUM = 1;
		public static int WEIXIN = 2;
		public static int ACCOUNT = 3;
		public static int XIANLIAO = 4;
		public static int WEIXIN_GAME = 5;
		public static int DUOLIAO = 6;
		public static int THIRD = 10;
	}

	public static class JiBeiQiHuSubType{
		public static int NORMAL = 0;
		public static int ZIMO_NOLIMIT = 1;
	}

	public static class SpreadLevel{
		public static int LEVEL_1 = 1;
		public static int LEVEL_2 = 2;
		public static int LEVEL_3 = 3;
		public static int LEVEL_4 = 4;
		public static int LEVEL_5 = 5;
	}

	public static class SpreadRmbOrderType{
		public static int TI_RMB = 0;
		public static int TI_COIN = 1;
	}

	public static class JiePaoLimitType{
		public static int NONE = 0;
		public static int MUST_FAN = 1;
	}
	public static class HuLimitType{
		public static int NONE = 0;
		public static int MUST_FAN = 1;
	}

	public static class SpreadRmbOrderState{
		public static int NORMAL = 0;
		public static int SUC = 1;
		public static int FAILED = 2;
	}



	public static class LaiziRuleType{
		public static int NONE = 0;
		public static int NO_LAIZI_JIEPAO = 1;
	}

	public static class MailLogicType{
		public static int NORMAL = 0;
		public static int PUBLIC = 1;
		public static int ROBOT = 2;
		public static int GAME = 3;
		public static int MAIL_VALID = 4;
		public static int MANAGE = 5;
		public static int CLUB_USER_OUT = 6;
		public static int ROBOT_BY_AGENT_DIE = 7;
		public static int ENROLL = 8;
		public static int AGENT_ZM = 9;
		public static int FK_PAY_ALL = 10;
		public static int PAY_SIGN_JIE = 11;	//连续包月协议解约
		public static int HELP_PAY_SUC = 12;	//代充成功
	}

	public static class MailType{
		public static int USER = 0;
		public static int GAME = 1;
	}


	public static class MailState{
		public static int UNREAD = 0;
		public static int UNGET = 1;
		public static int READED = 2;
	}

	public static class ClubCoinType{
		public static int CLUB_COIN = 0;
		public static int USER_COIN = 1;
	}

	public static class ClubState{
		public static int NORMAL = 0;
		public static int NO_GAME = 1;
	}

	public static class SysVarType{
		public static String STOCK_GET_GOLD = "stock_get_gold_";
		public static String TURN_TABLE_POOL = "turn_table_pool";
	}

	public static class QghType{
		public static int NORMAL = 0;
		public static int NO_LAIZI = 1;
	}

	public static class ChatType{
		public static int FIX_MSG = 0;
		public static int TXT = 1;
		public static int AUDIO = 2;
		public static int GIF = 3;
		public static int GIF_2_SEAT = 4;
	}

	public static class SysErrorCode{
		public static String LOGIN_NULL = "LOGIN_NULL";
		public static String NOT_DIMEN = "NOT_DIMEN";
		public static String NOT_GOLD = "NOT_GOLD";
		public static String NOT_GOLD_JIUJI = "NOT_GOLD_JIUJI";
		public static String GOLD_TOO_MORE = "GOLD_TOO_MORE";
		public static String GOLD_TOO_MORE_JUMP = "GOLD_TOO_MORE_JUMP";
		public static String TIP_SHOW = "TIP_SHOW";
		public static String ALREADY_IN_ROOM = "ALREADY_IN_ROOM";
		public static String NOT_FKTIME = "NOT_FKTIME";

	}

	public static class NextType{
		public static int NT_LEAVE = 0;
		public static int NT_NEW = 1;
		public static int NT_CONTINUE = 2;
	}

	public static class ExchangeType{
		public static int EXCHNAGE_RMB = 1;
		public static int EXCHANGE_COIN_A =2;
	}

	public static class DeviceType{
		public static int DT_ANDROID = 1;
		public static int DT_IOS = 2;
		public static int DT_WIN = 3;
	}

	public static class TuoGuanType{
		public static int NONE = 0;
		public static int TUOGUAN_CLIENT = 1;
		public static int TUOGUAN_SERVER = 2;
	}

	public static class CoinType{
		public static int DIMEN = 0;
		public static int GOLD = 1;
		public static int FKTIME = 2;
	}

	public static class GameConfigType{
		public static int FK = 0;
		public static int GOLD = 1;
		public static int MISSION = 2;
	}

	public static class RoomActiveType{
		public static int NORMAL = 0;
		public static int FREE_DIMEN = 1;
	}

	public static class ZnType{
//		3腾讯胡牌马 4腾讯自摸马 5腾讯庄家马 6腾讯所有人马
		public static int NORMAL = 0;
		public static int GD_ALL = 1;
		public static int GD_WIN = 2;
		public static int TXHZW_HU = 3;//腾讯胡牌马
		public static int TXHZW_ZIMO = 4;//腾讯自摸马
		public static int TXHZW_BANKER = 5;//腾讯庄家马
		public static int TXHZW_ALL = 6;//腾讯所有人马
	}

	public static class ZnDblType{
		public static int NONE = 0;
		public static int BASE_DBL = 1;
		public static int ZN_DBL = 2;
		public static int NHZ_ADD2 = 3;
		public static int NHZ_ADD1 = 4;
	}

	public static class UserGameState{
		public static String UGS_FREE = "free";
		public static String UGS_READY = "ready";
		public static String UGS_GAMING = "gaming";
	}

	public static class ActiveType{
		public static String SHARE_DIMEN = "share_dimen";
		public static String GOLD_FIRST_STOCK = "gold_first_stock";
		public static String BASE_FEN_STOCK = "base_fen_stock";
		public static String GOLD_FIRST_STOCK_DDZ = "gold_first_stock_ddz";
		public static String BASE_FEN_STOCK_DDZ = "base_fen_stock_ddz";
		public static String PAY_STOCK = "pay_stock";
		public static String GAME_JIUJI = "game_jiuji";

		public static String MAIL_ROBOT_SEND = "mail_robot_send";
		public static String MAIL_ROBOT_SEND_NEW = "mail_robot_send_new";
		public static String YEAR_CARD = "year_card";
		public static String CLIENT_GIFT = "client_gift";
		public static String IOS_SHARE = "ios_share";
		public static String FIRST_WEEK = "first_week";
		public static String MOUTH_GOLD = "mouth_gold";


		public static String THEME_TASK = "theme_task"; //主题任务
		public static String FIRST_PAY_THREE_DAY = "first_pay_three_day";	//首充三日活动
		public static String LEVEL_GIFT_BAG = "level_gift_bag";	//等级返利大礼包
		public static String PLAY_GAME_FIGHT = "play_game_fight";	//对局大作战
		public static String LOGIN_GAME_TASK = "login_game_task";	//连续登陆拿装扮

		public static String WIN_LOSE_RED = "win_lose_red";	//天降红包 失败保护
		public static String GAME_WIN_GIFT = "game_win_gift";	//累胜有礼
		public static String VIDEO_GIFT = "video_gift";	//看视频免费抽奖
		public static String PLAY_GAME_TASK = "play_game_task";	//对局任务
		public static String ONE_TIME = "one_time";	//一次性活动
		public static String DIAMOND_MONTH = "diamond_month";	//钻石尊享卡


		public static String CLIENT_GIFT_WX_VIDEO = "client_gift_wxVideo"; //看视频抽奖
		public static String CLIENT_GIFT_WX_INVITE = "client_gift_wxInvite"; //看视频抽奖

		public static String RMB_GIFT_NEW = "rmb_gift_new"; //现金礼包
		public static String RMB_GIFT_BACK = "rmb_gift_back"; //回归礼包
		public static String MISSION_SIGN = "mission_sign"; //闯关签到
		public static String MISSION_YEAR = "mission_year"; //闯关新春快乐
	}

	public static class FkPayType{
		public static int AA = 1;
		public static int MASTER = 2;

	}

	public static class YearTaskId{
		/*1登陆 */
		public static int YTID_LOGIN = 1;
		/*2分享  */
		public static int YTID_SHARE = 2;
		/*3被好友领取的卡片  */
		public static int YTID_SEND_TIMES = 3;
		/*4非金币房游戏次数  */
		public static int YTID_NOGOLD_GAMES = 4;
		/*5金币房游戏次数  */
		public static int YTID_GOLD_GAMES = 5;
		/*6非金币房大赢家次数 */
		public static int YTID_NOGOLD_BIGWINS = 6;
		/*7金币充值或兑换次数   */
		public static int YTID_GOLD_PAY_CHANGE = 7;
		/*8钻石充值次数 */
		public static int YTID_DIMEN_PAY = 8;
	}

	public static class YearCardId{
//		子鼠、丑牛、寅虎、卯兔、辰龙、巳蛇、午马、未羊、申猴、酉鸡、戌狗、亥猪
		public static int CARD_ID_PIG = 1;
		public static int[] CARD_ID_ALL = 	{ 1,2,3,4,5,6,7,8,9,10,11,12 };
		public static int[] CARD_ID_OTHER = { 2,3,4,5,6,7,8,9,10,11,12};
		public static int CARD_COUNT = 12;
	}

	public static class YearCardState{
		public static int YCS_NORMAL = 0;
		public static int YCS_USED = 1;
	}

	public static class RealNameType{
		public static int GET_STATE = 0;
		public static int SEND_REAL_NAME = 1;
		public static int SEND_REAL_MOB = 2;
		public static int GET_VALID_NUM = 3;
		public static int SEND_REAL_INFO = 4;
	}



	public static class Ting{
		//0无听
		public static int NONE = 0;
		//1有听可胡
		public static int TING_ANY_HU = 1;
		//2有听必听胡
		public static int TING_HU = 2;
	}

	public static class TingValue{
		public static int AN_LOU = 0;
		public static int MING_LOU = 1;
	}

	public static class HzResType{
		public static int NONE = 0;
		public static int FIRST_M2ALL_FIX = 1;// 首亮赔所有固定分
		public static int NOT2T_M2NOM_FIX = 2;// 无听赔听明赔无明固定分
		public static int NOT2T_M2NOM_MAX = 3;// 无听赔听明赔无明最大牌型
		public static int NOT2T_FIRST_M2ALL_FIX = 4;//无听赔听首亮赔所有固定分
	}

	public static class HzChajiaoType{
		public static int NONE = 0;
		public static int NOT2T_MAX = 1;// 无听赔听最大牌型
	}

	public static class TingType{
		//0暗楼接炮
		public static int AN_JIE_PAO = 0;
		//1暗楼自摸
		public static int AN_ZIMO = 1;
		//2明楼接炮
		public static int MING_JIE_PAO = 2;
		//3明楼自摸
		public static int MING_ZIMO = 3;
		//4明自摸暗接炮
		public static int MING_ZIMO_AN_JIE_PAO = 4;
		//5暗明都自摸
		public static int MING_AN_ZIMO = 5;
		//6暗明无限制(全明)
		public static int MING_AN_NO_LIMIT_ALL_MING = 6;

		public static int[] MING_TYPES = {
				MING_JIE_PAO,
				MING_ZIMO,
				MING_ZIMO_AN_JIE_PAO,
				MING_AN_ZIMO,
				MING_AN_NO_LIMIT_ALL_MING
		};

	}


	public static class GameType{
		public static String MJ = "MJ";
		public static String DDZ = "DDZ";
	}

	public static class MjRoomStep{
		public static int WAITING = 1;
		public static int PIAO_CHOICE = 2;
		public static int DOUBLE_CHOICE = 3;
		public static int KAIBAO = 4;
		public static int BAI_DA = 5;
		public static int GAMEING = 6;
	}


	public static class UserDoubleType{
		public static int DBL_NONE = 0;
		public static int DBL_NO_DBL = 1;
		public static int DBL_DBL = 2;
	}

	public static class UserType{
		//0:普通用户  1:调试面板用户  2:开发用户  3:禁止登陆用户
		public static int NORMAL = 0;
		public static int DEBUG_MJ = 1;
		public static int DEBUG_DEV = 2;
		public static int BLACK = 3;
		public static int ROBOT = 4;
	}

	public static class UserCoinLogType{
//		0后台操作  1手机充值 2游戏使用  3活动 4合作商充值
		public static int MANAGE_OPREATE = 0;
		public static int SDK_PAY = 1;
		public static int GAME_ROOM = 2;
		public static int ACTIVE_SEND = 3;
		public static int AGENT_PAY = 4;
		public static int REAL_MOB_SEND = 5;
		public static int GAME_REG_ORG = 6;
		public static int COINA_2_COINB = 7;
		public static int GET_JIUJI_GOLD = 8;
		public static int ROBOT_AUTO_FORCE = 9;
		public static int GOLD_TABLE = 10;
		public static int SPREAD_CONFIRM = 11;
		public static int MAIL_GET = 12;
		public static int SPREAD_TI_COIN = 13;

		public static int VIP_CLUB_GAME = 14;
		public static int VIP_CLUB_GAME_BACK = 15;
		public static int VIP_CLUB_GAME_FINAL = 16;
		public static int GAME_RMB_CHANGE = 17;

		public static int COIN_SWITCH = 18;

		public static int RECORD_GAME_PAY = 19; //买单
		public static int RECORD_GAME_PAY_REFUND = 20; //买单退回
		public static int LEVEL_REWARD_RECEIVE = 21;	//领取等级游戏
		public static int GAME_MISSION = 22;	//领取闯关奖励

	}

	public static class ChengbaoType{
		public static int NONE = 0;
		public static int CB_ICON = 1;
		public static int CB_3 = 2;
		public static int CB_5 = 3;
		public static int TX_LDCB = 4;
	}

	public static class SpreadRmbLogType{
		public static int TIXIAN = 0;
		public static int USER_PAY_BACK = 1;
		public static int TI_COIN_A = 2;
	}

	public static class SpreadUserType{
		public static int SPREAD = 0;
		public static int AGENT = 1;
	}

	public static class SpreadState{
		public static int NORMAL = 0;
		public static int PASS_FIRST = 1;
		public static int PASS = 2;
	}

	public static class HandleType{
		public static int OUT_TILE = 1;
		public static int PASS = 2;
		public static int HU_TILE = 3;
		public static int GANG_TILE = 4;
		public static int PENG_TILE = 5;
		public static int CHI_TILE = 6;
		public static int TING_TILE = 7;
		public static int MINGLOU_TILE = 8;
		public static int PIAO = 9;
		public static int PIAO_CONFIRM = 10;
//		public static int PIAO_CONFIRM_RESULT = 11;
		public static int DOUBLE = 12;
		public static int ZHUA_NIAO = 13;
		public static int BU_HUA = 14;
		public static int FEICANGYING = 15;
		public static int PASS_CLIENT = 20;
//		PASS_CLIENT = 20
	}




	//0无 1选百搭,无百搭可捉铳 2选百搭,无百搭不可捉铳
	public static class BaidaType{
		public static int NONE = 0;
		public static int BD_NODB_CAN_DIANPAO = 1;
		public static int BD_NODB_CANNOT_DIANPAO = 2;
		public static int BD_NORMAL = 3;
	}

	public static class UserResultType{
		public static String HU = "hu";
		public static String HU_ZIMO = "hu_zimo";
		public static String LOSS = "loss";
		public static String LOSS_DIANPAO = "loss_dianpao";
		public static String LIUJU = "liuju";
	}

	public static class GameResultType{
		public static int HU = 1;
		public static int ZIMO = 2;
		public static int LIUJU = 3;
	}


	public static class CountType{
		public static int HU_ZIMO = 1;			// 自摸次数
		public static int HU_NORMAL = 2;		// 非赢非点炮状态次数
		public static int LOSS_DIANPAO = 3;		// 点炮次数

		public static int GANG_AN = 4;			// 暗杠次数
		public static int GANG_MING = 5;		// 明杠次数
		public static int GANG_MING_BU = 6;		// 补杠次数
		public static int GANG_MING_FANG = 7;	// 放杠次数


		public static int GANG_GANG_MING = 8;		// 杠杠-明杠次数
		public static int GANG_GANG_MING_FANG = 9;	// 杠杠-放明杠次数

		public static int GANG_GANG_BU = 10;		// 杠杠-补杠次数
		public static int GANG_GANG_BU_FANG = 11;	// 杠杠-放补杠次数

		public static int GANG_GANG_AN = 12;		// 杠杠-暗杠次数
		public static int GANG_GANG_AN_FANG = 13;	// 杠杠-放暗杠次数

	}

	public static class ApplyDismissResult{
		public static int DISMISS = 1;
		public static int NO_DISMISS = 2;
		public static int WAITING = 3;
	}

	public static class AgreeDismissState{
		public static int AGREE = 1;
		public static int NO_AGREE = 2;
	}

	public static class HandleSubType{
		public static int PASS_BU_ZHANG = 1;

		public static int DOUBLE_QZ = 2;
		public static int FEICANGYING_YMQZ = 3;
		public static int TXHZW_ZHUANIAO = 4;
	}


	public static class YmqzType{
		public static final int ONE_IS_ONE = 0;
		public static final int ONE_IS_NINE = 1;
	}

	public static class YmqzLimit{
		public static final int NONE = 0;
		public static final int NEED_ZIMO = 1;
		public static final int NEED_ZIMO_MING_TING = 2;
	}

	public static class AwardType{
		public static final int AWARD_COIN_A = 1;
		public static final int AWARD_COIN_B = 2;
		public static final int AWARD_JIFEN  = 3;
		public static final int AWARD_MATERIAL_OBJECT = 4;
		public static final int AWARD_LOSS = 5;
		public static final int AWARD_HB = 6;
		public static final int AWARD_RMB = 7;
	}



	public static class SmsType{
		public static int ADD_SUB_AGENT = 1;
		public static int SELF_REGIST_AGENT = 2;
		public static int AGENT_ENCHASH =3;
		public static int AGENT_PHONE_LOGIN =4;
		public static int TURN_TABLE_INFO =5;
		public static int REAL_NAME_VALID =6;
		public static int SPREAD_TIXIAN = 7;
		public static int ACTIVE_YEAR_CARD = 8;
		public static int GAME_RMB = 9;
	}

	public static int DISMISS_TIMEOUT_SEC = 2*60;

	public static int HEART_TIMEOUT_SEC = 20;

	public static int USER_SCORE_DEF = 0;

	public static int TURN_TABLE_RECORD_COUNT = 10;


	public static class EnrollType{
		public static int NORMAL = 0;
		public static int HHR = 1;
		public static int AGENT = 2;
		public static int SPREAD = 3;
		public static int JOIN_QUN = 4;
		public static int CREATE_VIP_FREE_CLUB = 5;
		// 用户俱乐部游戏满一定局数
		public static int SIMPLE_VIP_CLUB_GAMES = 6;

		public static int SEND_VALID = 99;
	}
	public static class UserRmbOrderInfoType{
		public static int UNKNOWN = 0;
		public static int HUAFEI = 1;
		public static int WX_HB = 2;
		public static int ALI_HB = 3;
	}
	public static class UserRmbOrderLogicType{
		public static int UNKONWN = 0;
		public static int ACTIVE_DAILY = 1;
		public static int ACTIVE_FRIEND_REG = 2;
		public static int ACTIVE_YEAR_CARD = 3;
		public static int GAME_RMB = 4;
	}

	public static class UserRmbLogType{
		public static int TI_XIAN = 1;
		public static int TI_COIN = 2;
		public static int ACTIVE_GAME_TASK = 3;
		public static int ACTIVE_TURN_TABLE = 4;
		public static int ACTIVE_DAILY_SHARE = 5;
	}

	public static class GameTaskSendType{
		public static int DIMEN = 1;
		public static int GOLD = 2;
		public static int RMB_FEN = 3;
	}




	public static class BlackListAccountType{
		public static int GAME_USER = 0;
		public static int AGENT_USER = 1;
	}

	public static class BlackListAccountState{
		public static int NORMAL = 0;
		public static int CLOSE_ONE = 1;
		public static int CLOSE_ALL = 2;
	}
	public static class BlackType{
		public static int NONE = -1;
		public static int SIMPLE = 0;
		public static int NO_LOGIN = 1;

	}

	public static class BlackListAccountLevel{
		public static int ROOT = 0;
	}

	public static class BlackListAccountInfoType{
		public static int IP = 0;
		public static int DEVICE = 1;
	}

	public static class ServerStaticConfigType{
		public static int CLIENT_TEXT = 0;
		public static int GAME_CONFIG_FK = 1;
		public static int GAME_CONFIG_GOLD = 2;
		public static int GAME_TIP = 3;
		public static int GAME_PACKET = 4;
		public static int GAME_LEVEL = 5;
		public static int GAME_QUESTION = 6;
		public static int GAME_MISSION = 7;
	}

	public static class UserCoinType{
		public static int TYPE_DIAMOND = 1;
		public static int TYPE_MOUTH = 2;
	}

	public static class UserSpreadType{
		public static int TYPE_DIAMOND = 1;
		public static int TYPE_MOUTH = 2;
	}

	public static class GameRecordPayState{
		public static int STATE_NO_CAN = 0;
		public static int STATE_CAN = 1;
		public static int STATE_FINISH = 2;
	}

	public static class UserSpreadNOSpread{
		public static long USER_ID = -1L;
	}

	public static class SpreadRmbLogState{
		public static int STATE_NO_HANDLE = 0;
		public static int STATE_SUCCESS = 1;
		public static int STATE_ERROR = 2;
	}

	public static class UserInfoTempType{
		public static int NEW = 0;
		public static int OLD = 1;
	}

	public static class WxTiXianSwitch{
		public static int CLOSE = 0;
		public static int OPEN = 1;
	}

	public static class SpreadTraceRtType{
		//咨询
		public static int CONSULTING = -1;
		//普通推广员
		public static int TEMP_ONE = 0;
		//中级推广员
		public static int TEMP = 1;
		//高级推广员
		public static int CORE = 2;
	}

	public static class SpreadAuditState{
		public static int NO_DISTRIBUTION = 0;	//未分配
		public static int NO_HANDLE = 1;	//未处理
		public static int EXIGENCE_HANDLE = 2;//紧急处理
		public static int ABANDON = 3;		//暂时放弃
		public static int SUCCESSFUL = 4;		//成功
	}


	//连续包月 签约状态
	public static class AgreementStatus {
		public static final int SIGNING = 0;//签约中
		public static final int YET_SIGN = 1;//已签约
		public static final int JIE_SIGN = 2;//已解约
	}

	//推广员类型
	public static class SpreadOldSpread {
		public static final int NEW = 0;
		public static final int OLD = 1;
	}

	//推广员咨询状态
    public static class SpreadConsultState {
		public static final int NO = 0;
		public static final int YET = 1;
    }

    //背包类型 类型 1头像框 2称号 3聊天框 4桌布
	public static class UserPacketType{
		public static final int HEAD_IMG = 1;
		public static final int TITLE = 2;
		public static final int CHAT = 3;
		public static final int DESKTOP = 4;
	}

	//背包操作类型
	public static class UserPacketHandle{
		public static final int USE = 0; //使用
		public static final int UNLOAD = 1;//卸下
	}

	//奖励类型枚举
	public static class RewardType {
		public static final String GOLD = "gold"; //钻石
		public static final String DIMEN = "dimen";//金币
		public static final String FANGKA = "fangka";//房卡
		public static final String PHYSICAL = "physical";//体力

		public static final String HEADBOX = "headbox";//头像框
		public static final String TITLE = "title";//称号
		public static final String CHATBOX = "chatbox";//聊天框
		public static final String BGIMG = "bgimg";//桌布
	}

	//等级奖励领取状态
	public static class LevelAwardState {
		public static final int NO_GET = 0; //0未领取
		public static final int GETED = 1;  //1已领取
	}

	//主题任务
	public static class ThemeTask{
		public static final int TT_FK_ROOM_GAME_COUNT = 1;		// 好友房游戏次数
		public static final int TT_FK_ROOM_BIGWIN_COUNT = 2;	// 好友房大赢家次数
		public static final int TT_THEME_SHAER_COUNT = 3;		// 主题界面分享次数
		public static final int TT_CLUB_MASTER_COUNT = 4;		// 俱乐部管理员专属
		public static final int TT_WATCH_VIDEO_COUNT = 5;		// 累计观看视频%s次
		public static final int TT_GOLD_ROOM_GAME_COUNT = 6;	// 金币房完成%s局
		public static final int TT_WIN_GOLD_COUNT = 7;			// 金币房累计赢取%s金币
	}

	//现金活动任务
	public static class RmbGiftTask{
		public static final int PLAY_GOLD_GAME = 1;	//金币房游戏5局
		public static final int REAL_NAME = 2;		//实名认证
		public static final int SHARE_GAME = 3;		//分享游戏3次
		public static final int CREATE_JOIN_CLUB = 4;		//加入/创建亲友圈
		public static final int CLUB_AND_FRIEND_PLAY_GAME = 5;	//好友房/亲友圈局数
		public static final int SPREAD_USER = 6;	//推广人数
		public static final int SPREAD_PAY = 7;	//推广充值达到
		public static final int SPREAD_TIXIAN = 8;	//推广提现
		public static final int SHARE_PYQ = 9;	//分享朋友圈/或微信
	}

	//对局任务
	public static class PlayGameTask{
		public static final int PGT_GANG_PAI  		= 1;	//杠牌%s次
		public static final int PGT_AN_GANG 		= 2;	//暗杠%s次
		public static final int PGT_BU_GANG 		= 3;	//补杠%s次
		public static final int PGT_MING_GANG  		= 4;	//明杠%s次

		public static final int PGT_OUT_TIAO_PAI 	= 5;	//打出%s个条子牌
		public static final int PGT_OUT_TONG_PAI 	= 6;	//打出%s个筒子牌
		public static final int PGT_BEI_WAN_PAI 	= 7;	//打出%s个万字牌

		public static final int PGT_HAS_TIAO_PAI 	= 8;	//牌组内有%s个条子牌
		public static final int PGT_HAS_TONG_PAI 	= 9;	//牌组内有%s个筒子牌
		public static final int PGT_HAS_WAN_PAI 	= 10;	//牌组内有%s个万子牌

		public static final int PGT_PENG_PAI 		= 11;	//碰牌%s次
		public static final int PGT_MO_NAI_ZI_PAI 	= 12;	//摸到%s张癞子
		public static final int PGT_TING_PAI 		= 13;	//听牌%s张
		public static final int PGT_TING_PAI_ANY 	= 14;	//听任意牌
		public static final int PGT_WIN 			= 15;	//对局胜利
	}

	//一次性活动
	public static class ActiveOneTime{
		public static final String BUY_FK	= "buy_fk";	//购买房卡
	}

	//玩家渠道
	public static class UserChannel{
		public static final String WX_GAME	= "wxggw";	//微信小游戏
	}

}
