    package entities;

    import java.util.ArrayList;
    import java.util.List;

    public class Formation {
        private int id;
        private String titre;
        private String description;
        private int duree;
        private String dateFormation;
        private boolean isFavorite;
        List<Utilisateur> employees;

        public Formation() {
            employees = new ArrayList<>();
        }
        public Formation(int id, String titre, String description, int duree, String dateFormation) {
            this.id = id;
            this.titre = titre;
            this.description = description;
            this.duree = duree;
            this.dateFormation = dateFormation;
            this.employees = new ArrayList<>();
        }

        public Formation(String titre, String description, int duree, String dateFormation , boolean isFavorite) {
            this.titre = titre;
            this.description = description;
            this.duree = duree;
            this.dateFormation = dateFormation;
            this.isFavorite = isFavorite;
            this.employees = new ArrayList<>();
        }

        public Formation(int id ,String titre, String description, int duree, String dateFormation , boolean isFavorite) {
            this.id = id;
            this.titre = titre;
            this.description = description;
            this.duree = duree;
            this.dateFormation = dateFormation;
            this.isFavorite = isFavorite;
            this.employees = new ArrayList<>();
        }


        public Formation(String titre, String description, int duree, String dateFormation) {
            this.titre = titre;
            this.description = description;
            this.duree = duree;
            this.dateFormation = dateFormation;
            this.employees = new ArrayList<>();
        }



        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitre() {
            return titre;
        }

        public void setTitre(String titre) {
            this.titre = titre;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getDuree() {
            return duree;
        }

        public void setDuree(int duree) {
            this.duree = duree;
        }

        public String getDateFormation() {
            return dateFormation;
        }

        public void setDateFormation(String dateFormation) {
            this.dateFormation = dateFormation;
        }

        public List<Utilisateur> getEmployees() {
            return employees;
        }

        public void setEmployees(List<Utilisateur> employees) {
            this.employees = employees;
        }

        public boolean isFavorite() {
            return isFavorite;
        }

        public void setFavorite(boolean favorite) {
            isFavorite = favorite;
        }

        @Override
        public String toString() {
            return "Formation{" +
                    "id=" + id +
                    ", titre='" + titre + '\'' +
                    ", description='" + description + '\'' +
                    ", duree=" + duree +
                    ", dateFormation='" + dateFormation + '\'' +
                    '}';
        }
    }
