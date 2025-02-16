package _dmp.setd_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="finance_entries")
@Getter @Setter
@NoArgsConstructor
public class FinanceEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FinanceType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "original_start_date", nullable = false)
    private LocalDate originalStartDate;

    @Column(name = "original_end_date")
    private LocalDate originalEndDate;

    @Column(name = "repeat_days")
    private String repeatDays;
}

