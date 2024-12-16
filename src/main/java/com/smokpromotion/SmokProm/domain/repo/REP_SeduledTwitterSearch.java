package com.smokpromotion.SmokProm.domain.repo;

import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInterface;
import com.majorana.maj_orm.ORM_ACCESS.MultiId;
import com.smokpromotion.SmokProm.domain.entity.DE_AccessCode;
import com.smokpromotion.SmokProm.domain.entity.DE_SeduledTwitterSearch;
import com.smokpromotion.SmokProm.domain.entity.DE_TwitterSearch;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.exceptions.TwitterSearchNotFoundException;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class REP_SeduledTwitterSearch {

    private static MethodPrefixingLogger LOGGER = MethodPrefixingLoggerFactory.getLogger(REP_UserService.class);

    private DbBeanGenericInterface<DE_SeduledTwitterSearch> searchRepo = null;

//    private static final int changePasswordTimeOut = 10000;


    public REP_SeduledTwitterSearch(){
        DbBean dBean = new DbBean();
        try {
            dBean.connect();
            searchRepo = dBean.getTypedBean(DE_SeduledTwitterSearch.class);
        } catch (ClassNotFoundException | SQLException e){
            LOGGER.error("Class S_User not found");
        }
    }

    public List<DE_SeduledTwitterSearch>  getUserIdsLast7DaysUnsentWithCodes(int userId){
        List<DE_SeduledTwitterSearch> res = searchRepo.getBeansNP("SELECT "+ searchRepo.getFields("tw")+" FROM "+
                DE_SeduledTwitterSearch.getTableNameStatic()+" tw "+
                " INNER JOIN "+ DE_AccessCode.getTableNameStatic()+" ac ON tw.user_id=ac+user_id "
                +" WHERE tw.user_id=:user_id AND tw.nresults>0 AND tw.nsent=0 AND tw.results_date" +
                        " BETWEEN DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                " AND now()"+
                " AND ac.code_date "+
                        " BETWEEN DATE_SUB(NOW(), INTERVAL 7 DAY) AND ac.code_used_date IS NULL ORDER BY tw.twitter_search_id, tw.id ;"
                , new String[]{}, new Object[]{});
        return res;
    }


    public List<DE_SeduledTwitterSearch>  getUsersSearchesInLastMonth(int userId){
        List<DE_SeduledTwitterSearch> res = searchRepo.getBeansNP("SELECT "+ searchRepo.getFields("tw")+" FROM "+
                        DE_SeduledTwitterSearch.getTableNameStatic()+" tw "+
                        " WHERE tw.nsent>0 AND userId=:user_id AND tw_results_date" +
                        " BETWEEN DATE_SUB(NOW(), INTERVAL 1 MONTH) " +
                        " AND now()"
                , new String[]{"user_id"}, new Object[]{userId});
        return res;
    }

    public List<DE_SeduledTwitterSearch>  getUsersSearchesInLastMonthForSearch(int userId, int searchId){
        List<DE_SeduledTwitterSearch> res = searchRepo.getBeansNP("SELECT "+ searchRepo.getFields()+" FROM "+
                        DE_SeduledTwitterSearch.getTableNameStatic()+
                        " WHERE user_id=:user_id AND twitter_search_id=:search_id AND" +
                        " results_date" +
                        " BETWEEN DATE_SUB(NOW(), INTERVAL 1 MONTH) " +
                        " AND now()"
                , new String[]{"user_id","search_id"}, new Object[]{userId, searchId});
        return res;
    }


    public List<DE_SeduledTwitterSearch> findByUserId(int userId) {
        List<DE_SeduledTwitterSearch> res = searchRepo.getBeansNP("SELECT "+ searchRepo.getFields()+" FROM "+
                DE_SeduledTwitterSearch.getTableNameStatic()
                +" WHERE user_id=:user_id", new String[]{"user_id"}, new Object[]{userId});
        return res;

    }

    public Optional<DE_SeduledTwitterSearch> getById(int searchId) {
        List<DE_SeduledTwitterSearch> res = searchRepo.getBeansNP("SELECT "+ searchRepo.getFields()+" FROM "+
                DE_SeduledTwitterSearch.getTableNameStatic()
                +" WHERE id=:search_id", new String[]{"search_id"}, new Object[]{searchId});
        return res.stream().findFirst();

    }

    public boolean update(int id, DE_SeduledTwitterSearch ts){
        boolean ok = true;
        if (id==0){
            LOGGER.warn("Error updating scheduled twitter search id=0");
        }
        try {

            searchRepo.deleteBeanById(new MultiId(id));
            int i= searchRepo.storeBean(ts).getId();
            ts.setId(i);
        } catch (Exception e){
            LOGGER.warn("Error updating scheduled twitter search",e);
            return false;
        }
        return ok;
    }

    public boolean update(DE_SeduledTwitterSearch ts){
        boolean ok = true;
        try {
            //searchRepo.updateBean(new MultiId(ts.getId()), ts);
            searchRepo.deleteBeanById(new MultiId(ts.getId()));
            searchRepo.storeBean(ts);
        } catch (Exception e){
            LOGGER.warn("Error updating scheduled twitter search",e);
            return false;
        }
        return ok;
    }

    public int  create(DE_SeduledTwitterSearch ts){
        try {
            return searchRepo.storeBean( ts).getId();
        } catch (Exception e){
            LOGGER.warn("Error creating twitter search record",e);
            return 0;
        }
    }

}
