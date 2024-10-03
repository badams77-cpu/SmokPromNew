package com.smokpromotion.SmokProm.domain.repo;

import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInterface;
import com.majorana.maj_orm.ORM_ACCESS.MultiId;
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

    public List<DE_SeduledTwitterSearch>  findByUserIdLast7DaysUnsnet(int userId){
        List<DE_SeduledTwitterSearch> res = searchRepo.getBeansNP("SELECT "+ searchRepo.getFields()+" FROM "+
                DE_SeduledTwitterSearch.getTableNameStatic()
                +" WHERE user_id=:user_id AND nresults>0 AND nsent=0 AND results_date BETWEEN date_add(now() INTERVAL -7 DAY)" +
                " AND now()", new String[]{"username"}, new Object[]{userId});
        return res;
    }

    public List<DE_SeduledTwitterSearch> findByUserId(int userId) {
        List<DE_SeduledTwitterSearch> res = searchRepo.getBeansNP("SELECT "+ searchRepo.getFields()+" FROM "+
                DE_SeduledTwitterSearch.getTableNameStatic()
                +" WHERE user_id=:user_id", new String[]{"username"}, new Object[]{userId});
        return res;

    }


    public boolean update(DE_SeduledTwitterSearch ts){
        boolean ok = true;
        try {
            searchRepo.updateBean(new MultiId(ts.getId()), ts);
        } catch (Exception e){
            LOGGER.warn("Error updating user",e);
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
