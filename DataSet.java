import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;
import org.jblas.*;
import org.math.plot.*;

/**
  Dataset class Author: S. Bhatnagar
  -mostly static methods which operate on the data set
 */
public class DataSet {

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // method that creates a list of dataPoints
  public static List<DataPoint> readDataSet(String file) throws FileNotFoundException {

    List<DataPoint> dataset = new ArrayList<DataPoint>();
    Scanner scanner = new Scanner(new File(file));

    String line;
    String[] columns;
    String label;

    while (scanner.hasNextLine()) {
      line = scanner.nextLine();
      columns = line.split(",");

      // feature vector will append 1 as x_0, and then take in all
      // but the last column (which is assigned as label)
      double[] X = new double[columns.length];

      X[0] = 1;
      for (int i = 1; i < columns.length; i++) {
        // check if feature is numeric
        if (isNumeric(columns[i - 1])) {
          X[i] = Double.parseDouble(columns[i - 1]);
        } else {
          // code to convert nominal X to numeric
        }
      }

      label = columns[columns.length - 1];

      // special fix of label for handwritten digits data set: label "10" switched to "0"
      if (label.equals("10")) {
        label = "0";
      }

      DataPoint dataPoint = new DataPoint(label, X);

      dataPoint.setTestOrTrain("held_out");
      dataset.add(dataPoint);
    }
    scanner.close();

    return dataset;
  }
  ///////////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // method that creates a list of dataPoints with higher order polynomial X upto to user defined
  // degree
  public static List<DataPoint> readDataSetHigherOrderFeatures(String file, int degree)
      throws FileNotFoundException {

    List<DataPoint> dataset = new ArrayList<DataPoint>();
    Scanner scanner = new Scanner(new File(file));

    String line;
    String[] columns;
    String label;

    // all dataPoints in dataset given dummy labelAsDouble, which will be changed
    // from with call to Logistic.train, based on target label
    double labelAsDouble = -1.;

    while (scanner.hasNextLine()) {
      line = scanner.nextLine();
      columns = line.split(",");

      // feature vector will append 1 as x_0, and then take in all
      // but the last column (which is assigned as label)
      double[] X = new double[columns.length];

      X[0] = 1;
      for (int i = 1; i < columns.length; i++) {
        // check if feature is numeric
        if (isNumeric(columns[i - 1])) {
          X[i] = Double.parseDouble(columns[i - 1]);
        } else {
          // code to convert nominal X to numeric
        }
      }

      label = columns[columns.length - 1];

      // add higher order X
      ArrayList<Double> higherOrderX = new ArrayList<Double>();

      for (int n = 0; n <= degree; n++) {
        for (int k = 0; k <= n; k++) {
          double xnk = Math.pow(X[1], n - k) * Math.pow(X[2], k);
          higherOrderX.add(xnk);
        }
      }

      // convert list to array
      double[] allX = new double[higherOrderX.size()];
      for (int i = 0; i < higherOrderX.size(); i++) {
        allX[i] = higherOrderX.get(i);
      }

      DataPoint dataPoint = new DataPoint(label, allX);

      dataPoint.setTestOrTrain("held_out");
      dataset.add(dataPoint);
    }
    scanner.close();

    System.out.print("Each data point now has the feature vector: ");
    for (int n = 0; n <= degree; n++) {
      for (int k = 0; k <= n; k++) {
        System.out.print(", x1^" + (n - k) + "*x2^" + k);
      }
    }
    System.out.println();

    return dataset;
  }
  ///////////////////////////////////////////////////////////////////////////////////

  ///////////////////////////////////////////////////////////////////////////////////
  // check is data entry is nominal or numeric
  public static boolean isNumeric(String str) {
    return str.matches("-?\\d+(\\.\\d+)?"); // match a number with optional '-' and decimal.
  }
  ///////////////////////////////////////////////////////////////////////////////////

  ///////////////////////////////////////////////////////////////////////////////////
  // "split off" testSet by setting testOrTrain variable for each dataPoint based on fraction input
  // by user
  public static List<DataPoint> getTestSet(List<DataPoint> fullDataSet, double fractionTestSet) {

    // Random rnd = new Random(123);
    // Collections.shuffle(fullDataSet, rnd);
    Collections.shuffle(fullDataSet);

    List<DataPoint> testSet = new ArrayList<DataPoint>();

    // shuffle dataSet and split into test and training sets by setting
    // testOrTrain variable for each dataPoint
    for (int i = 0; i < fractionTestSet * fullDataSet.size(); i++) {
      fullDataSet.get(i).setTestOrTrain("test_set");
      testSet.add(fullDataSet.get(i));
    }

    return testSet;
  }
  //////////////////////////////////////////////////////////////////////////////////////

  ///////////////////////////////////////////////////////////////////////////////////
  // "split off" trainingSet by setting testOrTrain variable for each dataPoint based on fraction
  // input by user
  public static List<DataPoint> getTrainingSet(
      List<DataPoint> fullDataSet, double fractionTrainingSet) {

    // Random rnd = new Random(123);
    // Collections.shuffle(fullDataSet);
    Collections.shuffle(fullDataSet);

    List<DataPoint> trainingSet = new ArrayList<DataPoint>();

    int count = 0;
    int i = 0;
    while (count < fractionTrainingSet * fullDataSet.size() && i < fullDataSet.size()) {
      String currentSetting = fullDataSet.get(i).getTestOrTrain();

      if (currentSetting.equals("training_set")) {
        trainingSet.add(fullDataSet.get(i));
        count++;
      } else if (!currentSetting.equals("test_set")) {
        fullDataSet.get(i).setTestOrTrain("training_set");
        trainingSet.add(fullDataSet.get(i));
        count++;
      }

      i++;
    }

    return trainingSet;
  }
  ///////////////////////////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////////////////////////////
  // count & print frequencies of different labels
  public static void printLabelFrequencies(List<DataPoint> fullDataSet) {

    HashMap<String, Integer> labelFrequencies = new HashMap<String, Integer>();

    List<String> labels = new ArrayList<String>();

    for (DataPoint i : fullDataSet) {
      labels.add(i.getLabel());
    }

    Set<String> uniqueSet = new HashSet<String>(labels);

    for (String temp : uniqueSet) {
      labelFrequencies.put(temp, Collections.frequency(labels, temp));
      System.out.println(temp + " " + Collections.frequency(labels, temp) + " dataPoints");
    }
  }
  ///////////////////////////////////////////////////////////////////////////////////////

  ////////////////////////////////////////////////////////////////////////////
  // get list (set) of unique labels
  public static Set<String> getLabels(List<DataPoint> fullDataSet) {

    List<String> labels = new ArrayList<String>();

    for (DataPoint i : fullDataSet) {
      labels.add(i.getLabel());
    }

    Set<String> uniqueSet = new HashSet<String>(labels);

    return uniqueSet;
  }
  ////////////////////////////////////////////////////////////////////////////

  ////////////////////////////////////////////////////////////////////////////
  //
  public static void printDataSet(List<DataPoint> fullDataSet) {
    for (DataPoint i : fullDataSet) {
      System.out.println(
          "X = "
              + Arrays.toString(i.getX())
              + ", label = "
              + i.getLabel()
              + ", label as vector: "
              + Arrays.toString(i.getVectorLabel().toArray()));
    }
  }
  ////////////////////////////////////////////////////////////////////////////

  ////////////////////////////////////////////////////////////////////////////
  // convert string labels into binary vector labels
  public static HashMap<String, DoubleMatrix> setVectorizedLabels(List<DataPoint> dataSet) {

    // scan through dataSet and make a list of unique labels
    List<String> labels = new ArrayList<String>();

    for (DataPoint i : dataSet) {
      labels.add(i.getLabel());
    }
    Set<String> uniqueSet = new HashSet<String>(labels);

    // make a HashMap which assigns a binary row vector to each possible class Label
    HashMap<String, DoubleMatrix> labelVectors = new HashMap<String, DoubleMatrix>();

    DoubleMatrix labelMatrix = DoubleMatrix.eye(uniqueSet.size());

    int i = 0;
    for (String temp : uniqueSet) {
      labelVectors.put(temp, labelMatrix.getRow(i));
      i++;
    }

    // scan through dataSet and based on the class label, assign correct binary vector
    for (DataPoint dp : dataSet) {
      String currentLabel = dp.getLabel();
      dp.setVectorLabel((DoubleMatrix) labelVectors.get(currentLabel));
    }
    return labelVectors;
  }
  ///////////////////////////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////////////////
  //
  public static DoubleMatrix convertXToMatrixX(List<DataPoint> data) {
    int numDataPoints = data.size();
    int numX = data.get(0).getX().length;

    DoubleMatrix X = new DoubleMatrix(numDataPoints, numX);

    // create Design matrix X
    for (int i = 0; i < numDataPoints; i++) {
      DataPoint currentDataPoint = data.get(i);
      double[] currentDataPointX = currentDataPoint.getX();

      for (int j = 0; j < numX; j++) {
        X.put(i, j, currentDataPointX[j]);
      }
    }
    return X;
  }
  /////////////////////////////////////////////////////////////////////////

  ////////////////////////////////////////////////////////////////////////////
  //
  public static void drawHandwrittenDigits(List<DataPoint> testSet, int numDigitsToPrint) {

    int numRows = (int) Math.ceil(Math.sqrt(numDigitsToPrint));
    int numColumns = (int) numDigitsToPrint / numRows;

    DoubleMatrix X = convertXToMatrixX(testSet);

    // omit first column, which represents the intercept which was added on
    DoubleMatrix XdigitsDrawn = X.getRange(0, numDigitsToPrint, 1, X.columns);
    ;

    JFrame frame =
        new JFrame("Sample of " + numDigitsToPrint + " Handwritten Digits from Test Set");
    frame.setSize(1000, 1000);

    for (int count = 0; count < numDigitsToPrint; count++) {
      DoubleMatrix XcurrentDigit = XdigitsDrawn.getRow(count);
      double[][] image = new double[20][20];

      for (int i = 0; i < 400; i++) {
        int row = (int) Math.floor((double) i / 20);
        int column = i % 20;
        image[row][column] = XcurrentDigit.get(i);
      }

      BufferedImage theImage = new BufferedImage(20, 20, BufferedImage.TYPE_BYTE_GRAY);
      for (int y = 0; y < 20; y++) {
        for (int x = 0; x < 20; x++) {
          // System.out.println(image[x][y]);
          // int value = (int)image[y][x] << 16 | (int)image[y][x] << 8 | (int)image[y][x];
          theImage.setRGB(x, y, (int) (255 * (image[x][y])));
        }
      }

      frame.getContentPane().setLayout(new GridLayout(numRows, numColumns));
      frame.getContentPane().add(new JLabel(new ImageIcon(theImage)));
    }

    frame.pack();
    frame.setVisible(true);

    System.out.println("Sample of " + numDigitsToPrint + " dataPoints and their ACTUAL classes");
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numColumns; j++) {
        if ((numRows * i + j) < testSet.size()) {
          DataPoint currentDataPoint = testSet.get(numRows * i + j);
          System.out.print(currentDataPoint.getLabel() + "  ");
        }
      }
      System.out.print("\n");
    }
  }
  ////////////////////////////////////////////////////////////////////////////

  ////////////////////////////////////////////////////////////////////////////////////////////////////////
  // plot all different classes from list of dataPoints
  public static void plot2DAllLabels(
      List<DataPoint> dataPoints, double xmin, double xmax, double ymin, double ymax)
      throws FileNotFoundException {

    // create your PlotPanel (you can use it as a JPanel)
    Plot2DPanel plot = new Plot2DPanel();
    // define the legend position
    plot.addLegend("SOUTH");

    double currentExampleX, currentExampleY;

    Set<String> labels = getLabels(dataPoints);

    for (String currentLabel : labels) {

      List<DataPoint> currentLabelDataPoints = new ArrayList<DataPoint>();

      // loop over all dataPoints
      for (DataPoint i : dataPoints) {
        if (i.getLabel().equals(currentLabel)) {
          currentLabelDataPoints.add(i);
          // System.out.println(i.getLabel());
        }
      }

      // make arrays for jmathplot
      double[] currentLabelXArray = new double[currentLabelDataPoints.size()];
      double[] currentLabelYArray = new double[currentLabelDataPoints.size()];

      for (int j = 0; j < currentLabelXArray.length; j++) {
        currentLabelXArray[j] = currentLabelDataPoints.get(j).getX()[1];
        currentLabelYArray[j] = currentLabelDataPoints.get(j).getX()[2];
        // System.out.println(currentLabelXArray[j] + "," + currentLabelYArray[j]);
      }
      // System.out.println(labels[labelIndex]);
      // add scatter plots to the PlotPanel
      plot.addScatterPlot(currentLabel, currentLabelXArray, currentLabelYArray);
    }

    plot.setFixedBounds(0, xmin, xmax);
    plot.setFixedBounds(1, ymin, ymax);

    // put the PlotPanel in a JFrame, as a JPanel
    JFrame frame = new JFrame("Data with multiple classes");
    frame.setSize(600, 600);
    frame.setContentPane(plot);
    frame.setVisible(true);
  }

  
  ////////////////////////////////////////////////////////////////////////////
  public static double distanceEuclid(DataPoint p1, DataPoint p2){
    double distanceSquared = 0;
    for (int i=0; i<p1.x.length; i++){
      distanceSquared += Math.pow((p1.x[i]-p2.x[i]),2);
    }

    double distance = Math.pow(distanceSquared,0.5);

    return distance;
  }
}
