package com.example.CapstonProject0.DTO;

public class AssetForm {
    private int amount;
    private String type;
    private String name;
    private Long targetId;

    // ✅ getter
    public int getAmount() { return amount; }
    public String getType() { return type; }
    public String getName() { return name; }
    public Long getTargetId() { return targetId; }

    // ✅ setter (리턴타입 void로 수정)
    public void setAmount(int amount) { this.amount = amount; }
    public void setType(String type) { this.type = type; }
    public void setName(String name) { this.name = name; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
}
