package ongoing.backend.data.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class AttributionNameResponse implements Comparable<AttributionNameResponse> {
    private Integer order;
    private String name;
    private String title;
    private List data;
    private List<AttributionNameResponse> children;
    private AttributionNameResponse parent;

    @Override
    public int compareTo(AttributionNameResponse response) {
        return this.title
                .replaceAll("đ", "d")
                .replaceAll("Đ", "D")
                .replaceAll("`", "")
                .replaceAll("´", "")
                .replaceAll("\\^", "")
                .trim().toLowerCase()
                .compareTo(response.getTitle()
                        .replaceAll("đ", "d")
                        .replaceAll("Đ", "D")
                        .replaceAll("`", "")
                        .replaceAll("´", "")
                        .replaceAll("\\^", "")
                        .trim().toLowerCase());
    }
}
