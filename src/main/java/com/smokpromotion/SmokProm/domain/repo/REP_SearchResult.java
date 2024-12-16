package com.smokpromotion.SmokProm.domain.repo;

import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInterface;
import com.majorana.maj_orm.ORM_ACCESS.MultiId;
import com.smokpromotion.SmokProm.domain.entity.DE_SearchResult;
import com.smokpromotion.SmokProm.domain.entity.DE_SeduledTwitterSearch;
import com.smokpromotion.SmokProm.domain.entity.DE_TwitterSearch;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.exceptions.TwitterSearchNotFoundException;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class REP_SearchResult {

    private static MethodPrefixingLogger LOGGER = MethodPrefixingLoggerFactory.getLogger(REP_SearchResult.class);

    private DbBeanGenericInterface<DE_SearchResult> searchRepo = null;

//    private static final int changePasswordTimeOut = 10000;


    public REP_SearchResult(){
        DbBean dBean = new DbBean();
        try {
            dBean.connect();
            searchRepo = dBean.getTypedBean(DE_SearchResult.class);
        } catch (ClassNotFoundException | SQLException e){
            LOGGER.error("Class DE_SearchResult not found");
        }
    }


    public List<DE_SearchResult>  findByUserUnsent(int userId, int seduledSearchId){
        List<DE_SearchResult> res = searchRepo.getBeansNP("SELECT "+ searchRepo.getFields()+" FROM "+
                DE_SearchResult.getTableNameStatic()
                +" WHERE user_id=:user_id  AND sent=0 AND seduled_search_id = :seduled_search_id"
                , new String[]{"user_id", "seduled_search_id"}, new Object[]{userId, seduledSearchId});
        return res;
    }

    public List<DE_SearchResult>  findByUserAndSearchId(int userId, int seduledSearchId){
        List<DE_SearchResult> res = searchRepo.getBeansNP("SELECT "+ searchRepo.getFields()+" FROM "+
                        DE_SearchResult.getTableNameStatic()
                        +" WHERE user_id=:user_id AND seduled_search_id = :seduled_search_id"
                , new String[]{"user_id", "seduled_search_id"}, new Object[]{userId, seduledSearchId});
        return res;
    }
/*
    public DE_TwitterSearch getById(int id, int uid) throws TwitterSearchNotFoundException {
        List<DE_TwitterSearch> res = searchRepo.getBeansNP("SELECT "+ searchRepo.getFields()+" FROM "+S_User.getTableNameStatic()
                +" WHERE id=:id AND  user_id=:usid", new String[]{"id","uid"}, new Object[]{id, uid});
        return res.stream().findFirst().orElseThrow( ()->new TwitterSearchNotFoundException("TS"+id, "User not found"));

    }
*/
    public boolean update(DE_SearchResult ts){
        boolean ok = true;
        try {
//            searchRepo.updateBean(new MultiId(ts.getId()), ts);
            searchRepo.deleteBeanById(new MultiId(ts.getId()));
            searchRepo.storeBean(ts);
        } catch (Exception e){
            LOGGER.warn("Error updating user",e);
            return false;
        }
        return ok;
    }

    public int  create(DE_SearchResult ts){
        try {
            return searchRepo.storeBean( ts).getId();
        } catch (Exception e){
            LOGGER.warn("Error creating twitter search record",e);
            return 0;
        }
    }

}
