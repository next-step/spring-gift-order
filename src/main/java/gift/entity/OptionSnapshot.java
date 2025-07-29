package gift.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class OptionSnapshot {

    @Column(name = "option_name_snapshot", nullable = false)
    private String optionNameSnapshot;

    @Column(name = "option_quantity_snapshot", nullable = false)
    private int optionQuantitySnapshot;

    protected OptionSnapshot() {
    }

    public OptionSnapshot(String name, int quantity) {
        this.optionNameSnapshot = name;
        this.optionQuantitySnapshot = quantity;
    }


    public String getName() {
        return optionNameSnapshot;
    }

    public int getQuantity() {
        return optionQuantitySnapshot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OptionSnapshot)) return false;
        OptionSnapshot that = (OptionSnapshot) o;
        return optionQuantitySnapshot == that.optionQuantitySnapshot && optionNameSnapshot.equals(that.optionNameSnapshot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(optionNameSnapshot, optionQuantitySnapshot);
    }
}
