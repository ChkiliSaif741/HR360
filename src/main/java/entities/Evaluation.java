package entities;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class Evaluation {
    private int idEvaluation;
        private float noteTechnique;
    private float noteSoftSkills;
    private String commentaire;
    private LocalDateTime dateEvaluation;
    private int idEntretien;
    private int id;

    public Evaluation() {}

    public Evaluation(float noteTechnique, float noteSoftSkills, String commentaire, LocalDateTime dateEvaluation, int idEntretien, int id ) {
        this.noteTechnique = noteTechnique;
        this.noteSoftSkills = noteSoftSkills;
        this.commentaire = commentaire;
        this.dateEvaluation = dateEvaluation;
        this.idEntretien = idEntretien;
        this.id = id;
    }
    public Evaluation(int idEvaluation,float noteTechnique, float noteSoftSkills, String commentaire, LocalDateTime dateEvaluation, int idEntretien, int id ) {
        this.idEvaluation = idEvaluation;
        this.noteTechnique = noteTechnique;
        this.noteSoftSkills = noteSoftSkills;
        this.commentaire = commentaire;
        this.dateEvaluation = dateEvaluation;
        this.idEntretien = idEntretien;
        this.id = id;
    }



    public int getIdEvaluation() {
        return idEvaluation;
    }
    public void setIdEvaluation(int idEvaluation) {
        this.idEvaluation = idEvaluation;
    }
    public float getNoteTechnique() {
        return noteTechnique;
    }
    public void setNoteTechnique(float noteTechnique) {
        this.noteTechnique = noteTechnique;
    }
    public float getNoteSoftSkills() {
        return noteSoftSkills;
    }
    public void setNoteSoftSkills(float noteSoftSkills) {
        this.noteSoftSkills = noteSoftSkills;
    }
    public String getCommentaire() {
        return commentaire;
    }
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    public LocalDateTime getDateEvaluation() {
        return dateEvaluation;
    }
    public void setDateEvaluation(LocalDateTime dateEvaluation) {
        this.dateEvaluation = dateEvaluation;
    }
    public int getIdEntretien() {
        return idEntretien;
    }
    public void setIdEntretien(int idEntretien) {
        this.idEntretien = idEntretien;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Evaluation{" +
                "idEvaluation=" + idEvaluation +
                ", noteTechnique=" + noteTechnique +
                ", noteSoftSkills=" + noteSoftSkills +
                ", commentaire='" + commentaire + '\'' +
                ", dateEvaluation=" + dateEvaluation +
                ", identretien=" + idEntretien +
                ", id=" + id +
                '}';
    }


}
