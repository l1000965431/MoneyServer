package com.money.Service.MissionService.MissionClass;

/**
 * Created by liumin on 16/2/17.
 *
 * 任务接口
 *
 */
public interface MissionInterface {

    /**
     * 任务逻辑
     */
    void MissionLogic( MissionParameterBase missionParameterBase );


    /**
     * 任务是否完成
     */
    Boolean MissionIsComplete();

}
