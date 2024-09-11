package com.smokpromotion.SmokProm.domain.repo;

import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInterface;
import com.majorana.maj_orm.ORM_ACCESS.MultiId;
import com.smokpromotion.SmokProm.domain.entity.AdminUser;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class REP_AdminUserService {

    private static MethodPrefixingLogger LOGGER = MethodPrefixingLoggerFactory.getLogger(REP_AdminUserService.class);

    private DbBeanGenericInterface<AdminUser> adminRepo = null;


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


}
