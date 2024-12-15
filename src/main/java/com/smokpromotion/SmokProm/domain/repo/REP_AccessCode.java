package com.smokpromotion.SmokProm.domain.repo;

import com.majorana.maj_orm.ORM.MajoranaAnnotationRepository;
import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInterface;
import com.majorana.maj_orm.ORM_ACCESS.MultiId;
import com.smokpromotion.SmokProm.domain.entity.DE_AccessCode;
import com.smokpromotion.SmokProm.domain.entity.DE_SeduledTwitterSearch;
import com.smokpromotion.SmokProm.domain.entity.DE_TwitterSearch;
import com.smokpromotion.SmokProm.exceptions.TwitterSearchNotFoundException;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.springframework.jdbc.core.RowMapper;
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

    public List<Integer> getUsersWithNewCodes() {
        MajoranaAnnotationRepository<DE_AccessCode> repo = searchRepo.getRepo();
        List<Integer> res = searchRepo.getListNPUsingIntegerMapper(
                "SELECT DISTINCT user_id FROM "+
                DE_AccessCode.getTableNameStatic()
                +" WHERE code_date=now() and code_used_date IS NULL ",
                 new String[0], new Object[0]);
        return res;
    }

    public List<Integer> getFindUserAllActiveWithResultsButNoAccessCode() {

        List<Integer> res = searchRepo.getListNPUsingIntegerMapper(
                "SELECT DISTINCT user_id FROM "+DE_TwitterSearch.getTableNameStatic()+" ts "+
                        " JOIN DE_SearchResult.getTableNameStatic() sr ON sr.user_ud=ts.user_id " +
                        " WHERE ts.active=true and (sr.result_date=now()) and NOT EXISTS (" +
                        " SELECT * FROM "+DE_AccessCode.getTableNameStatic()+" WHERE ac.user_id=ts.user_id AND ac.access_code_date=now()" +
                        ")" +
                        ";", new String[0], new Object[0]);
        return res;

    }

    public List<Integer>  getUserIdsLast7DaysWithoutCodes(){
        List<Integer> res = searchRepo.getListNPUsingIntegerMapper("SELECT DISTINCT tw.user_id FROM "+
                        DE_SeduledTwitterSearch.getTableNameStatic()+" tw "

                        +" WHERE  tw.nresult>0 AND tw.nsent=0 AND " +
                        " (results_date BETWEEN DATE_SUB(now(), INTERVAL 7 DAY)" +
                        " AND now()) AND NOT EXISTS ("+
                        " SELECT * FROM "+ DE_AccessCode.getTableNameStatic()+" ac WHERE  "+
                        " ac.code_date "+
                        " BETWEEN DATE_SUB(now(), INTERVAL 7 DAY) AND now() "+
                ");"
                , new String[]{}, new Object[]{});
        return res;
    }




    public Optional<DE_AccessCode> getLastCodeForUser(int userId) {
        List<DE_AccessCode> res = searchRepo.getBeansNP(
                "SELECT "+ searchRepo.getFields()+" FROM "+
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
      //      searchRepo.updateBean(new MultiId(ts.getId()), ts);
            searchRepo.deleteBeanById(new MultiId(ts.getId()));
            searchRepo.storeBean(ts);
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
