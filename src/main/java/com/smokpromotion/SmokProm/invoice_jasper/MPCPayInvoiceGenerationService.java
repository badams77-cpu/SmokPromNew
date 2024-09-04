package com.smokpromotion.SmokProm.invoice_jasper;

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.builders.SubReportBuilder;
import ar.com.fdvs.dj.domain.constants.*;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.entities.Subreport;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionStyleExpression;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionalStyle;
import com.urcompliant.config.portal.PortalEmailConfig;
import com.urcompliant.config.portal.PortalSecurityPrinciple;
import com.urcompliant.config.portal.PortalSecurityPrincipleService;
import com.urcompliant.controller.portal.mpcpay.domain.ProviderDetailData;
import com.urcompliant.controller.portal.mpcpay.domain.ProviderEmailDetailData;
import com.urcompliant.controller.portal.mpcpay.domain.ProviderItem;
import com.urcompliant.domain.EmailLanguage;
import com.urcompliant.domain.PortalEnum;
import com.urcompliant.domain.entity.DE_User;
import com.urcompliant.domain.entity.LanguageSettingEnum;
import com.urcompliant.domain.entity.mpcpay.*;
import com.urcompliant.domain.repository.MPCAppDBConnectionFactory;
import com.urcompliant.domain.repository.mpcpay.*;
import com.urcompliant.domain.service.DS_UserService;
import com.urcompliant.practicedata.mpcpay.PayCalculationData;
import com.urcompliant.practicedata.mpcpay.ProviderPayDetailService;
import com.urcompliant.practicedata.mpcpay.ProviderPayEmailDetailService;
import com.urcompliant.service.EmailPreview;
import com.urcompliant.service.SmtpMailWrapper;
import com.urcompliant.util.CryptoException;
import com.urcompliant.util.FileToZip;
import com.urcompliant.util.GenericUtils;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.fill.JRFillVariable;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOdsExporterConfiguration;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Profile("portal")
@Service
public class MPCPayInvoiceGenerationService {

    private static final int SECONDS_IN_HALF_HOUR = 1800;

    private static final Logger LOGGER = LoggerFactory.getLogger(MPCPayInvoiceGenerationService.class);




    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(("EEEE dd MMMM yyyy "));
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern(("HH:mm:ss EEEE dd MMMM yyyy"));
    public static final int MINUTES_IN_HOUR = 60;

    private final DR_PayPeriod drPayPeriod;
    private final DR_PeriodProvider drPeriodProvider;
    private final DR_PayCalculationDataRow drPayCalculationDataRow;
    private final DR_ResultView drResultView;
    private final DR_MPCPayProvider drMpcPayProvider;
    private final DR_MPCPayPractice drMpcPayPractice;
    private final ProviderPayEmailDetailService providerPayEmailDetailService;
    private final ProviderPayDetailService providerPayDetailService;
    private final PortalSecurityPrincipleService portalSecurityPrincipleService;
    private final MPCAppDBConnectionFactory dbConnectionFactory;

    private final SmtpMailWrapper smtpMailSender;

    private final PortalEmailConfig emailConfig;



    private ZoneId zoneId;
    private ZoneId systemZoneId;


    @Autowired
    public MPCPayInvoiceGenerationService(

            SmtpMailWrapper smptMailSender,
            DR_PayPeriod drPayPeriod,
            DR_PayCalculationDataRow drPayCalculationDataRow,
            DR_ResultView drResultView,
            DR_MPCPayProvider drMpcPayProvider,
            DR_PeriodProvider drPeriodProvider,
            DR_MPCPayPractice drMpcPayPractice,
            ProviderPayEmailDetailService providerPayEmailDetailService,
            ProviderPayDetailService providerPayDetailService,
            PortalSecurityPrincipleService principleService,
            @Value("${MPC_SCHEDULER_TIMEZONE:Europe/London}") String timezone,
            MPCAppDBConnectionFactory dbConnectionFactory,
            PortalEmailConfig emailConfig
    ) {
        this.drPayCalculationDataRow = drPayCalculationDataRow;
        this.drResultView = drResultView;
        this.drMpcPayProvider = drMpcPayProvider;
        this.drMpcPayPractice = drMpcPayPractice;
        this.drPeriodProvider = drPeriodProvider;
        this.drPayPeriod = drPayPeriod;
        this.providerPayEmailDetailService = providerPayEmailDetailService;
        this.providerPayDetailService = providerPayDetailService;
        this.portalSecurityPrincipleService = principleService;
        this.emailConfig = emailConfig;
        this.smtpMailSender = smptMailSender;
        this.dbConnectionFactory = dbConnectionFactory;
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


   public List<FileToZip> generateForPeriod(PortalSecurityPrinciple principle, DE_PayPeriod payPeriod){
        List<DE_PeriodProvider> periodProviders = drPeriodProvider.getAllForPeriod(principle.getPortal(), principle.getPracticeGroupId(), payPeriod.getId())
               .stream().sorted(Comparator.comparing(DE_PeriodProvider::getMpcPayProviderId)).collect(Collectors.toList());
        List<FileToZip> files = new LinkedList<>();
        for(DE_PeriodProvider provider : periodProviders) {
            Optional<DE_MPCPayProvider> prov = drMpcPayProvider.getByProviderId(principle.getPortal(), principle.getPracticeGroupId(), provider.getMpcPayProviderId());
            if (!prov.isPresent()){ continue; }
            Optional<DE_ResultView> latestView = drResultView.getLatestForPeriod(principle.getPortal(), principle.getPracticeGroupId(), payPeriod.getId());
            PayCalculationData data = null;
            if (latestView.isPresent()) {
                data = drPayCalculationDataRow.getDataByProvider(principle.getPortal(), principle.getPracticeGroupId(), payPeriod.getId(), latestView.get().getId(), provider.getMpcPayProviderId());
            } else {
                continue;
            }
            ProviderEmailDetailData emailData = providerPayEmailDetailService.execute(principle.getCurrencySymbol(), data, payPeriod, provider.getMpcPayProviderId());
            ProviderDetailData detailData = providerPayDetailService.execute(principle.getCurrencySymbol(), data, payPeriod, true);
            try {
                files.add(generateInvoice(principle.getPortal(), prov.get(), emailData, detailData, payPeriod, latestView.get(), principle.getCurrencySymbol(), principle.getUserLanguage()));
            } catch (Exception e){
                LOGGER.warn("generateForPeriod: error generate jasper invoice for provider "+prov.get().getProviderCode()+" group: "+prov.get().getPracticeGroupId()+" user: "+principle.getEmail(),e);
            }
        }
        return files;
    }

    public FileToZip generateForProvider(PortalSecurityPrinciple principle, DE_PayPeriod payPeriod, int mpcProviderId) throws Exception {
        List<DE_PeriodProvider> periodProviders = drPeriodProvider.getAllForPeriod(principle.getPortal(), principle.getPracticeGroupId(), payPeriod.getId())
                .stream().sorted(Comparator.comparing(DE_PeriodProvider::getMpcPayProviderId)).collect(Collectors.toList());
        Optional<DE_MPCPayProvider> prov = drMpcPayProvider.getByProviderId(principle.getPortal(), principle.getPracticeGroupId(), mpcProviderId);
        if (!prov.isPresent()){ return null; }
        Optional<DE_ResultView> latestView = drResultView.getLatestForPeriod(principle.getPortal(), principle.getPracticeGroupId(), payPeriod.getId());
        PayCalculationData data = null;
        if (latestView.isPresent()) {
            data = drPayCalculationDataRow.getDataByProvider(principle.getPortal(), principle.getPracticeGroupId(), payPeriod.getId(), latestView.get().getId(), mpcProviderId);
        } else {
            return null;
        }
        ProviderEmailDetailData emailData = providerPayEmailDetailService.execute(principle.getCurrencySymbol(), data, payPeriod, mpcProviderId);
        ProviderDetailData detailData = providerPayDetailService.execute(principle.getCurrencySymbol(), data, payPeriod, true);
        return generateInvoice(principle.getPortal(),prov.get(), emailData, detailData, payPeriod, latestView.get(), principle.getCurrencySymbol(), principle.getUserLanguage());

    }

    private FileToZip generateInvoice(PortalEnum portal,
                                      DE_MPCPayProvider provider,
                                      ProviderEmailDetailData providerDetailData,
                                      ProviderDetailData payDetail,
                                      DE_PayPeriod payPeriod,
                                      DE_ResultView resultView,
                                      String currency,
                                      LanguageSettingEnum languageIn) throws Exception {


        Optional<DE_MPCPayPractice> optPractice = drMpcPayPractice.getByPracticeId(portal, provider.getPracticeGroupId(), provider.getPracticeId());
        return jasperExport(currency, payPeriod, optPractice, provider, payDetail);
    }



    private FileToZip jasperExport(String currencySymbol, DE_PayPeriod period, Optional<DE_MPCPayPractice> optPractice, DE_MPCPayProvider prov,ProviderDetailData data) throws Exception {

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
                .setReportName("Invoice "+prov.getName())
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
        addrTable.add( prov.getProviderBusinessName());
        addrTable.add( prov.getAddress1());
        if (prov.getAddress2()!=null && !prov.getAddress2().equals("")) addrTable.add( prov.getAddress2());
        addrTable.add( prov.getTownCity());
        addrTable.add( prov.getCountyState());
        addrTable.add( prov.getPostCode());
        addrTable.add("");
        addrTable.add("Invoice Date: "+(period.getLockedDateTime()!=null?period.getLockedDateTime().format(DATE_FORMAT):" No Lock Date" ));

        List<Map<String,String>> addrData = new LinkedList<>();

        for(int j=0; j<addrTable.size(); j++){
            Map<String, String> dataMap = new HashMap<>();
            for(int i=0; i<addrCols.length; i++){
                dataMap.put("ADDR_"+i, i==addrCols.length-1? addrTable.get(j): "");
            }
            addrData.add(dataMap);
        }

        params.put("address", addrData );

        practiceAddressSubReport(params, drb,1, optPractice);

        revenueSubReport(params, currencySymbol, drb, 2, prov, period, data);

        footerSubReport(params, drb,3);

        DynamicReport dr = drb.build();

        JasperPrint jasperPrint = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(),  new JRBeanCollectionDataSource(addrData), params);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        JasperExportManager.exportReportToPdfStream(jasperPrint,out);

        byte[] binData = out.toByteArray();
        FileToZip file= new FileToZip();
        file.setFilename(optPractice.orElse(new DE_MPCPayPractice()).getPracticeCode()+"_"+prov.getProviderCode()+".pdf");
        file.setFile(binData);
        return file;
    }

    private void setDefaultPdfFontEmbedded() {
        JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();
        jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.font.name", "net/sf/jasperreports/fonts/dejavu/DejaVuSans.ttf");
        jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
    }

    private void practiceAddressSubReport(Map<String, Object> params, DynamicReportBuilder builder,  int repNum, Optional<DE_MPCPayPractice> practiceOpt) throws Exception {
        if (!practiceOpt.isPresent()){
            return;
        }
        DE_MPCPayPractice prac = practiceOpt.get();
        FastReportBuilder drb = new FastReportBuilder();
        String[] addrCols = {"","","",""};
        for(int i=0; i<addrCols.length; i++){
            drb.addColumn(addrCols[i],"PRACADDR_"+i, String.class.getName(), 150,true );
        }
        List<String> addrTable = new LinkedList<>();
        addrTable.add( prac.getPracticeBusinessName());
        addrTable.add( prac.getAddress1());
        if (prac.getAddress2()!=null && !prac.getAddress2().equals("")){ addrTable.add( prac.getAddress2()); }
        addrTable.add( prac.getTownCity());
        addrTable.add( prac.getCountyState());
        addrTable.add( prac.getPostCode());

        List<Map<String,String>> addrData = new LinkedList<>();

        for(int j=0; j<addrTable.size(); j++){
            Map<String, String> dataMap = new HashMap<>();
            for(int i=0; i<addrCols.length; i++){
                dataMap.put("PRACADDR_"+i, i==0? addrTable.get(j): "");
            }
            addrData.add(dataMap);
        }

        params.put("pracAddress", addrData );

        Style titleStyle = new Style();
        Font font2 = new Font(1, "DejaVu Sans", false, false, false);
        titleStyle.setFont(font2);
        titleStyle.setHorizontalAlign(HorizontalAlign.LEFT);
        titleStyle.setVerticalAlign(VerticalAlign.TOP);
        titleStyle.setBorder(Border.PEN_1_POINT());
        titleStyle.setBackgroundColor(Color.WHITE);
        titleStyle.setBorderColor(Color.WHITE);
        titleStyle.setTextColor(Color.BLACK);
        titleStyle.setTransparency(Transparency.OPAQUE);
        
        Style subtitleStyle = new Style();
        Font font1 = new Font(10, "DejaVu Sans", false, false, false);
        subtitleStyle.setFont(font1);
        subtitleStyle.setHorizontalAlign(HorizontalAlign.LEFT);
        subtitleStyle.setVerticalAlign(VerticalAlign.TOP);
        subtitleStyle.setBorder(Border.PEN_1_POINT());
        subtitleStyle.setBackgroundColor(Color.WHITE);
        subtitleStyle.setBorderColor(Color.WHITE);
        subtitleStyle.setTextColor(Color.BLACK);
        subtitleStyle.setTransparency(Transparency.OPAQUE);

        drb      .setDetailHeight(10)
                .setReportName("Invoice "+prac.getPracticeName())
                //               .setTitle("Invoice "+prov.getName()+" - "+period.getStartDate().format(DATE_FORMAT)+" to "+period.getEndDate().format(DATE_FORMAT))
                //                              .setSubtitle("This invoice was generated at " + period.getLockedDateTime().format(DATE_FORMAT))
                .setPageSizeAndOrientation(Page.Page_A4_Portrait())
                .setWhenNoData("No Data", subtitleStyle)
                .setUseFullPageWidth(true)//defines the height for each record of the report
                .setMargins(0, 0, 50, 50)		//define the margin space for each side (top, bottom, left and right)
                .setDefaultStyles(titleStyle, titleStyle, titleStyle, subtitleStyle)
        ;
        DynamicReport dr = drb.build();
        dr.setWhenNoDataType(DJConstants.WHEN_NO_DATA_TYPE_ALL_SECTIONS_NO_DETAIL);


        String dataSourcePath = "DataSource" + repNum;
        params.put(dataSourcePath, addrData);

        Subreport subreport = new SubReportBuilder()
                .setStartInNewPage(false)
                .setDataSource(DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION, dataSourcePath)
                .setDynamicReport(dr,  new ClassicLayoutManager())
                .build();
        builder.addConcatenatedReport(subreport);

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


    private void revenueSubReport(Map<String,Object> params, String currencySymbol, DynamicReportBuilder builder, int repNum, DE_MPCPayProvider prov, DE_PayPeriod period, ProviderDetailData data  ){
        try {

            String[] columnNames = {"", "", ""};
            FastReportBuilder drb = new FastReportBuilder();
            for(int i=0; i<columnNames.length; i++){
                drb.addColumn(columnNames[i],"COLUMN_"+i, String.class.getName(), i!=1? 100: 60,false );
            }
            List<List<String>> revTable = new LinkedList<>();
            String [] blankRow = {"","",""};


            revTable.add(Arrays.asList(blankRow));

            List<String> revRow1 = new LinkedList();
            revRow1.add("Revenue Details");
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
            headRow1.add("Revenue Description");
            headRow1.add("Gross Amount");
            headRow1.add("Net Due");
            revTable.add(headRow1);
            for(ProviderItem item :data.getIncomeItems()){
                List<String> revRow = new LinkedList();
                revRow.add(item.getItemName());
                revRow.add(item.getItemGrossValueFormatted());
                revRow.add(item.getItemValueFormatted());
                revTable.add(revRow);
            }
            for(ProviderItem item :data.getCapitationItems()){
                List<String> revRow = new LinkedList();
                revRow.add(item.getItemName());
                revRow.add(item.getItemGrossValueFormatted());
                revRow.add(item.getItemValueFormatted());
                revTable.add(revRow);
            }
            for(ProviderItem item :data.getOtherIncomeItems()){
                List<String> revRow = new LinkedList();
                revRow.add(item.getItemName());
                revRow.add(item.getItemGrossValueFormatted());
                revRow.add(item.getItemValueFormatted());
                revTable.add(revRow);
            }
            revTable.add(Arrays.asList(blankRow));

            List<String> revRow = new LinkedList();
            revRow.add("Earnings before deductions");
            revRow.add("");
            String earnBefore = " "+data.getEarningBeforeDeductionsFormatted(currencySymbol);
            revRow.add(earnBefore);
            revTable.add(revRow);

            revTable.add(Arrays.asList(blankRow));

            List<String> deductRow1 = new LinkedList();
            deductRow1.add("Deduction Details");
            deductRow1.add("");
            deductRow1.add("");
            revTable.add(deductRow1);


            revTable.add(Arrays.asList(blankRow));
            
            List<String> headRow2 = new LinkedList();
            headRow2.add("Deduction Description");
            headRow2.add("Gross Amount");
            headRow2.add("Net Cost");
            revTable.add(headRow2);
            for(ProviderItem item :data.getDeductionItems()){
                List<String> deductRow = new LinkedList();
                deductRow.add(item.getItemName());
                deductRow.add(item.getItemGrossValueFormatted());
                deductRow.add(item.getItemValueFormatted());
                revTable.add(deductRow);
            }
            revTable.add(Arrays.asList(blankRow));
            List<String> deductTotalRow = new LinkedList();
            deductTotalRow.add("Total Deductions");
            deductTotalRow.add("");
            String deductTotal = " "+ currencySymbol+String.format("%.2f", data.getDeductionTotal());
            deductTotalRow.add(deductTotal);
            revTable.add(deductTotalRow);

            revTable.add(Arrays.asList(blankRow));
            List<String> totalRow = new LinkedList();
            totalRow.add("");
            totalRow.add("Payment Due");
            String total = " "+ currencySymbol+String.format("%.2f",data.getIncomeTotal());
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
                    .setReportName("Invoice "+period.getDescription())
                    .setTitle("Invoice "+period.getDescription())
                    .setSubtitle(period.getStartDate().format(DATE_FORMAT)+" to "+period.getEndDate().format(DATE_FORMAT))
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
            String underlineFields[] = {earnBefore, deductTotal};
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



            String dataSourcePath = "DataSource" + repNum;
            params.put(dataSourcePath, dataRows);

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


