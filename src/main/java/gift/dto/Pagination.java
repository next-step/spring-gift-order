package gift.dto;

import jakarta.validation.constraints.Min;
import java.util.List;

public class Pagination {

    @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
    private int page = 1;

    @Min(value = 1, message = "페이지 사이즈는 1 이상이어야 합니다.")
    private int size = 10;

    private List<String> sort;

    public Pagination() {
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public List<String> getSort() {
        return sort;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setSort(List<String> sort) {
        this.sort = sort;
    }
}
