package Service.user;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fisher on 2015/7/9.
 */
public class TableConstant {
    /**
     * tableBean ʵ����������ƵĶ�Ӧ��ϵmap
     * key��ʵ����ȫ��
     * value��������
     */
    public static final Map<String, String> TABLE_BEAN = new HashMap<String, String>();
    static{
        TABLE_BEAN.put("com.codingyun.core.entity.bo.SysUserBo", "sys_user");
    }

    /**
     * TABLE_PRIMARY_KEY ��������ñ������ֶεĶ�Ӧ��ϵmap
     * key��������
     * value���������ֶ�����
     */
    public static final Map<String, String> TABLE_PRIMARY_KEY = new HashMap<String, String>();
    static{
        TABLE_PRIMARY_KEY.put("sys_user", "id");
    }
}
