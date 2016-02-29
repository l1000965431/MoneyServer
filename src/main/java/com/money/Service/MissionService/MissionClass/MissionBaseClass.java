package com.money.Service.MissionService.MissionClass;

/**
 * Created by liumin on 16/2/17.
 */
public class MissionBaseClass implements MissionInterface {

    /**
     * 任务名称
     */
    String MissionName;

    /**
     * 任务所属组
     */
    String MissionGroup;

    @Override
    public void MissionLogic(MissionParameterBase missionParameterBase) {

    }

    @Override
    public Boolean MissionIsComplete() {
        return null;
    }
}
