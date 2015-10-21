package com.money.Service.GlobalConifg;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.config.Config;
import com.money.dao.GeneraDAO;
import com.money.dao.TransactionSessionCallback;
import com.money.dao.globalConfig.GlobalConfigDAO;
import com.money.model.GlobalConfigDataStruct;
import com.money.model.GlobalConfigModel;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局设置服务
 * <p>User: Guo Hong
 * <p>Date: 15-7-8
 * <p>Version: 1.0
**/
@Service("GlobalConfigService")
public class GlobalConfigService extends ServiceBase implements ServiceInterface {
    @Autowired
    private GlobalConfigDAO globalConfigDAO;


    public void SetConfigVaule( final Map<String,String> map ){

        globalConfigDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            @Override
            public boolean callback(Session session) throws Exception {
                globalConfigDAO.SetConfigVaule(map);

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    switch ( entry.getKey() ){
                        case "AddExpInvite":
                            Config.AddExpInvite = Integer.valueOf( entry.getValue() );
                            break;
                        case "AddVirtualSecuritiesInvite":
                            Config.AddVirtualSecuritiesInvite = Integer.valueOf( entry.getValue() );
                            break;
                        case "AddExpPurchase":
                            Config.AddExpPurchase = Integer.valueOf( entry.getValue() );
                            break;
                        case "AddVirtualSecuritiesSelf":
                            Config.AddVirtualSecuritiesSelf = Integer.valueOf( entry.getValue() );
                            break;
                        case "MaxVirtualSecurities":
                            Config.MaxVirtualSecurities = Integer.valueOf( entry.getValue() );
                            break;
                        case "MinVirtualSecuritiesBuy":
                            Config.MinVirtualSecuritiesBuy = Integer.valueOf( entry.getValue() );
                            break;
                    }
                }

                return true;
            }
        });
    }


    public String GetConfigVaule(){

        List<GlobalConfigModel> list = globalConfigDAO.getAllList( GlobalConfigModel.class );

        return GsonUntil.JavaClassListToJsonList( list );

    }

}
