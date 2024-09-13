package com.smokpromotion.SmokProm.domain.repo;

import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInterface;
import com.majorana.maj_orm.ORM_ACCESS.MultiId;
import com.smokpromotion.SmokProm.domain.entity.AdminUser;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import com.smokpromotion.SmokProm.util.PwCryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class REP_AdminUserService {

    private static MethodPrefixingLogger LOGGER = MethodPrefixingLoggerFactory.getLogger(REP_AdminUserService.class);

    private DbBeanGenericInterface<AdminUser> adminRepo = null;

    @Autowired
    private PwCryptUtil pwCryptUtil;

    public REP_AdminUserService(){
        DbBean dBean = new DbBean();
        try {
            adminRepo = dBean.getTypedBean(AdminUser.class);
        } catch (ClassNotFoundException | SQLException e){
            LOGGER.error("Class AdminUser not found");
        }
    }

    public AdminUser findByName(String name) throws UserNotFoundException {
        List<AdminUser> users = adminRepo.getBeansNP("SELECT "+adminRepo.getFields()+" FROM "+
                AdminUser.getTableNameStatic()
                +" WHERE useremail=:username", new String[]{"username"}, new Object[]{name});
        return users.stream().findFirst().orElseThrow( ()->new UserNotFoundException(name, "User not found"));

    }

    public AdminUser getById(int uid) throws UserNotFoundException {
        List<AdminUser> users = adminRepo.getBeansNP("SELECT "+adminRepo.getFields()+" FROM "+
                AdminUser.getTableNameStatic()
                +" WHERE id=:id", new String[]{"id"}, new Object[]{uid});
        return users.stream().findFirst().orElseThrow( ()->new UserNotFoundException("uid="+uid, "User not found"));

    }

    public boolean isPasswordGood(AdminUser u, String pw){
        return pwCryptUtil.isPasswordGood(u.getSecVNEnum().getCode(), u.getUsername(), u.getUserpw(), pw);
    }

    public boolean update(AdminUser user){
        boolean ok = true;
        try {
            adminRepo.updateBean(new MultiId(user.getId()), user);
        } catch (Exception e){
            LOGGER.warn("Error updating user",e);
            return false;
        }
        return ok;
    }

    public boolean create(AdminUser user, String pass){
        boolean ok = true;
        user.setUserpw(pwCryptUtil.getPasswd(pass, 0));
        try {
            adminRepo.storeBean( user);
        } catch (Exception e){
            LOGGER.warn("Error updating user",e);
            return false;
        }
        return ok;
    }


}
