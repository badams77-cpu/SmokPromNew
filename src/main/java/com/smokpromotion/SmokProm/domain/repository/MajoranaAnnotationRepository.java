package com.smokpromotion.SmokProm.domain.repository;

import com.smokpromotion.SmokProm.config.DBs.SmokDatasourceName;
import com.smokpromotion.SmokProm.config.common.PrimaryGlobalDBName;
import com.smokpromotion.SmokProm.domain.entity.BaseSmokEntity;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.util.SQLHelper;

import jakarta.persistence.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import jakarta.persistence.Column;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class MajoranaAnnotationRepository<T extends BaseSmokEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MajoranaAnnotationRepository.class);

    private static final String PACKAGE_BASE = "com.smokpromotion.SmokProm";

    private static final LocalDate defDate = LocalDate.of(1970,1,1);

    private static final LocalTime defTime = LocalTime.of(0,0,0);

    private static final LocalTime oneSecPast = LocalTime.of(0,0,1);

    private static final LocalDateTime defDateTime = defDate.atTime(oneSecPast);

    protected Class<T> clazz;

    protected MajoranaDBConnectionFactory dbFactory;

    protected SmokDatasourceName dbName;

    protected List<MajoranaRepositoryField> repoFields = new LinkedList<>();

    protected MajoranaAnnotationRepository(MajoranaDBConnectionFactory dbFactory,  SmokDatasourceName dbName ,Class<T> clazz){
        this.dbFactory = dbFactory;
        this.dbName = dbName;
        this.clazz =clazz;
        setFieldsByReflection(clazz);
    }

    protected String getCreateStringNP(T sUser){
        StringBuffer buffy =  new StringBuffer();
        SqlParameterSource params = getSqlParameterSource(sUser);
        buffy.append("("+ repoFields.stream().filter(x->!x.isTransient()).map(
                x->x.getDbColumn()).collect(Collectors.joining(",") )+ ")");
        buffy.append(" VALUES ("+ repoFields.stream().filter(x->!x.isTransient())
                .map(x->x.isPopulatedCreated() || x.isPopulatedUpdated()?
                        "now()": ":"+x.getDbColumn())
                .collect(Collectors.joining(",") )+ ");");
        return buffy.toString();
    }

    protected String getUpdateStringNP(T sUser){
        StringBuffer buffy =  new StringBuffer();
        buffy.append(" SET "+ repoFields.stream().filter(x->!x.isTransient()).filter(x->x.isUpdateable())
                .map(x->x.getDbColumn() + ":" + ((x.isPopulatedUpdated())?"now() " : ":"+x.getField().getName()))
                .collect(Collectors.joining(",") )+ " WHERE id=:id");
        return buffy.toString();
    }


    protected String getCreateString(T sUser){
        StringBuffer buffy =  new StringBuffer();
        SqlParameterSource params = getSqlParameterSource(sUser);
        buffy.append("("+ repoFields.stream().filter(x->!x.isTransient()).map(x->x.getDbColumn()).collect(Collectors.joining(",") )+ ")");
        buffy.append(" VALUES ("+ repoFields.stream().filter(x->!x.isTransient())
                .map(x->x.isPopulatedCreated() || x.isPopulatedUpdated()? "now()": "?")
                .collect(Collectors.joining(",") )+ ");");
        return buffy.toString();
    }

    protected String getUpdateString(T sUser){
        StringBuffer buffy =  new StringBuffer();
        buffy.append(" SET "+ repoFields.stream().filter(x->!x.isTransient()).filter(x->x.isUpdateable())
                .map(x->x.getDbColumn() + ":" + ((x.isPopulatedUpdated())?"now() " : "?"))
                .collect(Collectors.joining(",") )+ " WHERE id=:id");
        return buffy.toString();
    }


    private void setFieldsByReflection(Class clazz){
        List<Field> fields = getClassFields(clazz);
        List<Method> methods = getClassMethods(clazz);
        for(Field field : fields){
            Annotation[] annotations = field.getDeclaredAnnotations();
            boolean toAdd = false;
            MajoranaRepositoryField majoranaField = new MajoranaRepositoryField();
            majoranaField.setField(field);
            majoranaField.setValueType(field.getType());
            majoranaField.setName(field.getName());
            majoranaField.setDbColumn(field.getName());

            if (field.getName().equals("createdByUserEmail")){
                LOGGER.warn("e");
            }

            boolean isTransient = Modifier.isTransient(field.getModifiers());
            majoranaField.setTransient(isTransient);

            boolean updateable = false;
            boolean popCreated = false;
            boolean popUpdated = false;
            boolean nullable = false;
            for(Annotation ann : annotations){
                if (ann.annotationType().equals(Updateable.class)){
                    updateable = true;
                }
                if (ann.annotationType().equals(PopulatedCreated.class)){
                    popCreated = true;
                }
                if (ann.annotationType().equals(PopulatedUpdated.class)){
                    popUpdated = true;
                }
                if (ann.annotationType().equals(Nullable.class)){
                    nullable = true;
                }
                if (ann.annotationType().equals(jakarta.persistence.Column.class)){
                    toAdd=true;
                    Column column = field.getAnnotation(Column.class);
                    String dbField = column.name();
                    if (dbField!=null){
                        majoranaField.setDbColumn(dbField);
                    }
                    majoranaField.setColumnAnnotation(column);
                }
            }
            majoranaField.setUpdateable(updateable);
            majoranaField.setPopulatedCreated(popCreated);
            majoranaField.setPopulatedUpdated(popUpdated);
            majoranaField.setNullable(nullable);
            for(Method method: methods){
                if (isGetter(method) && method.getName().equalsIgnoreCase("GET"+field.getName())
                        || method.getName().equalsIgnoreCase("IS"+field.getName().toUpperCase())
                        || ( method.getName().equalsIgnoreCase(field.getName().toUpperCase()) )
                ){
                    majoranaField.setGetter(method);
                } else if (isSetter(method) && method.getName().equalsIgnoreCase("SET"+field.getName())) {
                    majoranaField.setSetter(method);
                }
            }
            boolean haveRequiredData = majoranaField.checkFields();
            if (toAdd && haveRequiredData) {
                repoFields.add(majoranaField);
            } else if (!haveRequiredData){
                LOGGER.warn("setFieldsByReflection: Class: "+clazz.getName()+" Field: "+majoranaField.getName()+" missing info "+majoranaField.getMissing());
            }
        }
    }

    protected PreparedStatementCreator getSqlPreparedStatementParameter(String sql, T entity, boolean genKey)
        throws SQLException
    {



     //   Connection conn = dbFactory.getMysqlConn(dbName)s
        PreparedStatementCreator pc = new MajorPreparedStatCreator() {


            protected boolean genKey;

            public void MajorPreparedStatCreator(boolean genKey){
                this.genKey = genKey;
            }

            @Override
            public void setGenKey(boolean bol0) {

            }

            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = genKey ? con.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS ) : con.prepareStatement(sql);
                if (genKey){

                }
                setPreparedStatementFields(ps, entity);
                return ps;
            }
        };

        return pc;
    }

    protected SqlParameterSource getSqlParameterSource(T entity){
        return new MapSqlParameterSource(getParameterMap(entity));
    }

    protected SqlParameterSource getSqlParameterSourceWithDeletedAt(SmokDatasourceName sDn,T entity){
        Map<String, Object> sourceMap =getParameterMap(entity);
        sourceMap.put("deleted_at", (Boolean) sourceMap.getOrDefault("deleted",false)?  dbFactory.getDBTime(sDn) : SQLHelper.BLANK_TIMESTAMP);
        return new MapSqlParameterSource(sourceMap);
    }

    protected java.sql.Timestamp getDeletedA(BaseSmokEntity bse){
        return java.sql.Timestamp.valueOf (bse.isDeleted() ? bse.getDeletedAt() : SQLHelper.BLANK_TIMESTAMP);
    }

    private Map<String, Object> getParameterMap(T entity){
        Map<String, Object> sourceMap = new HashMap<>();
        for(MajoranaRepositoryField field : repoFields){
            try {
                Object ob = invokeGetter(entity, field.getGetter());
                if (ob!=null) {
                    if (ob instanceof LocalDate) {
                        ob = java.sql.Date.valueOf((LocalDate) ob);
                    } else if (ob instanceof LocalTime) {
                        ob = java.sql.Time.valueOf((LocalTime) ob);
                    } else if (ob instanceof LocalDateTime) {
                        ob = java.sql.Timestamp.valueOf((LocalDateTime) ob);
                    } else if (field.getValueType().isEnum()) {
                        ob = ((Enum) ob).name();
                    } else {
                        // Default
                    }
                }
                sourceMap.put(field.getDbColumn(), ob);
            } catch (Exception e){
                Exception f=e;
                LOGGER.warn("getSqlParametersource: Error Serializing field "+field.getName()+" "+field.getDbColumn(),f);

            }
        }
        return sourceMap;
    }

    private void setPreparedStatementFields(PreparedStatement ps,T entity) throws SQLException {
        Map<String, Object> sourceMap = new HashMap<>();
        int i = 1;
        for(MajoranaRepositoryField field : repoFields){
            try {
                Object ob = invokeGetter(entity, field.getGetter());
                boolean isNull = ob==null;
                boolean isNullable = field.isNullable();
//                if (ob==null) {
                
//                } else {
                    if (field.getValueType()==UUID.class) {
                        String ns = isNullable ? null : UUID.randomUUID().toString();
                        ps.setString(i, isNull ? ns : ((String) ob));
                    } else if (field.getValueType()==LocalDate.class) {
                        java.sql.Date nd = isNullable? null : java.sql.Date.valueOf(defDate);
                        java.sql.Date d = isNull ? nd : java.sql.Date.valueOf((LocalDate) ob);
                        ps.setDate(i, d);
                    } else if (field.getValueType()== LocalTime.class) {
                        java.sql.Time nt = isNullable? null : java.sql.Time.valueOf(defTime);
                        java.sql.Time t = isNull ? nt : java.sql.Time.valueOf((LocalTime) ob);
                        ps.setTime(i, t);
                    } else if (field.getValueType()== LocalDateTime.class) {
                        java.sql.Timestamp nts = isNullable? null : java.sql.Timestamp.valueOf(defDateTime);
                        java.sql.Timestamp ts = isNull ? nts : java.sql.Timestamp.valueOf((LocalDateTime) ob);
                        ps.setTimestamp(i, ts);
                    } else if (field.getValueType().isEnum()) {
                        String en = isNull ? null : ((Enum) ob).name();
                        ps.setString(i, en);
                    } else if (field.getValueType().isPrimitive()){

                        switch( field.getValueType().getName()) {
                            case "int":
                                ps.setInt(i, ((Integer) ob).intValue());
                                break;
                            case "long":
                                ps.setLong(i, ((Integer) ob).longValue());
                                break;
                            case "float":
                                ps.setFloat(i, ((Float) ob).floatValue());
                                break;
                            case "double":
                                ps.setDouble(i, ((Double) ob).doubleValue());
                                break;
                            case "boolean":
                                ps.setBoolean(i, ((Boolean) ob).booleanValue());
                                break;
                         }
                    } else {
                            switch( field.getValueType().getName()) {

                                case "java.lang.Integer":
                                    ps.setInt(i, isNull ? 0 : ((Integer) ob).intValue());
                                    //  if (ob){ invokeSetter(entity, null, setter); }
                                    break;
                                case "java.lang.Long":
                                    ps.setLong(i, isNull ? 0 : ((Long) ob).longValue());
                                    //invokeSetter(entity, rs.getLong(col), setter);
                                    // if (rs.wasNull()){ invokeSetter(entity, null, setter); }
                                    break;
                                case "java.lang.Float":
                                    ps.setFloat(i, isNull ? 0 : ((Float) ob).floatValue());
                                    //        invokeSetter(entity, rs.getFloat(col), setter);
                                    //        if (rs.wasNull()){ invokeSetter(entity, null, setter); }
                                    break;
                                case "java.lang.Double":
                                    ps.setDouble(i, isNull ? 0 : ((Double) ob).doubleValue());
//                                invokeSetter(entity, rs.getDouble(col), setter);
//                                if (rs.wasNull()){ invokeSetter(entity, null, setter); }
                                    break;
                                case "java.lang.Boolean":
                                    ps.setBoolean(i, isNull ? false : ((Boolean) ob).booleanValue());
                                    //  invokeSetter(entity, rs.getBoolean(col), setter);
                                    //  if (rs.wasNull()){ invokeSetter(entity, null, setter); }
                                    break;
                                case "java.lang.String":
                                    String ns = isNullable ? null : "";
                                    ps.setString(i, isNull ? ns : ((String) ob));
                                    //invokeSetter(entity, rs.getString(col), setter);
                                    break;
                            }
                        }
                        // Default
//                    }

//                sourceMap.put(field.getDbColumn(), ob);
                i++;
            } catch (Exception e){
                Exception f=e;
                LOGGER.warn("getSqlParametersource: Error Serializing field "+field.getName()+" "+field.getDbColumn(),f);

            }
        }
        String s = ps.toString();
        LOGGER.warn("Values: "+s);
    }


    public RowMapper<T> getMapper(){
        return new RepositoryFieldMapper();
    }

    public RowMapper<Integer> getIntegerMapper(){
        return new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(1);
            }
        };
    }

    protected class RepositoryFieldMapper implements RowMapper<T> {

        @Override
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                T entity = null;
                try {
                  entity = clazz.newInstance();
                } catch (IllegalAccessException e) {
                 LOGGER.warn("New Instance "+clazz+"IllegalAccess: ",e);
                  } catch (InstantiationException e) {
                    LOGGER.warn("New Instance "+clazz+"InstantiationException ", e);
                }
                for(MajoranaRepositoryField field : repoFields){
                    Method setter = field.getSetter();
                    String col = field.getDbColumn();
                    try {
                        if (field.getValueType().isEnum()) {
                            try {
                                Method valueOf = field.getValueType().getMethod("valueOf", String.class);
                                Object value = valueOf.invoke(null, rs.getString(col));
                                invokeSetter(entity, value, setter);
                            } catch (Exception e){
                                Method valueOf = field.getValueType().getMethod("fromString", String.class);
                                Object value = valueOf.invoke(null, rs.getString(col));
                                invokeSetter(entity, value, setter);
                            }
                        } else {
                            switch( field.getValueType().getName()) {
                                case "int":
                                    invokeSetter(entity, rs.getInt(col), setter);
                                    break;
                                case "long":
                                    invokeSetter(entity, rs.getLong(col), setter);
                                    break;
                                case "float":
                                    invokeSetter(entity, rs.getFloat(col), setter);
                                    break;
                                case "double":
                                    invokeSetter(entity, rs.getDouble(col), setter);
                                    break;
                                case "boolean":
                                    invokeSetter(entity, rs.getBoolean(col), setter);
                                    break;
                                case "java.util.UUID":
                                    invokeSetter(entity, UUID.fromString(rs.getString(col)), setter);
                                    break;
                                case "java.lang.Integer":
                                    invokeSetter(entity, rs.getInt(col), setter);
                                    if (rs.wasNull()){ invokeSetter(entity, null, setter); }
                                    break;
                                case "java.lang.Long":
                                    invokeSetter(entity, rs.getLong(col), setter);
                                    if (rs.wasNull()){ invokeSetter(entity, null, setter); }
                                    break;
                                case "java.lang.Float":
                                    invokeSetter(entity, rs.getFloat(col), setter);
                                    if (rs.wasNull()){ invokeSetter(entity, null, setter); }
                                    break;
                                case "java.lang.Double":
                                    invokeSetter(entity, rs.getDouble(col), setter);
                                    if (rs.wasNull()){ invokeSetter(entity, null, setter); }
                                    break;
                                case "java.lang.Boolean":
                                    invokeSetter(entity, rs.getBoolean(col), setter);
                                    if (rs.wasNull()){ invokeSetter(entity, null, setter); }
                                    break;
                                case "java.lang.String":
                                    invokeSetter(entity, rs.getString(col), setter);
                                    break;
                                case "java.time.LocalDate":
                                    java.sql.Date date = rs.getDate(col);
                                    LocalDate ld = date != null ? date.toLocalDate() : null;
                                    invokeSetter(entity, ld, setter);
                                    break;
                                case "java.time.LocalTime":
                                    java.sql.Time time = rs.getTime(col);
                                    LocalTime lt = time != null ? time.toLocalTime() : null;
                                    invokeSetter(entity, lt, setter);
                                    break;
                                case "java.time.LocalDateTime":
                                    java.sql.Timestamp timestamp = rs.getTimestamp(col);
                                    LocalDateTime ldt = timestamp != null ? timestamp.toLocalDateTime() : null;
                                    invokeSetter(entity, ldt, setter);
                                    break;
                                default:
                                    LOGGER.warn("mapRow: Unknown column type" + field.getValueType().getName());
                                    break;
                            }

                        }

                    } catch (Exception e){
                        LOGGER.warn("mapRow: Error deserializing field "+field.getName()+" "+field.getDbColumn(),e);
                    }
                }
                return entity;

        }
    }

    private static boolean isGetter(Method method){
        // check for getter methods
        if((method.getName().startsWith("get") || method.getName().startsWith("is"))
                && method.getParameterCount() == 0 && !method.getReturnType().equals(void.class)){
            return true;
        }
        return false;
    }

    private static boolean isSetter(Method method){
        // check for setter methods
        if(method.getName().startsWith("set") && method.getParameterCount() == 1
                && method.getReturnType().equals(void.class)){
            return true;
        }
        return false;
    }

    public void invokeSetter(Object obj,Object variableValue,Method setter) throws IllegalAccessException, IllegalArgumentException , InvocationTargetException
    {
            setter.invoke(obj,variableValue);
    }

    public Object invokeGetter(Object obj,Method getter) throws IllegalAccessException, IllegalArgumentException , InvocationTargetException
    {
            Object f = getter.invoke(obj);
            return f;
    }



    private List<Field> getClassFields(Class clazz){
        Field[] fields = clazz.getDeclaredFields();
        List<Field> out = new LinkedList<Field>();
        for(Field field: fields){
            out.add(field);
        }
        Class supClass = clazz.getSuperclass();
        while (supClass.getPackage().getName().contains(PACKAGE_BASE)){
            Field[] supFields = supClass.getDeclaredFields();
            for(Field field: supFields){
                out.add(field);
            }
            supClass = supClass.getSuperclass();
        }
        return out;
    }

    private List<Method> getClassMethods(Class clazz){
        Method[] methods = clazz.getDeclaredMethods();
        List<Method> out = new LinkedList<Method>();
        for(Method method : methods){
            out.add(method);
        }
        Class supClass = clazz.getSuperclass();
        while (supClass.getPackage().getName().contains(PACKAGE_BASE)){
            Method[] supMethods = supClass.getDeclaredMethods();
            for(Method method : supMethods){
                out.add(method);
            }
            supClass = supClass.getSuperclass();
        }
        return out;
    }

}
