package Service.user;

import java.util.Map;

/**
 * Created by fisher on 2015/7/9.
 */
public interface UserBaseInterface {

    //Ͷ�������Ƹ�����Ϣ,1���޸���Ϣ�ɹ���2����Ϣ���Ϸ�
    public int modifyInvestorInfo(String userName,String info);

    //��������Ƹ�����Ϣ
    public int  modifyBorrowerInfo(String userName,String info);

    //Ͷ�����޸ĸ�����Ϣ
    public int changeInvestorInfo(String userName,String info);

    //������޸ĸ�����Ϣ
    public int changeBorrowerInfo(String userName,String info);

    //�޸�����
    public boolean changePassword(String userName,String newPassWord);

    //ע��
    public void registered(String userName,String passWord,String userType);

    //��֤�û����Ƿ���ע��
    public boolean checkUserName(String userName);

    //��֤������֤���Ƿ���ȷ
    public boolean checkTeleCode(String userName,String code);

    //�����ֻ���֤�룬����֤�ֻ������Ƿ��ͳɹ� 1Ϊ�ɹ���0Ϊʧ��............��֤�����ݴ��ģ��������
    public int teleCodeIsSend(String userName);

    //��¼����ѯDB
    public String landing(String userName, String passWord);

    //��ѯ�û����Ƿ����
    public boolean userIsExist(String userName);

    //��ѯ���ݿ⣬�ȶ��û������Ƿ���ȷ
    public boolean checkPassWord(String userName,String passWord);

    //��¼��2�ɹ���0ʧ��
    public int tokenLand(String userName,String time);

    //����userName���һ������ϴ�token����ʱ��,�ж��Ƿ�Ϊ��¼״̬
    public boolean tokenTime(String userName,Long time);

    //��ѯ�������Ƿ���token�ַ���,����֤token�ַ����Ƿ���ͻ��˴��������
    public boolean isTokenExist(String userName,String token);

    //�˳���¼
    public boolean quitTokenLand(String userName);

    //��ȡ�û�����
    public String getUserType(String userName);

    //����¼�����Ƿ�Ϸ�
    public boolean passwordIsRight(String password);
}
