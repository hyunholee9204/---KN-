package com.example.CapstonProject0.DTO;

public class AssetTotalForm {
    private String type;
    private Long totalAmount;

    public AssetTotalForm(String type, Long totalAmount) {
        this.type = type;
        this.totalAmount = totalAmount;
    }

    public String getType() {
        return type;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }
}
