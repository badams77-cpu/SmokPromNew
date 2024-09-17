package com.smokpromotion.SmokProm.domain.repo;

import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInstance;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInterface;
import com.majorana.maj_orm.ORM_ACCESS.MultiId;
import com.smokpromotion.SmokProm.domain.entity.AdminUser;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import com.smokpromotion.SmokProm.util.PwCryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.smokpromotion.SmokProm.domain.entity.S_User;

import java.sql.SQLException;
import java.util.List;

@Service
@Lazy
public class REP_UserService {

    private static MethodPrefixingLogger LOGGER = MethodPrefixingLoggerFactory.getLogger(REP_UserService.class);

    private DbBeanGenericInterface<S_User> userRepo = null;

    private static final int changePasswordTimeOut = 10000;

    @Autowired
    private PwCryptUtil pwCryptUtil;

    public REP_UserService(){
        DbBean dBean = new DbBean();
        try {
            dBean.connect();
            userRepo = dBean.getTypedBean(S_User.class);
        } catch (ClassNotFoundException | SQLException e){
            LOGGER.error("Class S_User not found");
        }
    }

    public S_User findByName(String name) throws UserNotFoundException {
        List<S_User> users = userRepo.getBeansNP("SELECT "+userRepo.getFields()+" FROM "+S_User.getTableNameStatic()
                +" WHERE useremail=:username", new String[]{"username"}, new Object[]{name});
        return users.stream().findFirst().orElseThrow( ()->new UserNotFoundException(name, "User not found"));

    }

    public S_User getById(int id) throws UserNotFoundException {
        List<S_User> users = userRepo.getBeansNP("SELECT "+userRepo.getFields()+" FROM "+S_User.getTableNameStatic()
                +" WHERE uid=:uid", new String[]{"uid"}, new Object[]{id});
        return users.stream().findFirst().orElseThrow( ()->new UserNotFoundException("U"+id, "User not found"));

    }

    public boolean update(S_User user){
        boolean ok = true;
        try {
            userRepo.updateBean(new MultiId(user.getId()), user);
        } catch (Exception e){
            LOGGER.warn("Error updating user",e);
            return false;
        }
        return ok;
    }

    public boolean create(S_User user, String pass){
        boolean ok = true;
        user.setUserpw(pwCryptUtil.getPasswd(pass, 0));
        try {
            userRepo.storeBean( user);
        } catch (Exception e){
            LOGGER.warn("Error updating user",e);
            return false;
        }
        return ok;
    }

    public boolean isPasswordGood(S_User u, String pw){
        return pwCryptUtil.isPasswordGood(u.getSecVNEnum().getCode(), u.getUsername(), u.getUserpw(), pw);
    }

    public static int getChangePasswordTimeOut(){
        return changePasswordTimeOut;
    }

}
