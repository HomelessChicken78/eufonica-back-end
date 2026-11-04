package it.eufonica.engagementservice.dto.playlist;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@AllArgsConstructor @NoArgsConstructor @Builder
@Data
public class PlaylistSearchFiltersDTO {
    /**
     * Case-insensitive substring match against the playlist name.
     */
    private String name;

    @Builder.Default
    @PositiveOrZero(message = "The minimum number of likes can't be negative.")
    private Integer minLikes = 0;

    @PositiveOrZero(message = "The maximum number of likes can't be negative.")
    private Integer maxLikes; // Null means no maximum

    @Builder.Default
    @PositiveOrZero(message = "The minimum number of entries can't be negative.")
    private Integer minNumberOfEntries = 0;

    @PositiveOrZero(message = "The maximum number of entries can't be negative.")
    private Integer maxNumberOfEntries; // Null means no maximum

    @AssertTrue(message = "The maximum number of likes can't be lower than the minimum.")
    public boolean isLikesRangeValid() {
        // Always return true if the max likes is set to null (equivalent to infinity)
        if (maxLikes == null) return true;

        return maxLikes >= minLikes;
    }

    @AssertTrue(message = "The maximum number of entries can't be lower than the minimum.")
    public boolean isNumberOfEntriesRangeValid() {
        // Always return true if the max number of entries is set to null (equivalent to infinity)
        if (maxNumberOfEntries == null) return true;

        return maxNumberOfEntries >= minNumberOfEntries;
    }
}
