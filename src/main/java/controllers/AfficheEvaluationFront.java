package controllers;

import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import entities.Evaluation;
import javafx.stage.Stage;
import services.ServiceEvaluation;
import utils.MyDatabase;

import java.io.IOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.ImageView;


import java.net.URL;
import java.util.ResourceBundle;

public class AfficheEvaluationFront implements Initializable {
    @javafx.fxml.FXML
    private ScrollPane scrollEva;
    @javafx.fxml.FXML
    private GridPane gridEva;
    private int idEntretien;
    private AfficheEvaluationFront afficheEvaluationFront;


    private Connection conn;
    private List<Evaluation> evaluations = new ArrayList<>();
    @FXML
    private ImageView btnAddEvaluation;
    // ModifierEvaluation afficheEvaluation;

    public AfficheEvaluationFront() {
        this.conn = MyDatabase.getInstance().getConnection();
    }
    /*public void setAfficheEvaluation(ModifierEvaluation modifierEvaluation) {
        this.afficheEvaluation = modifierEvaluation;
    }*/





    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("initialize() appelé");
        //afficheEvaluation();

    }


    private void afficheEvaluation() {
        gridEva.getChildren().clear(); // Vider le GridPane avant de recharger les tests

        ServiceEvaluation serviceEva = new ServiceEvaluation();
        System.out.println(idEntretien+"Evaluation");

        try {


            evaluations.clear();
            evaluations.addAll(serviceEva.afficher().stream().peek(e-> System.out.println(e.getIdEntretien())).filter(e->e.getIdEntretien()==idEntretien).toList());
            System.out.println(evaluations);

            int column = 0;
            int row = 0;

            for (Evaluation eva : evaluations) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ItemEvaluationFront.fxml"));
                AnchorPane anchorPane = loader.load();

                ItemEvaluationFront itemEvaluationFront = loader.getController();
                itemEvaluationFront.setData(eva);
                itemEvaluationFront.setEvaluationFront(this);
                if (column == 3) {
                    column = 0;
                    row++;
                }
                // Assurez-vous qu'il n'y a qu'un seul élément à afficher
                if (evaluations.size() == 1) {
                    column = 1; // Positionner l'élément au centre (dans la colonne du milieu)
                    row = 2;
                }

                gridEva.add(anchorPane, column++, row);


                gridEva.setMinWidth(Region.USE_COMPUTED_SIZE);
                gridEva.setMinHeight(Region.USE_COMPUTED_SIZE);
                gridEva.setPrefWidth(Region.USE_COMPUTED_SIZE);
                gridEva.setPrefHeight(Region.USE_COMPUTED_SIZE);
                gridEva.setMaxWidth(Region.USE_PREF_SIZE);
                gridEva.setMaxHeight(Region.USE_PREF_SIZE);



                //GridPane.setMargin(anchorPane, new Insets(20));

            }
        } catch (IOException e) { // Supprimer SQLException car non levée ici
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Méthode pour rafraîchir la vue
    public void refresh() {
        evaluations.clear(); // Vider la liste des tests
        afficheEvaluation(); // Recharger les tests
    }

    public void setIdEntretien(int idEntretien) {
        this.idEntretien = idEntretien;
        afficheEvaluation();
    }



}
