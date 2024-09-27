package com.smokpromotion.SmokProm.domain.repo;

import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInterface;
import com.majorana.maj_orm.ORM_ACCESS.MultiId;
import com.smokpromotion.SmokProm.domain.entity.DE_TwitterSearch;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.exceptions.TwitterSearchNotFoundException;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class REP_TwitterSearch {

    private static MethodPrefixingLogger LOGGER = MethodPrefixingLoggerFactory.getLogger(REP_UserService.class);

    private DbBeanGenericInterface<DE_TwitterSearch> searchRepo = null;

//    private static final int changePasswordTimeOut = 10000;


    public REP_TwitterSearch(){
        DbBean dBean = new DbBean();
        try {
            dBean.connect();
            searchRepo = dBean.getTypedBean(DE_TwitterSearch.class);
        } catch (ClassNotFoundException | SQLException e){
            LOGGER.error("Class S_User not found");
        }
    }


    public List<DE_TwitterSearch> findByUserId(int userId) {
        List<DE_TwitterSearch> res = searchRepo.getBeansNP("SELECT "+ searchRepo.getFields()+" FROM "+
                DE_TwitterSearch.getTableNameStatic()
                +" WHERE user_id=:user_id", new String[]{"user_id"}, new Object[]{userId});
        return res;

    }

    public DE_TwitterSearch getById(int id, int uid) throws TwitterSearchNotFoundException {
        List<DE_TwitterSearch> res = searchRepo.getBeansNP("SELECT "+ searchRepo.getFields()+" FROM "+S_User.getTableNameStatic()
                +" WHERE id=:id AND  user_id=:usid", new String[]{"id","uid"}, new Object[]{id, uid});
        return res.stream().findFirst().orElseThrow( ()->new TwitterSearchNotFoundException("TS"+id, "User not found"));

    }

    public boolean update(DE_TwitterSearch ts){
        boolean ok = true;
        try {
            searchRepo.updateBean(new MultiId(ts.getId()), ts);
        } catch (Exception e){
            LOGGER.warn("Error updating user",e);
            return false;
        }
        return ok;
    }

    public int  create(DE_TwitterSearch ts){
        try {
            return searchRepo.storeBean( ts).getId();
        } catch (Exception e){
            LOGGER.warn("Error creating twitter search record",e);
            return 0;
        }
    }

}
