package com.smokpromotion.SmokProm.domain.repo;

import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInterface;
import com.majorana.maj_orm.ORM_ACCESS.MultiId;
import com.smokpromotion.SmokProm.domain.entity.SalesLeadEntity;
import com.smokpromotion.SmokProm.exceptions.TwitterSearchNotFoundException;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class REP_SalesLeadEntity {

    private static MethodPrefixingLogger LOGGER = MethodPrefixingLoggerFactory.getLogger(REP_SalesLeadEntity.class);

    private DbBeanGenericInterface<SalesLeadEntity> salesRepo = null;

//    private static final int changePasswordTimeOut = 10000;


    public REP_SalesLeadEntity(){
        DbBean dBean = new DbBean();
        try {
            dBean.connect();
            salesRepo = dBean.getTypedBean(SalesLeadEntity.class);
        } catch (ClassNotFoundException | SQLException e){
            LOGGER.error("Class SalesLeadEntity not found");
        }
    }


    public List<SalesLeadEntity>  findByUser(int userId){
        List<SalesLeadEntity> res = salesRepo.getBeansNP("SELECT "+ salesRepo.getFields()+" FROM "+
                SalesLeadEntity.getTableNameStatic()
                +" WHERE user_id=:user_id "
                , new String[]{"user_id"}, new Object[]{userId});
        return res;
    }

    public List<SalesLeadEntity>  findByUserAfterDate(int userId, LocalDate df){
        java.sql.Timestamp date = java.sql.Timestamp.valueOf(df.atStartOfDay());
        List<SalesLeadEntity> res = salesRepo.getBeansNP("SELECT "+ salesRepo.getFields()+" FROM "+
                        SalesLeadEntity.getTableNameStatic()
                        +" WHERE user_id=:user_id AND created >=:date"
                , new String[]{"user_id", "date"}, new Object[]{userId, date});
        return res;
    }

    public Optional<SalesLeadEntity> getById(int uid, int id) {
        List<SalesLeadEntity> res = salesRepo.getBeansNP("SELECT "+ salesRepo.getFields()+" FROM "+SalesLeadEntity.getTableNameStatic()
                +" WHERE id=:id AND user_id=:uid", new String[]{"id","uid"}, new Object[]{id, uid});
        return res.stream().findFirst();
    }

    public boolean update(SalesLeadEntity ts){
        boolean ok = true;
        try {
            salesRepo.deleteBeanById(new MultiId(ts.getId()));
            salesRepo.storeBean(ts);
        } catch (Exception e){
            LOGGER.warn("Error updating sales entity",e);
            return false;
        }
        return ok;
    }

    public int  create(SalesLeadEntity ts){
        try {
            return salesRepo.storeBean( ts).getId();
        } catch (Exception e){
            LOGGER.warn("Error creating twitter search record",e);
            return 0;
        }
    }

}
