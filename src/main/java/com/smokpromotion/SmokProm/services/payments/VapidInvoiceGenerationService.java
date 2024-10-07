package com.smokpromotion.SmokProm.services.payments;

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.builders.SubReportBuilder;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.*;
import ar.com.fdvs.dj.domain.entities.Subreport;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionStyleExpression;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionalStyle;
import com.smokpromotion.SmokProm.config.portal.PortalEmailConfig;
import com.smokpromotion.SmokProm.config.portal.PortalSecurityPrinciple;
import com.smokpromotion.SmokProm.config.portal.PortalSecurityPrincipleService;
import com.smokpromotion.SmokProm.domain.entity.DE_Invoice;
import com.smokpromotion.SmokProm.domain.entity.DE_SeduledTwitterSearch;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.repo.REP_SeduledTwitterSearch;
import com.smokpromotion.SmokProm.email.SmtpMailWrapper;
import com.smokpromotion.SmokProm.util.FileToZip;
import com.smokpromotion.SmokProm.util.GenericUtils;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Profile("smok_app")
@Service
public class VapidInvoiceGenerationService {

    private static final int SECONDS_IN_HALF_HOUR = 1800;

    private static final Logger LOGGER = LoggerFactory.getLogger(VapidInvoiceGenerationService.class);




    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(("EEEE dd MMMM yyyy "));
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern(("HH:mm:ss EEEE dd MMMM yyyy"));
    public static final int MINUTES_IN_HOUR = 60;


    private final REP_SeduledTwitterSearch sTSRepo;


    private final PortalSecurityPrincipleService portalSecurityPrincipleService;

    private final SmtpMailWrapper smtpMailSender;

    private final PortalEmailConfig emailConfig;



    private ZoneId zoneId;
    private ZoneId systemZoneId;


    @Autowired
    public VapidInvoiceGenerationService(

            SmtpMailWrapper smptMailSender,
            REP_SeduledTwitterSearch sTSRepo,
            @Value("${MPC_SCHEDULER_TIMEZONE:Europe/London}") String timezone,
            PortalEmailConfig emailConfig,
            PortalSecurityPrincipleService principleService
    ) {

        this.sTSRepo = sTSRepo;
        this.portalSecurityPrincipleService = principleService;
        this.emailConfig = emailConfig;
        this.smtpMailSender = smptMailSender;
        systemZoneId = ZoneId.systemDefault();
        if (timezone != null) {
            try {
                zoneId = ZoneId.of(timezone);
            } catch (Exception e) {
                LOGGER.error("MPCPayEmailGenerationService: Exception setting timezone: "+timezone, e);
                LOGGER.warn("MPCPayEmailGenerationService: Using System Default Timezone");
            }
        }
    }



    public FileToZip generateForUser(PortalSecurityPrinciple principle, S_User user, DE_Invoice deInvoice, List<DE_SeduledTwitterSearch> days) throws Exception {
        List<DE_SeduledTwitterSearch> periodProviders = days.stream().sorted(Comparator.comparing(DE_SeduledTwitterSearch::getResultsDate)).collect(Collectors.toList());

        return generateInvoice(user, deInvoice, days, principle.getCurrencySymbol());

    }

    private FileToZip generateInvoice(S_User user, DE_Invoice invoice ,  List<DE_SeduledTwitterSearch> days,
                                      String currency) throws Exception {


        return jasperExport(currency, user, invoice, days);
    }



    private FileToZip jasperExport(String currencySymbol, S_User user,
                                   DE_Invoice invoice, List<DE_SeduledTwitterSearch> days) throws Exception {

        setDefaultPdfFontEmbedded();
        FastReportBuilder drb = new FastReportBuilder();

        Style subtitleStyle = new Style();
        Font font1 = new Font(1, "DejaVu Sans",  false, false, false);
        subtitleStyle.setFont(font1);
        subtitleStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
        subtitleStyle.setVerticalAlign(VerticalAlign.TOP);
        subtitleStyle.setBorder(Border.PEN_1_POINT());
        subtitleStyle.setBackgroundColor(Color.WHITE);
        subtitleStyle.setBorderColor(Color.WHITE);
        subtitleStyle.setTextColor(Color.BLACK);
        subtitleStyle.setTransparency(Transparency.OPAQUE);

        Style mainStyle = new Style();
        Font font2 = new Font(10, "DejaVu Sans", false, false, false);
        mainStyle.setFont(font2);
        mainStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
        mainStyle.setVerticalAlign(VerticalAlign.TOP);
        mainStyle.setBorder(Border.PEN_1_POINT());
        mainStyle.setBackgroundColor(Color.WHITE);
        mainStyle.setBorderColor(Color.WHITE);
        mainStyle.setTextColor(Color.BLACK);
        mainStyle.setTransparency(Transparency.OPAQUE);
        
        drb
                .setReportName("Invoice "+user.getFirstname()+" "+user.getLastname())
 //               .setTitle("Invoice "+prov.getName()+" - "+period.getStartDate().format(DATE_FORMAT)+" to "+period.getEndDate().format(DATE_FORMAT))
 //                              .setSubtitle("This invoice was generated at " + period.getLockedDateTime().format(DATE_FORMAT))
                .setPageSizeAndOrientation(Page.Page_A4_Portrait())
                .setWhenNoData("No Data", subtitleStyle)
                .setUseFullPageWidth(true)
                .setDetailHeight(10)		//defines the height for each record of the report
                .setMargins(10, 0, 50, 50)		//define the margin space for each side (top, bottom, left and right)
                .setDefaultStyles(subtitleStyle, subtitleStyle, subtitleStyle, mainStyle);

//        drb.setTemplateFile(file.getAbsolutePath(), true, true, true, true);

        HashMap<String, Object> params = new HashMap<>();

        String[] addrCols = {""};
        for(int i=0; i<addrCols.length; i++){
            drb.addColumn(addrCols[i],"ADDR_"+i, String.class.getName(), 500,true );
        }

        List<String> addrTable = new LinkedList<>();
        addrTable.add( user.getCompanyName());
        addrTable.add( user.getAddress1());
        if (user.getAddress2()!=null && !user.getAddress2().equals("")) addrTable.add( user.getAddress2());
        addrTable.add( user.getTown());
        addrTable.add( user.getCountry());
        addrTable.add( user.getPostcode());
        addrTable.add("");
        addrTable.add("Invoice Date: "+(invoice.getInvoiceDate().format(DATE_FORMAT)));

        List<Map<String,String>> addrData = new LinkedList<>();

        for(int j=0; j<addrTable.size(); j++){
            Map<String, String> dataMap = new HashMap<>();
            for(int i=0; i<addrCols.length; i++){
                dataMap.put("ADDR_"+i, i==addrCols.length-1? addrTable.get(j): "");
            }
            addrData.add(dataMap);
        }

        params.put("address", addrData );

//        practiceAddressSubReport(params, drb,1, optPractice);

        revenueSubReport( user, currencySymbol, drb, invoice, days);

        footerSubReport(params, drb,3);

        DynamicReport dr = drb.build();

        JasperPrint jasperPrint = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(),  new JRBeanCollectionDataSource(addrData), params);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        JasperExportManager.exportReportToPdfStream(jasperPrint,out);

        byte[] binData = out.toByteArray();
        FileToZip file= new FileToZip();
        String filename = user.getCompanyName().replaceAll("/s","_");
        if (!GenericUtils.isValid(filename)){
            filename = user.getFirstname()+"_"+user.getLastname();
        }
        file.setFilename(filename+".pdf");
        file.setFile(binData);
        return file;
    }

    private void setDefaultPdfFontEmbedded() {
        JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();
        jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.font.name", "net/sf/jasperreports/fonts/dejavu/DejaVuSans.ttf");
        jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
    }



    private void footerSubReport(Map<String, Object> params, DynamicReportBuilder builder, int repNum) throws Exception {
        FastReportBuilder drb = new FastReportBuilder();
        String[] addrCols = {""};
        for(int i=0; i<addrCols.length; i++){
            drb.addColumn(addrCols[i],"FOOTER_"+i, String.class.getName(), 500,true );
        }


        List<Map<String,String>> footerData = new LinkedList<>();
        Map<String, String> footMap = new HashMap<>();
        footMap.put("FOOTER_0","Payment As Contract Agreement");
        footerData.add(footMap);

        params.put("footer", footerData );

        Style subtitleStyle = new Style();
        Font font1 = new Font(10, "DejaVu Sans", true, false, false);
        subtitleStyle.setFont(font1);
        subtitleStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        subtitleStyle.setVerticalAlign(VerticalAlign.TOP);
        subtitleStyle.setBorder(Border.PEN_1_POINT());
        subtitleStyle.setBackgroundColor(Color.WHITE);
        subtitleStyle.setBorderColor(Color.WHITE);
        subtitleStyle.setTextColor(new Color(127,127,127));
        subtitleStyle.setTransparency(Transparency.OPAQUE);

        drb      .setDetailHeight(10)
                .setReportName("")
                //               .setTitle("Invoice "+prov.getName()+" - "+period.getStartDate().format(DATE_FORMAT)+" to "+period.getEndDate().format(DATE_FORMAT))
                //                              .setSubtitle("This invoice was generated at " + period.getLockedDateTime().format(DATE_FORMAT))
                .setPageSizeAndOrientation(Page.Page_A4_Portrait())
                .setWhenNoData("No Data",subtitleStyle )
                .setUseFullPageWidth(true)//defines the height for each record of the report
                .setMargins(10, 10, 20, 20)		//define the margin space for each side (top, bottom, left and right)
                .setDefaultStyles(subtitleStyle, subtitleStyle, subtitleStyle, subtitleStyle)
        ;
        DynamicReport dr = drb.build();
        dr.setWhenNoDataType(DJConstants.WHEN_NO_DATA_TYPE_ALL_SECTIONS_NO_DETAIL);


        String dataSourcePath = "DataSource" + repNum;
        params.put(dataSourcePath, footerData);

        Subreport subreport = new SubReportBuilder()
                .setStartInNewPage(false)
                .setDataSource(DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION, dataSourcePath)
                .setDynamicReport(dr,  new ClassicLayoutManager())
                .build();
        builder.addConcatenatedReport(subreport);

    }


    private void revenueSubReport(S_User user, String currencySymbol, DynamicReportBuilder builder, DE_Invoice invoice,  List<DE_SeduledTwitterSearch> days ){
        try {

            String[] columnNames = {"Day", "tweets found","sent messages"};
            FastReportBuilder drb = new FastReportBuilder();
            for(int i=0; i<columnNames.length; i++){
                drb.addColumn(columnNames[i],"COLUMN_"+i, String.class.getName(), i!=1? 100: 60,false );
            }
            List<List<String>> revTable = new LinkedList<>();
            String [] blankRow = {"","",""};


            revTable.add(Arrays.asList(blankRow));

            List<String> revRow1 = new LinkedList();
            revRow1.add("Message Details by Day");
            revRow1.add("");
            revRow1.add("");
            revTable.add(revRow1);

            revTable.add(Arrays.asList(blankRow));
            
 /*           if (data.isHasUDAs()) {
                List<String> headRow1 = new LinkedList();
                headRow1.add("Associated Base Data Source for Period");
                headRow1.add("");
                headRow1.add("");
                revTable.add(headRow1);
                revTable.add(Arrays.asList(blankRow));
                List<String> udaHeadRow = new LinkedList();
                udaHeadRow.add("UDAs Verified");
                udaHeadRow.add("UDA Rate");
                udaHeadRow.add("Gross Private Revenue");
                revTable.add(udaHeadRow);
                List<String> udaRow = new LinkedList();
                udaRow.add(String.format("%.1f", data.getUdasVerfied()));
                udaRow.add(currencySymbol + String.format("%.2f", data.getUdaRate()));
                udaRow.add(currencySymbol + String.format("%.2f", data.getGrossPrivateRevenue()));
                revTable.add(udaRow);
                revTable.add(Arrays.asList(blankRow));
            }*/

            List<String> headRow1 = new LinkedList();
            headRow1.add("Date");
            headRow1.add("Tweets Found");
            headRow1.add("Messages Seng");
            revTable.add(headRow1);

            int nMessage=0;

            for(DE_SeduledTwitterSearch item : days){
                List<String> revRow = new LinkedList();
                revRow.add(item.getResultsDate().format(DateTimeFormatter.ISO_DATE));
                revRow.add(""+item.getNresults());
                revRow.add(""+item.getNsent());
                nMessage += item.getNsent();
                revTable.add(revRow);
            }

            revTable.add(Arrays.asList(blankRow));
            List<String> totalRow = new LinkedList();
            totalRow.add("");
            totalRow.add("Payment Due");
            String total = " "+ currencySymbol+String.format("%.2f", nMessage*PaymentInvoiceService.chargePerSend);
            totalRow.add(total);
            revTable.add(totalRow);

            Style mainStyle = new Style();
            Font font2 = new Font(9, "DejaVu Sans", false, false, false);
            mainStyle.setFont(font2);
            mainStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
            mainStyle.setVerticalAlign(VerticalAlign.TOP);
            mainStyle.setBorder(Border.PEN_1_POINT());
            mainStyle.setBackgroundColor(Color.WHITE);
            mainStyle.setBorderColor(Color.WHITE);
            mainStyle.setTextColor(Color.BLACK);
            mainStyle.setTransparency(Transparency.OPAQUE);

            Style boldStyle = new Style();
            Font font5 = new Font(9, "DejaVu Sans", true, false, false);
            boldStyle.setFont(font5);
            boldStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
            boldStyle.setVerticalAlign(VerticalAlign.TOP);
            boldStyle.setBorder(Border.PEN_1_POINT());
            boldStyle.setBackgroundColor(Color.WHITE);
            boldStyle.setBorderColor(Color.WHITE);
            boldStyle.setTextColor(Color.BLACK);
            boldStyle.setTransparency(Transparency.OPAQUE);

            Style boldLeftStyle = new Style();
            boldLeftStyle.setFont(font5);
            boldLeftStyle.setHorizontalAlign(HorizontalAlign.LEFT);
            boldLeftStyle.setVerticalAlign(VerticalAlign.TOP);
            boldLeftStyle.setBorder(Border.PEN_1_POINT());
            boldLeftStyle.setBackgroundColor(Color.WHITE);
            boldLeftStyle.setBorderColor(Color.WHITE);
            boldLeftStyle.setTextColor(Color.BLACK);
            boldLeftStyle.setTransparency(Transparency.OPAQUE);

            Style leftStyle = new Style();
            Font font3 = new Font(9, "DejaVu Sans", false, false, false);
            leftStyle.setFont(font3);
            leftStyle.setHorizontalAlign(HorizontalAlign.LEFT);
            leftStyle.setVerticalAlign(VerticalAlign.TOP);
            leftStyle.setBorder(Border.PEN_1_POINT());
            leftStyle.setBackgroundColor(Color.WHITE);
            leftStyle.setBorderColor(Color.WHITE);
            leftStyle.setTextColor(Color.BLACK);
            leftStyle.setTransparency(Transparency.OPAQUE);

            Style purpleStyle = new Style();
            Font font4 = new Font(11, "DejaVu Sans", true, false, false);
            purpleStyle.setFont(font4);
            purpleStyle.setHorizontalAlign(HorizontalAlign.LEFT);
            purpleStyle.setVerticalAlign(VerticalAlign.TOP);
            purpleStyle.setBorder(Border.PEN_1_POINT());
            purpleStyle.setBackgroundColor(Color.WHITE);
            purpleStyle.setBorderColor(Color.WHITE);
            purpleStyle.setTextColor(new Color(95, 0 , 255)); // Purple
            purpleStyle.setTransparency(Transparency.OPAQUE);

            Style blueStyle = new Style();
            Font font6 = new Font(11, "DejaVu Sans", true, false, false);
            blueStyle.setFont(font6);
            blueStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
            blueStyle.setVerticalAlign(VerticalAlign.TOP);
            blueStyle.setBorder(Border.PEN_1_POINT());
            blueStyle.setBackgroundColor(Color.WHITE);
            blueStyle.setBorderColor(Color.WHITE);
            blueStyle.setTextColor(Color.BLUE); // Blue
            blueStyle.setTransparency(Transparency.OPAQUE);

            Style blueUnderlineStyle = new Style();
            Font font8 = new Font(11, "DejaVu Sans", true, false, true);
            blueUnderlineStyle.setFont(font8);
            blueUnderlineStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
            blueUnderlineStyle.setVerticalAlign(VerticalAlign.TOP);
            blueUnderlineStyle.setBorder(Border.PEN_1_POINT());
            blueUnderlineStyle.setBackgroundColor(Color.WHITE);
            blueUnderlineStyle.setBorderColor(Color.WHITE);
            blueUnderlineStyle.setTextColor(Color.BLUE); // Blue
            blueUnderlineStyle.setTransparency(Transparency.OPAQUE);

            Style underlineStyle = new Style();
            Font font7 = new Font(9, "DejaVu Sans", false, false, true);
            underlineStyle.setFont(font7);
            underlineStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
            underlineStyle.setVerticalAlign(VerticalAlign.TOP);
            underlineStyle.setBorder(Border.PEN_1_POINT());
            underlineStyle.setBackgroundColor(Color.WHITE);
            underlineStyle.setBorderColor(Color.WHITE);
            underlineStyle.setTextColor(Color.BLACK);
            underlineStyle.setTransparency(Transparency.OPAQUE);

            Style headerStyle = new Style();
            Font font = new Font(13, "DejaVu Sans", true, false, false);
            headerStyle.setFont(font);
            headerStyle.setHorizontalAlign(HorizontalAlign.CENTER);
            headerStyle.setVerticalAlign(VerticalAlign.TOP);
            headerStyle.setBorder(Border.PEN_1_POINT());
            headerStyle.setBackgroundColor(Color.WHITE);
            headerStyle.setBorderColor(Color.WHITE);
            headerStyle.setTextColor(Color.BLACK);
            headerStyle.setTransparency(Transparency.OPAQUE);

            Style subtitleStyle = new Style();
            Font font1 = new Font(12, "DejaVu Sans", true, false, false);
            subtitleStyle.setFont(font1);
            subtitleStyle.setHorizontalAlign(HorizontalAlign.CENTER);
            subtitleStyle.setVerticalAlign(VerticalAlign.TOP);
            subtitleStyle.setBorder(Border.PEN_1_POINT());
            subtitleStyle.setBackgroundColor(Color.WHITE);
            subtitleStyle.setBorderColor(Color.WHITE);
            subtitleStyle.setTextColor(new Color(127,127,127));
            subtitleStyle.setTransparency(Transparency.OPAQUE);

            List<Map<String, String>> dataRows = new LinkedList<>();
            for(int j=0; j<revTable.size(); j++){
                List<String> row = revTable.get(j);
                if (row.size()< columnNames.length){
                    LOGGER.warn("row "+j+" "+row, " as wrong length");
                    if (row.size()>0){
                        LOGGER.warn("row ",row, " cell 1 is "+ row.get(0));
                    }
                } else {
                    Map<String, String> dataMap = new HashMap<>();
                    for (int i = 0; i < columnNames.length; i++) {
                        dataMap.put("COLUMN_" + i, row.get(i));
                    }
                    dataRows.add(dataMap);
                }
            }

            drb      .setDetailHeight(30)
                    .setReportName("Invoice for "+user.getFirstname()+" "+user.getLastname())
                    .setTitle("Invoice ")
                    .setSubtitle(invoice.getInvoiceDate().minusMonths(1).plusDays(1).format(DATE_FORMAT)+
                            " to "+invoice.getInvoiceDate().format(DATE_FORMAT))
                    .setPageSizeAndOrientation(Page.Page_A4_Portrait())
                    .setWhenNoData("No Data", headerStyle)
                    .setUseFullPageWidth(true)
                    .setDetailHeight(15)		//defines the height for each record of the report
                    .setMargins(10, 20, 50, 50)		//define the margin space for each side (top, bottom, left and right)
                    .setDefaultStyles(headerStyle, subtitleStyle, mainStyle, mainStyle)
            ;
            DynamicReport dr = drb.build();

            List<ConditionalStyle> headerRowsStyles = new LinkedList<>();
            String purpleFields[] = {"Revenue Details", "Deduction Details"};
            ConditionalStyle purpleCStyle = new ConditionalStyle(new StringTextConditionStyle("COLUMN_0",purpleFields), purpleStyle);
            String boldFields[] = {"Associated Base Data Source for Period", "Revenue Description", "Deduction Description"};
            ConditionalStyle boldCStyle = new ConditionalStyle(new StringTextConditionStyle("COLUMN_0", boldFields), boldLeftStyle);
            headerRowsStyles.add(boldCStyle);
            headerRowsStyles.add(purpleCStyle);
            
            List<AbstractColumn> cols = dr.getColumns();
            cols.get(0).setStyle(leftStyle);
            cols.get(0).setConditionalStyles(headerRowsStyles);

            String amountHeadersFields[] = {"Gross Amount", "Net Due", "Net Cost"};

            ConditionalStyle colHeadCStyle1 = new ConditionalStyle(new StringTextConditionStyle("COLUMN_1", amountHeadersFields), boldStyle);
            ConditionalStyle colHeadCStyle2 = new ConditionalStyle(new StringTextConditionStyle("COLUMN_2", amountHeadersFields), boldStyle);
            
            String blueFields[] = {"Payment Due"};
            String underlineFields[] = {}; //earnBefore, deductTotal};
            String blueUnderlineFields[] = {total};

            List<ConditionalStyle> colheadRowsStyles = new LinkedList<>();
            colheadRowsStyles.add(colHeadCStyle1);

            ConditionalStyle blueCStyle = new ConditionalStyle(new StringTextConditionStyle("COLUMN_2", blueFields ), blueStyle );
            ConditionalStyle blueCStyle1 = new ConditionalStyle(new StringTextConditionStyle("COLUMN_1", blueFields ), blueStyle );
            ConditionalStyle blueCStyle2 = new ConditionalStyle(new StringTextConditionStyle("COLUMN_2", blueUnderlineFields ), blueUnderlineStyle );

            colheadRowsStyles.add(blueCStyle);
            colheadRowsStyles.add(blueCStyle1);

            cols.get(1).setStyle(mainStyle);
            cols.get(1).setConditionalStyles(colheadRowsStyles);

            ConditionalStyle underlineCStyle = new ConditionalStyle(new StringTextConditionStyle("COLUMN_2", underlineFields ), underlineStyle );

            List<ConditionalStyle> grossRowsStyles = new LinkedList<>();
            grossRowsStyles.add(blueCStyle2);
            grossRowsStyles.add(colHeadCStyle2);
            grossRowsStyles.add(underlineCStyle);
            cols.get(2).setStyle(mainStyle);
            cols.get(2).setConditionalStyles(grossRowsStyles);

            dr.setWhenNoDataType(DJConstants.WHEN_NO_DATA_TYPE_ALL_SECTIONS_NO_DETAIL);

            String dataSourcePath = "DataSource" + 1;
         //   params.put(dataSourcePath, dataRows);

            Subreport subreport = new SubReportBuilder()
                    .setStartInNewPage(false)
                    .setDataSource(DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION, dataSourcePath)
                    .setDynamicReport(dr,  new ClassicLayoutManager())
                    .build();
            builder.addConcatenatedReport(subreport);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class StringTextConditionStyle extends ConditionStyleExpression {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringTextConditionStyle.class);

    private List<String> testStrings;
    private String fieldName;

    public StringTextConditionStyle(String fieldName, String test[]){
        this.testStrings = Arrays.asList( test );
        this.fieldName = fieldName;
    }

    @Override
    public Object evaluate(Map fields, Map variables, Map parameters) {
        Object currentValue = fields.get(fieldName);
        if ("£0.00".equals(currentValue)){ return false; }
        boolean test =  testStrings.stream().anyMatch(x->x.equals(currentValue));
        return test;
    }

    public String getClassName() {
        return Boolean.class.getName();
    }

}


