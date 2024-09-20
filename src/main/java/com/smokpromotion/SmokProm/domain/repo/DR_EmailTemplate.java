package com.smokpromotion.SmokProm.domain.repo;

import com.majorana.maj_orm.ORM.MajoranaDBConnectionFactory;
import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInterface;
import com.majorana.maj_orm.ORM_ACCESS.MultiId;
import com.smokpromotion.SmokProm.domain.entity.DE_EmailTemplate;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Repository for Email Templates
 */
@Lazy
@Repository
public class DR_EmailTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(DR_EmailTemplate.class);

    private static final String TABLE_NAME = "email_templates";

    private DbBeanGenericInterface<DE_EmailTemplate> emailRepo = null;
    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    public DR_EmailTemplate(){
        DbBean dBean = new DbBean();
        try {
            dBean.connect();
            emailRepo = dBean.getTypedBean(DE_EmailTemplate.class);
        } catch (ClassNotFoundException | SQLException e){
            LOGGER.error("Class S_User not found");
        }
    }


    // -----------------------------------------------------------------------------------------------------------------
    // Public Methods
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * List all email templates in the portal
     * @param legacyPortal The portal db on which the practice group exists.
     * @return Optional DE_EmailTemplate the empty template
     */
    public List<DE_EmailTemplate> getAll() throws SQLException {

        Optional<DE_EmailTemplate> opt = Optional.empty();

        List<DE_EmailTemplate> matches =
                emailRepo.getBeans("SELECT * FROM " +DE_EmailTemplate.getTableNameStatic(), new Object[]{});

        return matches;
    }

    /**
     * Retrieve the Email Template with given id
     * @param legacyPortal The portal db on which the practice group exists.
     * @param id The template id.
     * @return Optional DE_EmailTemplate the empty template
     */
    public Optional<DE_EmailTemplate> getById( int id) {

        Optional<DE_EmailTemplate> opt = Optional.empty();

        List<DE_EmailTemplate> matches = emailRepo.getBeansNP(
                "SELECT id,name,template,subject,language FROM "+TABLE_NAME+" WHERE id = :id",
                new String[]{"id"},
                new Object[]{id}
        );

        if (!matches.isEmpty()) {
            opt = Optional.of(matches.get(0));
        }

        return opt;

    }

    /**
     * Retrieve the email template for given name and language
     * @param legacyPortal The portal db on which the practice group exists.
     * @param name The template group name.
     * @return Optional DE_EmailTemplate the empty template
     */
    public Optional<DE_EmailTemplate> getByNameAndLanguage( String name, String language) {

        Optional<DE_EmailTemplate> opt = Optional.empty();

     /*   List<DE_EmailTemplate> matches = dbFactory.getJdbcTemplate(legacyPortal).map(template->template.query(
                "SELECT id,name,template,subject,language FROM "+TABLE_NAME+" WHERE name = ? and language=?",

                new Object[]{name, language},
                new EmailTemplateMapper())).orElse(new LinkedList<>());
*/
        List<DE_EmailTemplate> matches = emailRepo.getBeansNP(
                "SELECT id,name,template,subject,language FROM "+TABLE_NAME+" WHERE name = :name and language=:language",
                new String[]{ "name", "language"},
                new Object[]{name, language}
        );

        if (!matches.isEmpty()) {
            opt = Optional.of(matches.get(0));
        }

        return opt;

    }

    /**
     * Create the email template
     * @param legacyPortal The portal db on which the practice group exists.
     * @param template The template object to insert
     * @return boolean Indication of success or failure.
     */
    public boolean create( DE_EmailTemplate template) throws Exception {

        MultiId id = emailRepo.storeBean(template);

        return id.hasAnyId();
    }
    /**
     * Update the email template
     * @param legacyPortal The portal db on which the practice group exists.
     * @param template The template object to insert
     * @return boolean Indication of success or failure.
     */
    public boolean update(DE_EmailTemplate template) throws Exception {

        return emailRepo.updateBean( new MultiId(template.getId()), template ).hasAnyId();

    }




    // -----------------------------------------------------------------------------------------------------------------
    // Protected Methods
    // -----------------------------------------------------------------------------------------------------------------



    // -----------------------------------------------------------------------------------------------------------------
    // Class: EmailTemplateMapper
    // -----------------------------------------------------------------------------------------------------------------

}
