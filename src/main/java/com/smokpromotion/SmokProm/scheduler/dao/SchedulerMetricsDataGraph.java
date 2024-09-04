package com.smokpromotion.SmokProm.scheduler.dao;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.urcompliant.util.DateUtils;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public class SchedulerMetricsDataGraph {

    private static final String D3_DATE_FORMAT_DD_MMM_YY = "%d %b %y";

    public SchedulerMetricsDataGraph(){}
    private MessageSource messageSource;

    public SchedulerMetricsDataGraph(boolean showSuccess) {
        this.showSuccess = showSuccess;
    }

    private boolean showSuccess;



    public Map<String ,List<Plot>> plots = new LinkedHashMap<>();

    public Map<String ,List<Plot>> getPlots() {
        return plots;
    }



    public void addPlotsForAllKeyItem( List<DE_SchedulerMetrics> toPlot) {
        for (SchedulerMetricsCategoryEnum metric : SchedulerMetricsCategoryEnum.values()) {
            int value = 0;
            Map<LocalDate, Integer> values = new HashMap<>();
            switch ( metric){
                case FAILURE_COUNT:
                    values = toPlot.stream().collect(Collectors.toMap( DE_SchedulerMetrics::getDate , DE_SchedulerMetrics::getFailureCount, (x1,x2)->x1));
                    break;
                case SUCCESS_COUNT:
                    values = toPlot.stream().collect(Collectors.toMap( DE_SchedulerMetrics::getDate , DE_SchedulerMetrics::getSuccessCount, (x1,x2)->Integer.max(x1,x2)));
                    break;
                case RUNNING_COUNT:
                    values = toPlot.stream().collect(Collectors.toMap( DE_SchedulerMetrics::getDate , DE_SchedulerMetrics::getRunCount, (x1,x2)->Integer.max(x1,x2)));
                    break;
                case SECONDS_RUNNING:
                    values = toPlot.stream().collect(Collectors.toMap( DE_SchedulerMetrics::getDate , DE_SchedulerMetrics::getSecondsRunning, (x1,x2)->Integer.max(x1,x2)));
                    break;
                case SECONDS_NOT_RUNNING:
                    values = toPlot.stream().collect(Collectors.toMap( DE_SchedulerMetrics::getDate , DE_SchedulerMetrics::getSecondsNotRunning, (x1,x2)->Integer.max(x1,x2)));
                    break;
                case SECONDS_QUEUE_EMPTY:
                    values = toPlot.stream().collect(Collectors.toMap( DE_SchedulerMetrics::getDate , DE_SchedulerMetrics::getSecondsQueueEmpty, (x1,x2)->Integer.max(x1,x2)));
                    break;
                case SECONDS_QUEUED:
                    values = toPlot.stream().collect(Collectors.toMap( DE_SchedulerMetrics::getDate , DE_SchedulerMetrics::getSecondsQueued, (x1,x2)->Integer.max(x1,x2)));
                    break;
            }
            Plot plot = new Plot();
            plot.label = metric.name();
            plot.labelTranslation =  metric.toString();
            plot.id = Integer.toString(metric.ordinal()+1);
            plot.isCurrency = false;
            plot.mouseoverDateFormat = D3_DATE_FORMAT_DD_MMM_YY;
            for (LocalDate localDate : toPlot.stream().map( x-> x.getDate()).collect(Collectors.toList())) {
                plot.points.add(new PlotPoint(localDate, Integer.toString(values.get(localDate))));
            }
            Collections.sort(plot.points);

            if (!plots.containsKey(metric.toString())) {
                plots.put(metric.toString(), new LinkedList<>());
            }

            plots.get(metric.toString()).add(plot);
        }

    }





    public class Plot {
        public String label;
        public String labelTranslation;
        public List<PlotPoint> points = new ArrayList<>();
        public String id;
        public boolean isCurrency;
        public String mouseoverDateFormat;

        @Override
        public String toString() {
            return "Plot{" +
                    "label='" + label + '\'' +
                    ", labelTranslation='" + labelTranslation + '\'' +
                    ", points=" + points +
                    ", id='" + id + '\'' +
                    '}';
        }
    }

   public class PlotPoint implements Comparable<PlotPoint> {



        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "dd-MMM-yy")
        public LocalDate date;
        public String value;
        public String dateStr;

        PlotPoint(LocalDate date, String value) {
            this.date = date;
            this.value = value;

            try {
                String dateStr =    DateUtils.fromDateToString(DateUtils.fromLocalDateToDate(date),"dd-MMM-yy");
                this.dateStr = dateStr;
            } catch (Exception e ) {
                e.printStackTrace();
            }
        }


        public int compareTo(PlotPoint o) {
            return this.date.compareTo(o.date);
        }


       @Override
       public String toString() {
           return "PlotPoint{" +
                   "date=" + date +
                   ", value='" + value + '\'' +
                   ", dateStr='" + dateStr + '\'' +

                   '}';
       }


   }
}
