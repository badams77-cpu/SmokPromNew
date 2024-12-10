package com.smokpromotion.SmokProm.domain.repo;

import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInterface;
import com.majorana.maj_orm.ORM_ACCESS.MultiId;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.entity.UserLoginActivity;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import com.smokpromotion.SmokProm.util.PwCryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@Lazy
public class REP_UserLoginActivity {

    private static MethodPrefixingLogger LOGGER = MethodPrefixingLoggerFactory.getLogger(REP_UserLoginActivity.class);

    private DbBeanGenericInterface<UserLoginActivity> userRepo = null;

    private static final int changePasswordTimeOut = 10000;

    @Autowired
    private PwCryptUtil pwCryptUtil;

    public REP_UserLoginActivity(){
        DbBean dBean = new DbBean();
        try {
            dBean.connect();
            userRepo = dBean.getTypedBean(UserLoginActivity.class);
        } catch (ClassNotFoundException | SQLException e){
            LOGGER.error("Class S_User not found");
        }
    }

    public Optional<UserLoginActivity> findByUserIdForPasswordRecovery(int id) throws UserNotFoundException {
        List<UserLoginActivity> users = userRepo.getBeansNP("SELECT "+userRepo.getFields()+" FROM "+UserLoginActivity.getTableNameStatic()
                +" WHERE id_user=:id_user", new String[]{"id_user"}, new Object[]{id});
        return users.stream().findFirst();

    }

    public Optional<UserLoginActivity> findByToken(String token) throws UserNotFoundException {
        List<UserLoginActivity> users = userRepo.getBeansNP("SELECT "+userRepo.getFields()+" FROM "+UserLoginActivity.getTableNameStatic()
                +" WHERE token=:token", new String[]{"token"}, new Object[]{token});
        return users.stream().findFirst();

    }


    public Optional<UserLoginActivity> findByUserId(int id) throws UserNotFoundException {
        List<UserLoginActivity> users = userRepo.getBeansNP("SELECT "+userRepo.getFields()+" FROM "+UserLoginActivity.getTableNameStatic()
                +" WHERE id_user=:id_user", new String[]{"id_user"}, new Object[]{id});
        return users.stream().findFirst();

    }

    public void reset(int id) throws Exception {
         userRepo.deleteBeanById(new MultiId(id));

    }


    public boolean update(UserLoginActivity user){
        boolean ok = true;
        try {
            userRepo.updateBean(new MultiId(user.getId()), user);
        } catch (Exception e){
            LOGGER.warn("Error updating user",e);
            return false;
        }
        return ok;
    }

    public int create(UserLoginActivity user){
        boolean ok = true;
        try {
            return userRepo.storeBean( user).getId();
        } catch (Exception e){
            LOGGER.warn("Error updating user login activity",e);
            return 0;
        }
    }

}
