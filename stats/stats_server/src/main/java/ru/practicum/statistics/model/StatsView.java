package ru.practicum.statistics.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stats_view")
public class StatsView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_app", nullable = false)
    private String nameApp;

    @Column(name = "from_ip", nullable = false)
    private String fromIp;

    @Column(name = "uri", nullable = false)
    private String url;

    @Column(name = "viewed", nullable = false)
    private LocalDateTime viewed;

}
