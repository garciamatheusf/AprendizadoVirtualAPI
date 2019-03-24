package util;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class JsonValidation {

    public static class RequiredJsonFieldsNotFilledException extends Exception{
        public final List<String> fieldsNotFilledList;
        public RequiredJsonFieldsNotFilledException(List<String> fieldsNotFilledList) {
            this.fieldsNotFilledList = fieldsNotFilledList;
        }
    }

    public static void validateRequiredFieldsFilled(JsonNode jsonNode, String... fields) throws RequiredJsonFieldsNotFilledException {
        validateRequiredFieldsFilled(jsonNode, false, fields);
    }

    public static void validateRequiredFieldsFilled(JsonNode jsonNode, boolean verifyIsBlank, String... fields) throws RequiredJsonFieldsNotFilledException {
        List<String> fieldsNotFilledList = new ArrayList<>();
        for (String field : fields) {
            if (!jsonNode.has(field)){
                fieldsNotFilledList.add(field);

            } else if (verifyIsBlank){
                if (StringUtils.isBlank(jsonNode.get(field).asText())){
                    fieldsNotFilledList.add(field);
                }
            }
        }

        if (!fieldsNotFilledList.isEmpty()){
            throw new RequiredJsonFieldsNotFilledException(fieldsNotFilledList);
        }
    }
}
