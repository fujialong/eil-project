package com.shencai.eil.survey.constants;

/**
 * Created by fanhj on 2018/10/5.
 */
public enum DefaultValueMapping {
    M6("M6", new String[]{"raw_material"}),
    M11("M11", new String[]{"production"}),
    M38("M38", new String[]{"other_effluent_intensity"}),
    M41("M41", new String[]{"other_emission_intensity"}),
    M45("M45", new String[]{"emission_intensity", "effluent_intensity"});

    private String code;
    private String[] templates;

    DefaultValueMapping(String code, String[] templates) {
        this.code = code;
        this.templates = templates;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String[] getTemplates() {
        return templates;
    }

    public void setTemplates(String[] templates) {
        this.templates = templates;
    }

    public static String[] getTemplatesByCode(String code) {
        for(DefaultValueMapping enumItem : DefaultValueMapping.values()){
            if(enumItem.getCode().equals(code)){
                return enumItem.getTemplates();
            }
        }
        return null;
    }
}
