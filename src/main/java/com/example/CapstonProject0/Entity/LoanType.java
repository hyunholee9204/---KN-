package com.example.CapstonProject0.Entity;

public enum LoanType {
    MORTGAGE("1", "주택담보대출"),
    CREDIT("2", "신용대출"),
    AUTO("3", "자동차할부"),
    OTHER("99", "기타");

    private final String code;
    private final String description;

    LoanType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static LoanType fromCode(String code) {
        if (code == null) return OTHER;

        for (LoanType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }

        if (code.startsWith("1")) return MORTGAGE;
        if (code.startsWith("2")) return CREDIT;
        if (code.startsWith("3")) return AUTO;
        return OTHER;
    }

}
