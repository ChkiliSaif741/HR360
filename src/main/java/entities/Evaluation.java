package entities;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import utils.commentaire;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class Evaluation {
    private int idEvaluation;
    private String titreEva;
    private float noteTechnique;
    private float noteSoftSkills;
    private commentaire commentaire;
    private LocalDateTime dateEvaluation;
    private int idEntretien;
    private int scorequiz;
    private List<QuizQuestion> questions;

    //private int id;

    public Evaluation() {}

    public Evaluation(float noteTechnique, float noteSoftSkills, commentaire commentaire, LocalDateTime dateEvaluation, int idEntretien) {
        this.noteTechnique = noteTechnique;
        this.noteSoftSkills = noteSoftSkills;
        this.commentaire = commentaire;
        this.dateEvaluation = dateEvaluation;
        this.idEntretien = idEntretien;
        //this.id = id;
    }
    public Evaluation(int idEvaluation,float noteTechnique, float noteSoftSkills, commentaire commentaire, LocalDateTime dateEvaluation, int idEntretien) {
        this.idEvaluation = idEvaluation;
        this.noteTechnique = noteTechnique;
        this.noteSoftSkills = noteSoftSkills;
        this.commentaire = commentaire;
        this.dateEvaluation = dateEvaluation;
        this.idEntretien = idEntretien;
        //this.id = id;
    }

    public Evaluation(String titreEva,float noteTechnique, float noteSoftSkills, commentaire commentaire, LocalDateTime dateEvaluation, int idEntretien) {
        this.titreEva = titreEva;
        this.noteTechnique = noteTechnique;
        this.noteSoftSkills = noteSoftSkills;
        this.commentaire = commentaire;
        this.dateEvaluation = dateEvaluation;
        this.idEntretien = idEntretien;
        //this.id = id;
    }



    public Evaluation(int idEvaluation,String titreEva,float noteTechnique, float noteSoftSkills, commentaire commentaire, LocalDateTime dateEvaluation, int idEntretien) {
        this.idEvaluation = idEvaluation;
        this.titreEva = titreEva;
        this.noteTechnique = noteTechnique;
        this.noteSoftSkills = noteSoftSkills;
        this.commentaire = commentaire;
        this.dateEvaluation = dateEvaluation;
        this.idEntretien = idEntretien;
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
    public commentaire getCommentaire() {
        return commentaire;
    }
    public void setCommentaire(commentaire commentaire) {
        this.commentaire = commentaire;
    }
    public LocalDateTime getDateEvaluation() {
        return dateEvaluation;
    }
    public void setDateEvaluation(LocalDateTime dateEvaluation) {
        this.dateEvaluation = dateEvaluation;
    }
    public int getIdEntretien() {return idEntretien;}
    public void setIdEntretien(int idEntretien) {
        this.idEntretien = idEntretien;
    }
    public List<QuizQuestion> getQuestions() {
        return questions;
    }
    public void setQuestions(List<QuizQuestion> questions) {
        this.questions = questions;
    }
    public int getScorequiz() {return scorequiz;}

    public void setScorequiz(int scorequiz) {this.scorequiz = scorequiz;}

    public String getTitreEva() {return titreEva;}

    public void setTitreEva(String titreEva) {this.titreEva = titreEva;}
    //public int getId() {return id;}
    //public void setId(int id) {this.id = id;}



    @Override
    public String toString() {
        return "Evaluation{" +
                "idEvaluation=" + idEvaluation +
                ", titreEva='" + titreEva + '\'' +
                ", noteTechnique=" + noteTechnique +
                ", noteSoftSkills=" + noteSoftSkills +
                ", commentaire='" + commentaire + '\'' +
                ", dateEvaluation=" + dateEvaluation +
                ", identretien=" + idEntretien +
                ", scorequiz=" + scorequiz +
                //", id=" + id +
                '}';
    }


}
