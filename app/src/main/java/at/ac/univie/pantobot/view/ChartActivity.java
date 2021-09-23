package at.ac.univie.pantobot.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import at.ac.univie.pantobot.R;
import at.ac.univie.pantobot.model.Topic;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;


/**
 * This activity is used to present a chart in its own activity about the three most influential topics of the asked domain
 *
 * Useful links:
 *
 * http://stackoverflow.com/questions/24591347/overlap-views-in-horizontal-linearlayout
 * https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/BarChartActivityMultiDataset.java
 * http://stackoverflow.com/questions/35240289/how-to-create-a-barchart-with-grouped-bars-with-mpandroidchart
 *
 */
public class ChartActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolBarTitle;
    private ArrayList<Topic> topicArrayList= new ArrayList<>();
    private TextView headerText;

    private PieChart pieChart;
    private BarChart barChart;

    /**
     * Default onCreate Class
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        initializeVariables();

        unwrapIntent();

    }

    /**
     * Unwrap the intent and get arraylist of topics.
     */

    private void unwrapIntent(){
        //Unwrap the intent and get arraylist of topics.
        if(getIntent() != null && getIntent().getExtras() != null){
            topicArrayList= (ArrayList<Topic>) getIntent().getExtras().getSerializable(HomeActivity.EXTRA_TOPICS);
            //create Chart
            //createPieChart();
            //headerText.setText(getString(R.string.chart_header_available));
            createGroupedBarChart();
        } else{
            headerText.setText(getString(R.string.chart_header_notavailable));//("No data available, please try it later again");

            //create test chart if no arraylist of topics available
            createTestGroupedBarChart();
            //createTestPieChart();
        }

        //Test
        //createTestPieChart();
    }

    /**
     * Initializes the Variables
     */

    private void initializeVariables() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolBarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolBarTitle.setTextColor(Color.BLACK);

        // Enable the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        headerText = (TextView) findViewById(R.id.headerText);

        barChart =(BarChart) findViewById(R.id.barChart);
        barChart.setNoDataText("");
        pieChart = (PieChart) findViewById(R.id.pieChart);
        pieChart.setNoDataText("");
    }

    /**
     * creates a grouped bar chart
     */

    private void createGroupedBarChart(){
        //set header text
        toolBarTitle.setText(getString(R.string.groupedbarchart_title));
        headerText.setText(getString(R.string.groupedbarchart_header_available));

        //disable some options that aren´t needed
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        //disable Description
        Description description = new Description();
        description.setEnabled(false);
        //description.setText("Philipp´s pie chart");
        barChart.setDescription(description);

        barChart.setMaxVisibleValueCount(50); //50
        barChart.setPinchZoom(true);
        barChart.setDrawGridBackground(false);

        //create an arraylist of the group labels
        final ArrayList<String> groupLabels= new ArrayList<>();
        groupLabels.add("");
        groupLabels.add("Influence");
        groupLabels.add("Year");
        groupLabels.add("Sum weight related topics");

        //set the x axis and map them with the group label arraylist
        XAxis topAxis = barChart.getXAxis();
        topAxis.setGranularity(1f);
        topAxis.setCenterAxisLabels(true);
        topAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(((int)value)<groupLabels.size())
                {
                    return  (groupLabels.get((int)value));
                }
                else
                {
                    return "";
                }
            }

            /*@Override
            public int getDecimalDigits() {
                return 0;
            }*/
        });
        topAxis.setTextSize(8f);

        //set the y axis to only one decimal after separator
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf(new BigDecimal(String.valueOf(value)).setScale(1, BigDecimal.ROUND_HALF_UP));
            }

            /*@Override
            public int getDecimalDigits() {
                return 0;
            }*/
        });
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(30f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true

        //disable right axis
        barChart.getAxisRight().setEnabled(false);

        //calculate interval of one group
        float groupSpace = 0.04f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.3f; // x2 dataset
        // (0.3 + 0.02) * 3 + 0.04 = 1.00 -> interval per "group"

        //Range of group on x axis
        int startXRange = 1;
        int endXRange = 4; //1985


        //get data from the unwrap intent and create entries for the bars
        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> entries2 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> entries3 = new ArrayList<BarEntry>();
        int positionArraylist=topicArrayList.size()-3;
        for(int i=startXRange;i<endXRange;i++){
            switch (i){
                case 1:
                    entries1.add(new BarEntry(i, (float)topicArrayList.get(positionArraylist).getInfluence(),"influence1"));
                    entries2.add(new BarEntry(i, (float)topicArrayList.get(++positionArraylist).getInfluence(),"influence2"));
                    entries3.add(new BarEntry(i, (float)topicArrayList.get(++positionArraylist).getInfluence(),"influence3"));
                    break;
                case 2:
                    positionArraylist=topicArrayList.size()-3;
                    entries1.add(new BarEntry(i, (float)topicArrayList.get(positionArraylist).getTimestamp(),"year1"));
                    entries2.add(new BarEntry(i, (float)topicArrayList.get(++positionArraylist).getTimestamp(),"year2"));
                    entries3.add(new BarEntry(i, (float)topicArrayList.get(++positionArraylist).getTimestamp(),"year3"));
                    break;
                case 3:
                    positionArraylist=topicArrayList.size()-3;
                    ArrayList<Topic.Related> relTopics =topicArrayList.get(positionArraylist).getRelated_topics();
                    System.out.println("First id: "+topicArrayList.get(positionArraylist).getId());
                    int sumWeight=0;
                    if(relTopics!=null && relTopics.size()>0){
                        for(int j=0;j<relTopics.size();j++){
                            sumWeight+=relTopics.get(j).getWeight();
                        }
                    }
                    entries1.add(new BarEntry(i, (float)sumWeight,"sum rel weight1"));

                    relTopics =topicArrayList.get(++positionArraylist).getRelated_topics();
                    System.out.println("Second id: "+topicArrayList.get(positionArraylist).getId());
                    sumWeight=0;
                    if(relTopics!=null && relTopics.size()>0){
                        for(int j=0;j<relTopics.size();j++){
                            sumWeight+=relTopics.get(j).getWeight();
                        }
                    }
                    entries2.add(new BarEntry(i, (float)sumWeight,"sum rel weight2"));

                    relTopics =topicArrayList.get(++positionArraylist).getRelated_topics();
                    System.out.println("Third id: "+topicArrayList.get(positionArraylist).getId());
                    sumWeight=0;
                    if(relTopics!=null && relTopics.size()>0){
                        for(int j=0;j<relTopics.size();j++){
                            sumWeight+=relTopics.get(j).getWeight();
                        }
                    }
                    entries3.add(new BarEntry(i, (float)sumWeight,"sum rel weight3"));
                    break;
                default: break;
            }


        }

        //create dataset and map them correctly, assign color, etc
        BarDataSet set1, set2, set3;

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet)barChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet)barChart.getData().getDataSetByIndex(1);
            set3 = (BarDataSet)barChart.getData().getDataSetByIndex(3);
            set1.setValues(entries1);
            set2.setValues(entries2);
            set3.setValues(entries3);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            // create 2 datasets with different types
            set1 = new BarDataSet(entries1, topicArrayList.get(topicArrayList.size()-3).getTopic());
            set1.setColor(ColorTemplate.JOYFUL_COLORS[0]);
            set2 = new BarDataSet(entries2, topicArrayList.get(topicArrayList.size()-2).getTopic());
            set2.setColor(ColorTemplate.JOYFUL_COLORS[1]);
            set3 = new BarDataSet(entries3, topicArrayList.get(topicArrayList.size()-1).getTopic());
            set3.setColor(ColorTemplate.JOYFUL_COLORS[2]);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            dataSets.add(set2);
            dataSets.add(set3);

            BarData data = new BarData(dataSets);
            barChart.setData(data);

        }

        //disable some options
        barChart.getBarData().setBarWidth(barWidth);
        barChart.getXAxis().setAxisMinValue(startXRange);
        barChart.getXAxis().setAxisMaximum(endXRange);
        barChart.groupBars(startXRange, groupSpace, barSpace);

        //make  three second dynamic creating of the chart
        barChart.animateY(3000);

        //refresh the chart
        barChart.invalidate();

    }

    private void createTestGroupedBarChart(){

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);

        Description description = new Description();
        description.setEnabled(false);
        //description.setText("Philipp´s pie chart");
        barChart.setDescription(description);

        barChart.setMaxVisibleValueCount(50); //50
        barChart.setPinchZoom(true);
        barChart.setDrawGridBackground(false);

        final ArrayList<String> groupLabels= new ArrayList<>();
        groupLabels.add("");
        groupLabels.add("Influence");
        groupLabels.add("Year");
        groupLabels.add("Sum weight related topics");


        XAxis topAxis = barChart.getXAxis();

        topAxis.setGranularity(1f);
        topAxis.setCenterAxisLabels(true);

        topAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                //return String.valueOf((int) value);
                if(((int)value)<groupLabels.size())
                {
                    return  (groupLabels.get((int)value));
                }
                else
                {
                    return "";
                }
            }

            /*@Override
            public int getDecimalDigits() {
                return 0;
            }*/
        });
        topAxis.setTextSize(8f);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf(new BigDecimal(String.valueOf(value)).setScale(1, BigDecimal.ROUND_HALF_UP));
            }

            /*@Override
            public int getDecimalDigits() {
                return 0;
            }*/
        });
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(30f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true
        barChart.getAxisRight().setEnabled(false);

        //data
        float groupSpace = 0.04f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.3f; // x2 dataset
        // (0.3 + 0.02) * 3 + 0.04 = 1.00 -> interval per "group"

        int startYear = 1; //1980
        int endYear = 4; //1985


        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals3 = new ArrayList<BarEntry>();

        for (int i = startYear; i < endYear; i++) {
           yVals1.add(new BarEntry(i, 0.4f,"test"));
        }

        for (int i = startYear; i < endYear; i++) {
            yVals2.add(new BarEntry(i, 0.7f, "test1"));

        }

        for (int i = startYear; i < endYear; i++) {
            yVals3.add(new BarEntry(i, 1.2f,"test3"));

        }


        BarDataSet set1, set2, set3;

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet)barChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet)barChart.getData().getDataSetByIndex(1);
            set3 = (BarDataSet)barChart.getData().getDataSetByIndex(2);
            set1.setValues(yVals1);
            set2.setValues(yVals2);
            set3.setValues(yVals3);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            // create 3 datasets with different types
            set1 = new BarDataSet(yVals1, "Company A");
            set1.setColor(ColorTemplate.JOYFUL_COLORS[0]);
            set2 = new BarDataSet(yVals2, "Company B");
            set2.setColor(ColorTemplate.JOYFUL_COLORS[1]);
            set3 = new BarDataSet(yVals3, "Company C");
            set3.setColor(ColorTemplate.JOYFUL_COLORS[2]);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            dataSets.add(set2);
            dataSets.add(set3);

            BarData data = new BarData(dataSets);

            //int numbGroupes=5; // not 3 because there is a strange error which didnt display the last 1.5 groupes
            //barChart.getXAxis().setAxisMinimum(-data.getBarWidth()/2);
            //barChart.getXAxis().setAxisMaximum(numbGroupes-data.getBarWidth()/2);

            barChart.setData(data);
        }

        barChart.getBarData().setBarWidth(barWidth);
        barChart.getXAxis().setAxisMinValue(startYear);
        barChart.getXAxis().setAxisMaximum(endYear);

        barChart.groupBars(startYear, groupSpace, barSpace);
        barChart.animateY(3000);
        barChart.invalidate();


    }

    private void createPieChart(){
        headerText.setText(getString(R.string.piechart_header_available));

        //PieChart chart=home.getPieChart();
        // pieChart.setNoDataText("");

        ArrayList<PieEntry> entries = new ArrayList<>();


        //final String [] mLabels = new String[3];
        //int u=0;
        for(int i=topicArrayList.size()-3;i<topicArrayList.size();i++){
            entries.add(new PieEntry((float)topicArrayList.get(i).getInfluence(), topicArrayList.get(i).getTopic()));
            //mLabels[u]=topicArrayList.get(i).getDomain();
            //u++;
            //labels.add(topicArrayList.get(i).getDomain());
            //numbCalls++;
        }

        PieDataSet dataset = new PieDataSet(entries, "Amount of influence");
        dataset.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(dataset); //labels
        data.setValueFormatter(new PercentFormatter());
        // dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.getLegend().setWordWrapEnabled(true);

        /*chart.getXAxis().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);*/
        Description description = new Description();
        description.setText("Philipp´s pie chart");
        pieChart.setDescription(description);
        pieChart.animateY(5000);

    }

    //Test
    private void createTestPieChart(){

        //PieChart chart=home.getPieChart();
        // pieChart.setNoDataText("");

        ArrayList<PieEntry> entries = new ArrayList<>();


        //final String [] mLabels = new String[3];
        //int u=0;
        for(int i=0;i<3;i++){
            entries.add(new PieEntry((float)i+3, "Test "+i));
            //mLabels[u]=topicArrayList.get(i).getDomain();
            //u++;
            //labels.add(topicArrayList.get(i).getDomain());
            //numbCalls++;
        }

        PieDataSet dataset = new PieDataSet(entries, "Amount of influence");
        dataset.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(dataset); //labels
        data.setValueFormatter(new PercentFormatter());
        // dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.getLegend().setWordWrapEnabled(true);

        /*chart.getXAxis().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);*/
        Description description = new Description();
        description.setText("Philipp´s pie chart");
        pieChart.setDescription(description);
        pieChart.animateY(5000);

    }
}
