package com.demo.test;

import org.jooq.util.DefaultGeneratorStrategy;
import org.jooq.util.Definition;
import org.jooq.util.SchemaDefinition;

public class SmartcourtGeneratorStrategy extends DefaultGeneratorStrategy {

    @Override
    public String getJavaClassName(Definition definition, Mode mode) {
        return super.getJavaClassName(definition, mode)
                .replaceAll("YunCourt", "")
                .replaceAll("Ccr", "")
                .replaceAll("TPl", "")
                .replaceAll("TGjfb", "")
                .replaceAll("TFgdq", "")
                .replaceAll("TFzzx", "")
                .replaceAll("TSfpj", "")
                .replaceAll("TCc", "")
                .replaceAll("TPay", "")
                .replaceAll("TFytt", "")
        		.replaceAll("TMrya", "")
                .replaceAll("TDzsd", "")
                .replaceAll("TOcr", "")
                .replaceAll("TWecourt", "")
                .replaceAll("TTszb", "")
                .replaceAll("TGgsd", "")
                .replaceAll("TFytt", "")
                .replaceAll("TSszy", "")
                .replaceAll("TAuth", "")
                .replaceAll("TWxuser", "")
                .replaceAll("TDxal", "");
    }

    @Override
    public String getJavaIdentifier(Definition definition) {
        return super.getJavaIdentifier(definition)
                .replaceAll("YUN_COURT_", "")
                .replaceAll("Ccr_", "")
                .replaceAll("T_PL_", "")
                .replaceAll("T_GJFB_", "")
                .replaceAll("T_FGDQ_", "")
                .replaceAll("T_FZZX_", "")
                .replaceAll("T_SFPJ_", "")
                .replaceAll("T_CC_", "")
                .replaceAll("T_PAY_", "")
                .replaceAll("T_FYTT_", "")
        		.replaceAll("T_MRYA_", "")
                .replaceAll("T_DZSD_", "")
                .replaceAll("T_OCR_", "")
                .replaceAll("T_WECOURT_", "")
                .replaceAll("T_TSZB_", "")
                .replaceAll("T_GGSD_", "")
                .replaceAll("T_FYTT_", "")
                .replaceAll("T_SSZY_", "")
                .replaceAll("T_AUTH_", "")
                .replaceAll("T_WXUSER_", "")
                .replaceAll("T_DXAL_", "");
    }

}
