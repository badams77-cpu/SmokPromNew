package com.smokpromotion.SmokProm.scheduler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.urcompliant.config.admin.AdminSecurityPrincipleService;
import com.urcompliant.config.portal.PortalSecurityPrincipleService;
import com.urcompliant.domain.PortalEnum;
import com.urcompliant.domain.repository.MPCAppDBConnectionFactory;
import com.urcompliant.domain.service.DS_AdminUserService;
import com.urcompliant.domain.service.DS_UserService;
import com.urcompliant.scheduler.repository.*;
import com.urcompliant.scheduler.service.CryptoService;
import com.urcompliant.service.SmtpMailSender;
import org.springframework.context.ApplicationContext;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class AdminScheduleServices {

    public static final long SECONDS_IN_HALF_HOUR = 1800L;

    private final DR_AdminSchedule drSchedule;
    private final DR_AdminScheduleLogEntry drScheduleLogEntry;
    private final String timezone;
    private final MPCAppDBConnectionFactory dbFactory;

    private final DR_AdminSchedulePermissions drSchedulePermissions;
    private final ApplicationContext context;
    private final AdminSecurityPrincipleService principleService;
    private final DS_AdminUserService userService;
    private final ObjectMapper mapper;
    private final SmtpMailSender attachmentMailer;
    private final CryptoService cryptoService;
    private final ZipHelper zipHelper;

    public AdminScheduleServices(DR_AdminSchedule drSchedule,
                                 DR_AdminScheduleLogEntry drScheduleLogEntry,
                                 String timezone,
                                 MPCAppDBConnectionFactory dbFactory,
                                 DR_AdminSchedulePermissions drSchedulePermissions,
                                 ApplicationContext context,
                                 AdminSecurityPrincipleService principleService,
                                 DS_AdminUserService userService,
                                 ObjectMapper mapper,
                                 SmtpMailSender attachmentMailer,
                                 CryptoService cryptoService,
                                 ZipHelper zipHelper) {
        this.drSchedule = drSchedule;
        this.drScheduleLogEntry = drScheduleLogEntry;
        this.timezone = timezone;
        this.dbFactory = dbFactory;
        this.drSchedulePermissions = drSchedulePermissions;
        this.context = context;
        this.principleService = principleService;
        this.userService = userService;
        this.mapper = new ObjectMapper();
        this.mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.attachmentMailer = attachmentMailer;
        this.cryptoService = cryptoService;
        this.zipHelper = zipHelper;
    }

    public DR_AdminSchedule getDrSchedule() {
        return drSchedule;
    }

    public DR_AdminScheduleLogEntry getDrScheduleLogEntry() {
        return drScheduleLogEntry;
    }

    public String getTimezone() {
        return timezone;
    }

    public MPCAppDBConnectionFactory getDbFactory() {
        return dbFactory;
    }

    public DR_AdminSchedulePermissions getDrSchedulePermissions() {
        return drSchedulePermissions;
    }

    public ApplicationContext getContext() {
        return context;
    }

    public AdminSecurityPrincipleService getPrincipleService() {
        return principleService;
    }

    public DS_AdminUserService getUserService() {
        return userService;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public SmtpMailSender getAttachmentMailer() {
        return attachmentMailer;
    }

    public CryptoService getCryptoService() {
        return cryptoService;
    }

    public ZipHelper getZipHelper() {
        return zipHelper;
    }

    public LocalDateTime adjustTimeToZone(LocalDateTime time){
        ZoneOffset zoneOffset = ZoneId.of(timezone).getRules().getOffset(time);
        Calendar calendar = new GregorianCalendar();
        TimeZone systemTimeZone = calendar.getTimeZone();
        ZoneOffset zoneOffsetSystem = ZoneId.of(systemTimeZone.getID()).getRules().getOffset(time);
        return  time.plusSeconds(zoneOffset.getTotalSeconds()-zoneOffsetSystem.getTotalSeconds());
    }

    public LocalDateTime adjustDBTimeToZone(LocalDateTime time){
        ZoneOffset zoneOffset = ZoneId.of(timezone).getRules().getOffset(time);
        LocalDateTime dbTime = dbFactory.getDBTime(PortalEnum.AWS);
        LocalDateTime now = LocalDateTime.now();
        long dbOffset = Duration.between(dbTime, now).getSeconds();
        long roundOffset = SECONDS_IN_HALF_HOUR *(Math.round(dbOffset/SECONDS_IN_HALF_HOUR)); // Round to near half hour
        Calendar calendar = new GregorianCalendar();
        TimeZone systemTimeZone = calendar.getTimeZone();
        ZoneOffset zoneOffsetSystem = ZoneId.of(systemTimeZone.getID()).getRules().getOffset(time);
        return  time.plusSeconds(zoneOffset.getTotalSeconds()-zoneOffsetSystem.getTotalSeconds()+roundOffset);
    }
}
