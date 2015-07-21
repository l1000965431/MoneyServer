package Service.user;

import com.money.Service.ServiceBase;
import com.money.config.Config;
import com.money.memcach.MemCachService;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fisher on 2015/7/6.
 */
@Service("User")
public class User extends ServiceBase implements UserInterface
{

    UserBase userbase=new UserBase();

    //�û�ע�ᣬ�ж���֤���Ƿ���ȷ����ȷ������û�ע��
    public boolean userRegister(String username,String code,String password,String userType)
    {
        //�ж��ֻ���֤���Ƿ�������ȷ
        if(userbase.checkTeleCode(username, code)==true)
        {
            userbase.registered(username,password,userType);
            return true;
        }

        else
            return false;


    }
    //�û�ע��-�ύ�ֻ��ţ���֤�Ƿ���ע�ᣬ���Ͷ�����֤��
    //��ע�᷵��2,������֤��ɹ�����1,ʧ�ܷ���0,���벻�Ϸ�����3
    public int submitTeleNum(String username,String password)
    {
        //��֤�û����Ƿ���ע��
        if(userbase.checkUserName(username)==true)
            return Config.USER_IS_REGISTER;
        else
        {
            //��֤�����Ƿ�Ϸ�
            boolean passwordIsRight=userbase.passwordIsRight(password);
            if(passwordIsRight==true)
            {
                //�����ֻ���֤�룬����֤�Ƿ��ͳɹ�
                return userbase.teleCodeIsSend(username);
            }
            else
                return Config.PASSWORD_ILLEGAL;

        }
    }

    //�˳���¼
    public boolean quitLand(String username)
    {
        return userbase.quitTokenLand(username);
    }
    //ʹ���û��������¼
    public String userLand(String username,String password)
    {
        boolean userIsExist=userbase.userIsExist(username);
        if(userIsExist==true)
        {
            String tokenData= userbase.landing(username, password);
            return tokenData;
        }
        else
            return "userName is not exist";
    }

    //�û�token��½,0��¼ʧ�ܣ�1�ѵ�¼��2��¼�ɹ�,3ʹ���û��������¼��token����ȷ
    public int tokenLand(String username,String token)
    {

        //�鿴�������Ƿ���token,�ҿͻ��˲����Ƿ���tokenһ��
        boolean tokenExist=userbase.isTokenExist(username, token);
        //�����ڣ���ѯ�û���¼״̬������,Ӧ��ʹ���û��������¼������3
        if(tokenExist==true)
        {
            //�ȶԻ���token�ϴθ���ʱ�䣬�ж��û��Ƿ��ѵ�¼
            Long orderTime = System.currentTimeMillis();
            String time = Long.toString(orderTime);
            Long timeLong=Long.parseLong(time);
            boolean landFlag=userbase.tokenTime(username,timeLong);
            if(landFlag==true)
            {
                return Config.ALREADLAND;
            }
            else
            {
                return userbase.tokenLand(username,time);
            }
        }
        else
        {
            return Config.USEPASSWORD;
        }

    }

    //������Ϣ 0δ��¼��1���޸���Ϣ�ɹ���2����Ϣ���Ϸ�;3��token��һ��;4,userType������
    public int perfectInfo(String username,String token,String info)
    {
        //�鿴�������Ƿ���token,�ҿͻ��˲����Ƿ���tokenһ��
        boolean tokenExist=userbase.isTokenExist(username, token);

        if(tokenExist==true)
        {
            //�ȶԻ���token�ϴθ���ʱ�䣬�ж��û��Ƿ��ѵ�¼
            Long orderTime = System.currentTimeMillis();
            String time = Long.toString(orderTime);
            Long timeLong=Long.parseLong(time);
            boolean landFlag=userbase.tokenTime(username,timeLong);
            if(landFlag==true) {
                //����username,�����û�����
                String userType =userbase.getUserType(username);
                if (userType == "INVESTOR")
                    return userbase.modifyInvestorInfo(username, info);
                if (userType == "BORROWER")
                    return userbase.modifyBorrowerInfo(username, info);
                else
                    return 4;
            }
            else
                return 0;
        }

        else
            return 3;
    }

    //�޸���Ϣ,0δ��¼��1���޸���Ϣ�ɹ���2����Ϣ���Ϸ�;3,tooken��һ��;4,userType������
    public int changeInfo(String userName,String token,String info)
    {
        //�鿴�������Ƿ���token,�ҿͻ��˲����Ƿ���tokenһ��
        boolean tokenExist=userbase.isTokenExist(userName, token);

        if(tokenExist==true)
        {
            //�ȶԻ���token�ϴθ���ʱ�䣬�ж��û��Ƿ��ѵ�¼
            Long orderTime = System.currentTimeMillis();
            String time = Long.toString(orderTime);
            Long timeLong=Long.parseLong(time);
            boolean landFlag=userbase.tokenTime(userName,timeLong);
            if(landFlag==true) {
                //����username,�����û�����
                String userType =userbase.getUserType(userName);
                if (userType == "INVESTOR")
                    return userbase.changeInvestorInfo(userName, info);
                if (userType == "BORROWER")
                    return userbase.changeBorrowerInfo(userName, info);
                else
                    return Config.USERTYPE_FAILED;
            }
            else
                return Config.NOT_LAND;
        }

        else
            return Config.TOKEN_FAILED;

    }

    //�޸����뷢����֤�� 3,���벻��ȷ;2,�����벻�Ϸ���0����δ���ͳɹ���1�ɹ�
    public int sendPasswordCode(String userName,String password,String newPassword)
    {
        //���������Ƿ���ȷ
        if(userbase.checkPassWord(userName,password)==true)
        {
            //����������Ƿ�Ϸ�
            boolean passwordIsRight=userbase.passwordIsRight(newPassword);
            //��������֤��ɹ�
            int sendSuccess=userbase.teleCodeIsSend(userName);
            if(passwordIsRight==true)
            {
                return sendSuccess;
            }
            else
                return Config.NEWPASSWORD_FAILED;
        }
        else
            return Config.PASSWORD_NOTRIGHT;
    }
    //�ȶ���֤�룬�޸�����
    public  boolean changPassword(String userName,String code,String newPassWord)
    {
       if(userbase.checkTeleCode(userName,code)==true) {
           boolean changeOK=userbase.changePassword(userName, newPassWord);
           return changeOK;
       }
        else
           return false;

    }
}
