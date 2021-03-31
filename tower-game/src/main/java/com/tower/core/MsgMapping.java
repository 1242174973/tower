package com.tower.core;

import com.tower.handler.base.LoginHandler;
import com.tower.handler.base.RecordHandler;
import com.tower.handler.base.RoomHandler;
import com.tower.utils.MyApplicationContextUti;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/17 10:18
 */
@Slf4j
@Component
public class MsgMapping {
    private Map<Integer, ILogicHandler<?>> handleMap;

    public void init() {
        log.info("Init Msg Mapping... Start");
        List<Class<?>> list = new ArrayList<>();
        list.add(LoginHandler.class);
        list.add(RoomHandler.class);
        list.add(RecordHandler.class);
        // 赋值
        handleMap = genMapBySpring(list);
        log.info("Init Msg Mapping... End");
    }

    /**
     * 将处理类的列表转换成id->instance的map
     *
     * @param list
     * @return
     */
    public Map<Integer, ILogicHandler<?>> genMapBySpring(List<Class<?>> list) {
        Map<Integer, ILogicHandler<?>> handleMapTemp = new HashMap<>();
        for (Class<?> cla : list) {
            String name = cla.getSimpleName().substring(0, 1).toLowerCase() + cla.getSimpleName().substring(1);
            ILogicHandler<?> obj = MyApplicationContextUti.getBean(name, ILogicHandler.class);
            obj.initPrototype();
            handleMapTemp.put(obj.getMid(), obj);
            log.info("Add Msg Id[{}] Handler[{}].", obj.getMid(), obj);
        }
        return handleMapTemp;
    }

    /**
     * 通过mid找到处理类
     *
     * @param mid
     * @return
     */
    public ILogicHandler<?> findHandle(int mid) {
        return handleMap.get(mid);
    }
}
