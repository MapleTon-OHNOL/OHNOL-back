package Onol.onol.Domain;

import Onol.onol.Domain.auth.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
public class Sticker {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sticker_id")
    private Long id;

    @Column(name = "sticker_image")
    private String sticker;

    @Builder
    public Sticker(String stickerUrl) {
        this.sticker = stickerUrl;
    }
}
