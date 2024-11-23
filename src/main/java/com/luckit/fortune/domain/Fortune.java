package com.luckit.fortune.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luckit.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class) // Auditing 활성화
@Table(name = "fortunes")
public class Fortune {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @CreatedDate
    private LocalDate date;

    @Lob
    @Convert(converter = ListToJsonConverter.class)
    private List<String> fortuneKeywords;

    @Lob
    @Convert(converter = MapToJsonConverter.class)
    private Map<String, Integer> categoryFortuneScores;

    @Lob
    @Convert(converter = MapToJsonConverter.class)
    private Map<String, Integer> timeOfDayFortuneScores;

    private Integer overallFortuneScore;

    @Column(length = 255)
    private String shortFortune;

    @Lob
    private String fullFortune;
}