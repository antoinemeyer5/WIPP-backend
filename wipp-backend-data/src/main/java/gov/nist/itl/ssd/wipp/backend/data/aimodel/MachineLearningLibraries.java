package gov.nist.itl.ssd.wipp.backend.data.aimodel;

public enum MachineLearningLibraries {
    TENSORFLOW("TensorFlow"),
    PYTORCH("PyTorch");

    public final String label;

    private MachineLearningLibraries(String label) {
        this.label = label;
    }
}
