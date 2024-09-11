package com.smokpromotion.SmokProm.domain.repo;

import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInstance;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInterface;
import com.majorana.maj_orm.ORM_ACCESS.MultiId;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.smokpromotion.SmokProm.domain.entity.S_User;

import java.sql.SQLException;
import java.util.List;

@Service
public class REP_UserService {

    private static MethodPrefixingLogger LOGGER = MethodPrefixingLoggerFactory.getLogger(REP_UserService.class);

    private DbBeanGenericInterface<S_User> userRepo = null;

    private static final int changePasswordTimeOut = 10000;


    public REP_UserService(){
        DbBean dBean = new DbBean();
        try {
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

    public static int getChangePasswordTimeOut(){
        return changePasswordTimeOut;
    }

}
