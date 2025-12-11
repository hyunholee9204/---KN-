package com.example.CapstonProject0.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Update_detail")
public class UpdateDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="note_id")
    private UpdateEntity note;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ChangeType type;

    @Column(nullable=false, length=500)
    private String description;

    // ===== Getter & Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UpdateEntity getNote() { return note; }
    public void setNote(UpdateEntity note) { this.note = note; }

    public ChangeType getType() { return type; }
    public void setType(ChangeType type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
