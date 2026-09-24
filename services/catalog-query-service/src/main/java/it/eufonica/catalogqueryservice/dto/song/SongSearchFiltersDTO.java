package it.eufonica.catalogqueryservice.dto.song;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode
public class SongSearchFiltersDTO {
    /**
     * Case-insensitive substring match against the song title.
     */
    private String title;

    @Builder.Default
    @PositiveOrZero(message = "The minimum duration can't be negative.")
    private Integer minDurationSec = 0;

    @PositiveOrZero(message = "The maximum duration can't be negative.")
    private Integer maxDurationSec; // Null means no maximum

    /**
     * Case-insensitive substring match against the song owner's name.
     */
    private String artistOwnerName;

    private LocalDate publishedDateAfter;

    private LocalDate publishedDateBefore;

    @Builder.Default
    @PositiveOrZero(message = "The minimum number of listens can't be negative.")
    private Integer minListens = 0;

    @PositiveOrZero(message = "The maximum number of listens can't be negative.")
    private Integer maxListens; // Null means no maximum

    @Builder.Default
    @PositiveOrZero(message = "The minimum number of likes can't be negative.")
    private Integer minLikes = 0;

    @PositiveOrZero(message = "The maximum number of likes can't be negative.")
    private Integer maxLikes; // Null means no maximum

    @AssertTrue(message = "The maximum duration can't be shorter than the minimum duration.")
    public boolean isDurationRangeValid() {
        // Always return true if the max duration is set to null (equivalent to infinity)
        if (maxDurationSec == null) return true;

        return maxDurationSec >= minDurationSec;
    }

    @AssertTrue(message = "The maximum number of listens can't be lower than the minimum.")
    public boolean isListensRangeValid() {
        // Always return true if the max listens is set to null (equivalent to infinity)
        if (maxListens == null) return true;

        return maxListens >= minListens;
    }

    @AssertTrue(message = "The maximum number of likes can't be lower than the minimum.")
    public boolean isLikesRangeValid() {
        // Always return true if the max likes is set to null (equivalent to infinity)
        if (maxLikes == null) return true;

        return maxLikes >= minLikes;
    }
}
