package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KakaoMessageResponse {

    @JsonProperty("result_code")
    private int resultCode;

    @JsonProperty("template_id")
    private long templateId;

    @JsonProperty("template_args")
    private Object templateArgs;

    public int getResultCode() {
        return resultCode;
    }

    public long getTemplateId() {
        return templateId;
    }

    public Object getTemplateArgs() {
        return templateArgs;
    }

    @Override
    public String toString() {
        return "KakaoMessageResponse{" +
                "resultCode=" + resultCode +
                ", templateId=" + templateId +
                ", templateArgs=" + templateArgs +
                '}';
    }
}

