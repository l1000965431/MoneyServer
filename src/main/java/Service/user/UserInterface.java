package Service.user;

/**
 * Created by lele on 2015/7/6.
 */
public interface UserInterface {

    //�û�ע�ᣬ�ж���֤���Ƿ���ȷ����ȷ������û�ע��
    public boolean userRegister(String username,String code,String password,String userType);

    //�û�ע��-�ύ�ֻ��ţ���֤�Ƿ���ע�ᣬ���Ͷ�����֤��
    //��ע�᷵��2,������֤��ɹ�����1,ʧ�ܷ���0,���벻�Ϸ�����3
    public int submitTeleNum(String username,String password);

    //�˳���¼
    public boolean quitLand(String username);

    //ʹ���û��������¼
    public String userLand(String username,String password);

    //�û�token��½,0��¼ʧ�ܣ�1�ѵ�¼��2��¼�ɹ�,3ʹ���û��������¼��token����ȷ
    public int tokenLand(String username,String token);

    //������Ϣ 0δ��¼��1���޸���Ϣ�ɹ���2����Ϣ���Ϸ�;3��token��һ��;4,userType������
    public int perfectInfo(String username,String token,String info);

    //�޸���Ϣ,0δ��¼��1���޸���Ϣ�ɹ���2����Ϣ���Ϸ�;3,tooken��һ��;4,userType������
    public int changeInfo(String userName,String token,String info);

    //�޸����뷢����֤�� 3,���벻��ȷ;2,�����벻�Ϸ���0����δ���ͳɹ���1�ɹ�
    public int sendPasswordCode(String userName,String password,String newPassword);

    //�ȶ���֤�룬�޸�����
    public  boolean changPassword(String userName,String code,String newPassWord);


}
