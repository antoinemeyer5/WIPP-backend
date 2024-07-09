package gov.nist.itl.ssd.wipp.backend.data.modelcard.tensorflow;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Tensorflow
{
    /***************** ATTRIBUTE(S) *****************/
    private ModelDetails modelDetails;
    /*public class modelParameters {
        private String model_architecture;
        private String input_format;
        private String input_format_map;
        private String output_format;
        private String output_format_map;
        private String data;
    }
    public class considerations {
        private String users;
        private String use_cases;
        private String limitations;
        private String tradeoffs;
        private String ethical_considerations;
        public void setUsers(String users) { this.users = users; }
    }
    public class quantitativeAnalysis {
        private String performance_metrics;
        private String graphics;
    }*/

    /***************** CONSTRUCTOR(S) *****************/
    public Tensorflow() { }

    /***************** METHOD(S) *****************/
    public ModelDetails getModelDetails() { return modelDetails; }
    public void setModelDetails(ModelDetails modelDetails) { this.modelDetails = modelDetails; }
}
