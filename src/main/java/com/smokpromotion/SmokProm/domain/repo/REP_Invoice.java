package com.smokpromotion.SmokProm.domain.repo;

import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInterface;
import com.majorana.maj_orm.ORM_ACCESS.MultiId;
import com.smokpromotion.SmokProm.domain.entity.DE_Invoice;
import com.smokpromotion.SmokProm.domain.entity.DE_SearchResult;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class REP_Invoice {

    private static MethodPrefixingLogger LOGGER = MethodPrefixingLoggerFactory.getLogger(REP_Invoice.class);

    private DbBeanGenericInterface<DE_Invoice> searchRepo = null;

//    private static final int changePasswordTimeOut = 10000;


    public REP_Invoice(){
        DbBean dBean = new DbBean();
        try {
            dBean.connect();
            searchRepo = dBean.getTypedBean(DE_Invoice.class);
        } catch (ClassNotFoundException | SQLException e){
            LOGGER.error("Class DE_Invoice not found");
        }
    }


    public List<DE_Invoice>  findByUserUnpaid(int userId, int seduledSearchId){
        List<DE_Invoice> res = searchRepo.getBeansNP("SELECT "+ searchRepo.getFields()+" FROM "+
                DE_Invoice.getTableNameStatic()
                +" WHERE user_id=:user_id  AND date_paid IS NULL "
                , new String[]{"userid" }, new Object[]{userId});
        return res;
    }
/*
    public List<DE_SearchResult> findByUserId(int userId) {
        List<DE_TwitterSearch> res = searchRepo.getBeansNP("SELECT "+ searchRepo.getFields()+" FROM "+
                DE_SearchResult.getTableNameStatic()
                +" WHERE user_id=:user_id ", new String[]{"username"}, new Object[]{userId});
        return res;

    }

    public DE_TwitterSearch getById(int id, int uid) throws TwitterSearchNotFoundException {
        List<DE_TwitterSearch> res = searchRepo.getBeansNP("SELECT "+ searchRepo.getFields()+" FROM "+S_User.getTableNameStatic()
                +" WHERE id=:id AND  user_id=:usid", new String[]{"id","uid"}, new Object[]{id, uid});
        return res.stream().findFirst().orElseThrow( ()->new TwitterSearchNotFoundException("TS"+id, "User not found"));

    }
*/
    public boolean update(DE_Invoice ts){
        boolean ok = true;
        try {
            searchRepo.updateBean(new MultiId(ts.getId()), ts);
        } catch (Exception e){
            LOGGER.warn("Error updating user",e);
            return false;
        }
        return ok;
    }

    public int  create(DE_Invoice ts){
        try {
            return searchRepo.storeBean( ts).getId();
        } catch (Exception e){
            LOGGER.warn("Error creating twitter search record",e);
            return 0;
        }
    }

}
