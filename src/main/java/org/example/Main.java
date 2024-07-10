package org.example;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;



import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {

        // les declaration des variables
        int n = 5;
         Double[] means = {1.5, 2.2, 6.5, 0.15, 0.9};
       Double[] variances = {2.5, 1.5, 14.0, 0.012, 0.6};

        GammaMatrix obj = new GammaMatrix();

// -----------------------------------------premier matrice de nk= 1500-----------------------------------------------
        int nk1 = 1500;
        LinkedList<Double>[][] matrice1 = new LinkedList[n][n];


        // Initialisation de chaque cellule avec une LinkedList
        matrice1 = obj.generateGammaMatrix(matrice1, means, variances, n, nk1);
        System.out.println("--------------------affichage de matrice de  nk="+nk1+"--------------------------------------------------------------------------------------------------------------------------------------------");
        // Affichage de la matrice
        obj.afficherMatrice(matrice1, n);

        // -----------------------------------------2emmmmme matrice de nk= 5000-----------------------------------------------
        int nk2 = 5000;
        LinkedList<Double>[][] matrice2 = new LinkedList[n][n];


        // Initialisation de chaque cellule avec une LinkedList
        matrice2 = obj.generateGammaMatrix(matrice2, means, variances, n, nk2);
        System.out.println("--------------------affichage de matrice de nk="+nk2+"----------------------------------------------------------------------------------------------------------------------------------------------");
        // Affichage de la matrice
        obj.afficherMatrice(matrice2, n);


        // -----------------------------------------3emmmme matrice de nk= 1500-----------------------------------------------
        int nk3 = 10000;
        LinkedList<Double>[][] matrice3 = new LinkedList[n][n];


        // Initialisation de chaque cellule avec une LinkedList
        matrice3 = obj.generateGammaMatrix(matrice3, means, variances, n, nk3);
        System.out.println("--------------------affichage de matrice de nk="+nk3+"---------------------------------------------------------------------------------------------------------------------------------------------");
        // Affichage de la matrice
        obj.afficherMatrice(matrice3, n);


        // Appeler la méthode d'échauffement de la JVM

        GammaMatrix.warmUpJVM();

        // la mesure des temps d'exécution pour matrice 1-------------------------------------------------------------------------------------
        double[][] avgTimePerOperation1 = new double[n][n];  // Pour stocker le temps moyen par opération
        double[][] avgTime1 = GammaMatrix.measureExecutionTime(matrice1,avgTimePerOperation1, means, variances, n, nk1);
        // la mesure des temps d'exécution pour matrice 2-------------------------------------------------------------------------------------
        double[][] avgTimePerOperation2 = new double[n][n];  // Pour stocker le temps moyen par opération

        double[][] avgTime2 = GammaMatrix.measureExecutionTime(matrice2,avgTimePerOperation2, means, variances, n, nk2);

        // la mesure des temps d'exécution pour matrice 3-------------------------------------------------------------------------------------
        double[][] avgTimePerOperation3 = new double[n][n];  // Pour stocker le temps moyen par opération

        double[][] avgTime3 = GammaMatrix.measureExecutionTime(matrice3,avgTimePerOperation3, means, variances, n, nk3);

        // Calculer les cas de complexité------------------------------------------------------------------------------------------------


        GammaMatrix.compareWithTheoreticalComplexities(avgTime1,avgTimePerOperation1, means, variances, n, nk1);

        GammaMatrix.compareWithTheoreticalComplexities(avgTime2,avgTimePerOperation2, means, variances, n, nk2);

        GammaMatrix.compareWithTheoreticalComplexities(avgTime3,avgTimePerOperation3, means, variances, n, nk3);

        //. Data storages and organization ---------------------------------------------------------------------------------------

        // Répertoire parent du répertoire du projet
        String projectParentDir = Paths.get(System.getProperty("user.dir")).getParent().toString();

        // Chemins dynamiques pour les répertoires de stockage
        Path baseDirectory1 = Paths.get(projectParentDir, "SortedData", "matrice1");
        Path baseDirectory2 = Paths.get(projectParentDir, "SortedData", "matrice2");
        Path baseDirectory3 = Paths.get(projectParentDir, "SortedData", "matrice3");

        // Création des répertoires s'ils n'existent pas
        try {
            Files.createDirectories(baseDirectory1);
            Files.createDirectories(baseDirectory2);
            Files.createDirectories(baseDirectory3);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Stockage des Résultats dans les Fichiers
        try {
            GammaMatrix.writeCellDataToCSV(matrice1, avgTime1, avgTimePerOperation1, means, variances, nk1, baseDirectory1.toString());
            GammaMatrix.writeCellDataToCSV(matrice2, avgTime2, avgTimePerOperation2, means, variances, nk2, baseDirectory2.toString());
            GammaMatrix.writeCellDataToCSV(matrice3, avgTime3, avgTimePerOperation3, means, variances, nk3, baseDirectory3.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Data Visualization and Analysis-----------------------------------------------------------------------------------------


        SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("Graphiques des Temps d'exécution");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());

                JTabbedPane tabbedPane = new JTabbedPane();



                tabbedPane.add("nk="+nk1, GammaMatrix.createTabPanel(means, variances, avgTime1,avgTimePerOperation1, nk1));
                tabbedPane.add("nk="+nk2, GammaMatrix.createTabPanel(means, variances, avgTime2,avgTimePerOperation2, nk2));
                tabbedPane.add("nk="+nk3, GammaMatrix.createTabPanel(means, variances, avgTime3,avgTimePerOperation3, nk3));

                frame.add(tabbedPane, BorderLayout.CENTER);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        }


    }








