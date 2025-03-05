package entities;

public class GrammarCheckResponse {
    private Errors errors;

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    public static class Errors {
        private String correction;
        private String error;

        public String getCorrection() {
            return correction;
        }

        public void setCorrection(String correction) {
            this.correction = correction;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}