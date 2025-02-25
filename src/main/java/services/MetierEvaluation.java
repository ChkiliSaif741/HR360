package services;


import entities.Evaluation;
import services.ServiceEvaluation;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class MetierEvaluation {
    private ServiceEvaluation serviceEvaluation;

    public MetierEvaluation() {
        this.serviceEvaluation = new ServiceEvaluation();
    }

    // Calculer le score total d'une évaluation
    public float calculerScoreTotal(Evaluation evaluation) {
        return evaluation.getNoteTechnique() + evaluation.getNoteSoftSkills();
    }

    // Récupérer la moyenne des notes techniques
    public float calculerMoyenneTechnique() throws SQLException {
        List<Evaluation> evaluations = serviceEvaluation.afficher();
        if (evaluations.isEmpty()) return 0;
        float somme = (float) evaluations.stream().mapToDouble(Evaluation::getNoteTechnique).sum();
        return somme / evaluations.size();
    }

    // Récupérer la moyenne des notes soft skills
    public float calculerMoyenneSoftSkills() throws SQLException {
        List<Evaluation> evaluations = serviceEvaluation.afficher();
        if (evaluations.isEmpty()) return 0;
        float somme = (float) evaluations.stream().mapToDouble(Evaluation::getNoteSoftSkills).sum();
        return somme / evaluations.size();
    }

    // Trouver les meilleures évaluations en fonction d'un seuil
    public List<Evaluation> getMeilleuresEvaluations(float seuil) throws SQLException {
        return serviceEvaluation.afficher().stream()
                .filter(e -> calculerScoreTotal(e) >= seuil)
                .collect(Collectors.toList());
    }

    // Trier les évaluations par score décroissant
    public List<Evaluation> trierEvaluationsParScore() throws SQLException {
        return serviceEvaluation.afficher().stream()
                .sorted((e1, e2) -> Float.compare(calculerScoreTotal(e2), calculerScoreTotal(e1)))
                .collect(Collectors.toList());
    }
}
