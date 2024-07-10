package org.example;

import org.apache.commons.math3.distribution.GammaDistribution;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.Layer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.lang.System;

public class GammaMatrix {



    // Initialisation de chaque cellule avec une LinkedList ----------------------------------------------------------------------------------------------

    public   LinkedList<Double>[][] generateGammaMatrix( LinkedList<Double>[][] matrice,Double[] means, Double[] variances,int n, int nk) {
        Double mean;
       Double variance;
        for (int i = 0; i < n; i++) {
            mean = means[i];
            for (int j = 0; j < n; j++) {
                variance=variances[j];
                matrice[i][j] = new LinkedList<>();

                // Calculer les paramètres shape et scale
                double shape = Math.pow(mean, 2) / variance;
                double scale = mean / variance;

                for(int k=0;k<nk;k++){

                    // Distribution gamma
                    GammaDistribution gammaDistribution = new GammaDistribution(shape, scale);

                    // Génération d'un nombre aléatoire selon la distribution gamma
                    double randomValue = gammaDistribution.sample();

                    matrice[i][j].add(randomValue);

                }
            }
        }
        return matrice;

    }

    // Affichage de la matrice ----------------------------------------------------------------------------------------------

   public void afficherMatrice(LinkedList<Double>[][] matrice,int n){


        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print("Cellule [" + i + "][" + j + "]: ");
                for (Double nombre : matrice[i][j]) {
                    System.out.print(nombre + " ");
                }
                System.out.println();
            }
        }


    }

    //L'étape de "Warm Up JVM"----------------------------------------------------------------------------------------------


    public static void warmUpJVM() {
        System.out.println("Début de l'échauffement de la JVM...");

        // Paramètres de la distribution Gamma
        double shape = 2.0;
        double scale = 1.0;

        // Création de l'objet GammaDistribution
        GammaDistribution gammaDistribution = new GammaDistribution(shape, scale);

        // Répéter le tri pour échauffer la JVM
        for (int i = 0; i < 2000; i++) {
            Double[] arr = new Double[5000];
            for (int j = 0; j < arr.length; j++) {
                // Générer un nombre suivant une distribution Gamma
                arr[j] = gammaDistribution.sample();
            }

            selectionSort(arr);

        }
        System.out.println("Échauffement de la JVM terminé.");

    }
    // Méthode pour trier un tableau -------------------------------------------------------------------------------------
    public static void selectionSort(Double[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            // Trouver l'index du minimum
            int min = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[min]) {
                    min = j;
                }
            }
            // Échanger l'élément courant avec le minimum trouvé
            Double temp = arr[min];
            arr[min] = arr[i];
            arr[i] = temp;
        }
    }

    // la mesure des temps d'exécution -------------------------------------------------------------------------------------


    public static double[][] measureExecutionTime(LinkedList<Double>[][] matrice,  double[][] TimePerOperation , Double[] means, Double[] variances, int n, int nk) {
        long totalTime;
        int repetitions = 20;  // Nombre de répétitions pour chaque combinaison
        double[][] time = new double[n][n];

        double totalTheoreticalTime = 0;  // Pour stocker le temps théorique total pour toute la matrice

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                totalTime = 0;  // Réinitialisation du temps total pour chaque combinaison moyenne/variance

                for (int k = 0; k < repetitions; k++) {
                    Double[] tab = matrice[i][j].toArray(new Double[0]);  // Réinitialiser et récupérer les données à chaque répétition

                    long startTime = System.nanoTime();  // Démarrer le chronomètre
                    GammaMatrix.selectionSort(tab);  // Tri du tableau
                    long endTime = System.nanoTime();  // Arrêter le chronomètre

                    totalTime += (endTime - startTime);  // Accumuler le temps d'exécution
                }

                long averageTime =  totalTime / repetitions;  // Calculer la moyenne du temps d'exécution
                double averageTimeInMs = averageTime / 1_000_000.0;  // Convertir en millisecondes
                int totalComparisons = (nk * (nk - 1)) / 2;  // Calcul du nombre total de comparaisons pour le tri par sélection
                double TimePerOp = averageTimeInMs / totalComparisons;  // Calculer le temps moyen par opération
                double theoreticalTime = TimePerOp * totalComparisons;  // Temps d'exécution théorique pour la cellule

                System.out.println("Moyenne du temps d'exécution pour nk=" + nk + " Cellule [" + i + "][" + j + "], moyenne=" + means[i] + ", variance=" + variances[j] + " : " + averageTimeInMs + " ms, " + TimePerOp + " ms par opération");

                time[i][j] =  averageTimeInMs;
                TimePerOperation[i][j] = TimePerOp;
                totalTheoreticalTime += theoreticalTime;  // Accumuler le temps théorique total de la matrice
            }
        }

        System.out.println("---------------> Total theoretical execution time for the entire matrix: " + totalTheoreticalTime + " ms");
        return time;
    }




    // Calculer les cas de complexité------------------------------------------------------------------------------------------------

    public static void compareWithTheoreticalComplexities(double[][] avgTime, double[][] TimePerOperation,Double[] means, Double[] variances, int n, int nk){
        System.out.println("-------------Comparaison avec les Complexités Théoriques pour nk=" + nk + "--------------------------------------------------------------------------------------------------------------------------------");

        // Calculer la complexité théorique pour


        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(" Comparaison de Complexités Cellule [" + i + "][" + j + "],  moyenne=" + means[i] + ", variance=" + variances[j] + " est  : ");
                double theoreticalAverageComplexity = (Math.pow(nk, 2))*TimePerOperation[i][j];
                // Mesurer le temps d'exécution réel
                double realExecutionTime = avgTime[i][j];
                // Comparer le temps d'exécution réel avec la complexité théorique
                if (realExecutionTime < theoreticalAverageComplexity) {
                    System.out.println("Le temps d'exécution réel est meilleur que la complexité théorique moyenne.");
                } else if (realExecutionTime == theoreticalAverageComplexity) {
                    System.out.println("Le temps d'exécution réel correspond à la complexité théorique moyenne.");
                } else {
                    System.out.println("Le temps d'exécution réel est pire que la complexité théorique moyenne.");
                }

            }
        }
    }

    // Data storages and organization ---------------------------------------------------------------------------------------

    public static void writeCellDataToCSV(LinkedList<Double>[][] matrix, double[][] avgTime, double[][] TimePerOperation,Double[] means, Double[] variances, int nk, String baseDirectory) throws IOException {
        // Créer le répertoire de base s'il n'existe pas
        File baseDir = new File(baseDirectory);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        // Formatteur pour la date et l'heure
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = LocalDateTime.now().format(dtf);

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                String fileName = String.format("results_nk_%d_mean_%f_variance_%f.csv", nk, means[i], variances[j]);
                File file = new File(baseDir, fileName);
                try (FileWriter writer = new FileWriter(file)) {
                    // Écriture des métadonnées
                    writer.append("Metadata\n");
                    writer.append("Timestamp,").append(timestamp).append("\n");
                    writer.append("Distribution,Gamma\n");
                    writer.append("Sample Size,").append(Integer.toString(nk)).append("\n");
                    writer.append("Mean,").append(Double.toString(means[i])).append("\n");
                    writer.append("Variance,").append(Double.toString(variances[j])).append("\n");
                    writer.append("Real Execution Time,").append(Double.toString(avgTime[i][j])).append("ms \n");


                    String comparisonResult;
                    double theoreticalAverageComplexity = (Math.pow(nk, 2))*TimePerOperation[i][j];
                    double realExecutionTime = avgTime[i][j];
                    if (realExecutionTime < theoreticalAverageComplexity) {
                        comparisonResult = "The real execution time is better than the  theoretical complexity.";
                    } else if (realExecutionTime == theoreticalAverageComplexity) {
                        comparisonResult = "The real execution time matches the  theoretical complexity.";
                    } else {
                        comparisonResult = "The real execution time is worse than the  theoretical complexity.";
                    }
                    writer.append("Comparison of Complexity,").append(comparisonResult).append("\n\n");

                    writer.append("Data\n");
                    writer.append("Value\n");

                    Double[] tab = matrix[i][j].toArray(new Double[0]); // Réinitialiser et récupérer les données à chaque répétition
                    GammaMatrix.selectionSort(tab); // Tri du tableau

                    // Écriture des données de la cellule
                    for (Double value : tab) {
                        writer.append(value.toString()).append("\n");
                    }
                }
            }
        }

    }

// Data Visualization and Analysis-----------------------------------------------------------------------------------------

    public static JPanel createTabPanel(Double[] means, Double[] variances, double[][] avgTime,double[][] TimePerOperation, int nk) {
        JPanel panel = new JPanel(new GridLayout(1, 2));

        XYSeriesCollection datasetMeans = new XYSeriesCollection();
        XYSeriesCollection datasetVariances = new XYSeriesCollection();

        for (int i = 0; i < means.length; i++) {
            XYSeries seriesMean = new XYSeries("Moyenne " + means[i]);
            XYSeries seriesVariance = new XYSeries("Variance " + variances[i]);
            for (int j = 0; j < variances.length; j++) {
                seriesMean.add((double) variances[j], avgTime[i][j]);
                seriesVariance.add((double) means[j], avgTime[j][i]);
            }
            datasetMeans.addSeries(seriesMean);
            datasetVariances.addSeries(seriesVariance);
        }

        JFreeChart chartMeans = ChartFactory.createXYLineChart("Temps d'exécution vs Variance", "Variance", "Temps d'exécution (ms)", datasetMeans, PlotOrientation.VERTICAL, true, true, false);
        JFreeChart chartVariances = ChartFactory.createXYLineChart("Temps d'exécution vs Moyenne", "Moyenne", "Temps d'exécution (ms)", datasetVariances, PlotOrientation.VERTICAL, true, true, false);

        double totalOperationTime = 0;
        int count = 0;

        for (int i = 0; i < TimePerOperation.length; i++) {
            for (int j = 0; j < TimePerOperation[i].length; j++) {
                totalOperationTime += TimePerOperation[i][j];
                count++;
            }
        }


        double averageTimePerOperation = totalOperationTime / count;


        long theoreticalComplexity = (long) ((long) (nk * nk)*averageTimePerOperation); // Calcul de la complexité théorique
        System.out.println("********* le averageTimePerOperation de matrice de size "+nk+" est : "+averageTimePerOperation );
       System.out.println("********* le theoreticalComplexity est : "+theoreticalComplexity );

        customizeChart(chartMeans, theoreticalComplexity);
        customizeChart(chartVariances, theoreticalComplexity);

        panel.add(new ChartPanel(chartMeans));
        panel.add(new ChartPanel(chartVariances));

        return panel;
    }

    private static void customizeChart(JFreeChart chart, long theoreticalComplexity) {
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // Utiliser des couleurs  pour les séries
        Color[] seriesColors = {
                Color.BLUE,
                Color.cyan,
                Color.BLACK,
                Color.yellow,
                Color.white
        };

        for (int i = 0; i < plot.getSeriesCount(); i++) {
            renderer.setSeriesPaint(i, seriesColors[i % seriesColors.length]);
            renderer.setSeriesShapesVisible(i, true);
        }

        plot.setRenderer(renderer);

        ValueMarker complexityMarker = new ValueMarker(theoreticalComplexity);
        complexityMarker.setPaint(Color.RED);
        complexityMarker.setStroke(new BasicStroke(2.0f));
        plot.addRangeMarker(complexityMarker, Layer.FOREGROUND);
    }

}
