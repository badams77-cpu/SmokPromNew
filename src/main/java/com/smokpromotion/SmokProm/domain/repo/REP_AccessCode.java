package com.smokpromotion.SmokProm.domain.repo;

import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInterface;
import com.majorana.maj_orm.ORM_ACCESS.MultiId;
import com.smokpromotion.SmokProm.domain.entity.DE_AccessCode;
import com.smokpromotion.SmokProm.domain.entity.DE_TwitterSearch;
import com.smokpromotion.SmokProm.exceptions.TwitterSearchNotFoundException;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class REP_AccessCode {

    private static MethodPrefixingLogger LOGGER = MethodPrefixingLoggerFactory.getLogger(REP_AccessCode.class);

    private DbBeanGenericInterface<DE_AccessCode> searchRepo = null;

//    private static final int changePasswordTimeOut = 10000;


    public REP_AccessCode(){
        DbBean dBean = new DbBean();
        try {
            dBean.connect();
            searchRepo = dBean.getTypedBean(DE_AccessCode.class);
        } catch (ClassNotFoundException | SQLException e){
            LOGGER.error("Class S_User not found");
        }
    }


    public Optional<DE_AccessCode> findLatestByUserId(int userId) {
        List<DE_AccessCode> res = searchRepo.getBeansNP("SELECT "+ searchRepo.getFields()+" FROM "+
                DE_AccessCode.getTableNameStatic()
                +" WHERE user_id=:user_id AND code_used_date IS NULL ORDER BY code_date DESC", new String[]{"user_id"}, new Object[]{userId});
        return res.stream().findFirst();
    }

    public Optional<DE_AccessCode> getLastCodeForUser(int userId) {
        List<DE_AccessCode> res = searchRepo.getBeansNP("SELECT "+ searchRepo.getFields()+" FROM "+
                DE_AccessCode.getTableNameStatic()
                +" WHERE user_id=:user_id AND access_code IS NOT NULL  ORDER BY code_date DESC", new String[]{"user_id"}, new Object[]{userId});
        return res.stream().findFirst();
    }

    public DE_AccessCode getById(int id, int uid) throws TwitterSearchNotFoundException {
        List<DE_AccessCode> res = searchRepo.getBeansNP("SELECT "+ searchRepo.getFields()+" FROM "+DE_TwitterSearch.getTableNameStatic()
                +" WHERE id=:id AND  userid=:uid", new String[]{"id","uid"}, new Object[]{id, uid});
        return res.stream().findFirst().orElseThrow( ()->new TwitterSearchNotFoundException("TS"+id, "Search "+id+" not found"));

    }



    public boolean update(DE_AccessCode ts){
        boolean ok = true;
        try {
            searchRepo.updateBean(new MultiId(ts.getId()), ts);
        } catch (Exception e){
            LOGGER.warn("Error updating user",e);
            return false;
        }
        return ok;
    }

    public int  create(DE_AccessCode ts){
        try {
            return searchRepo.storeBean( ts).getId();
        } catch (Exception e){
            LOGGER.warn("Error creating twitter search record",e);
            return 0;
        }
    }

}
