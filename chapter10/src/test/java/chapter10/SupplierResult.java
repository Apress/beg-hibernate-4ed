package chapter10;

class SupplierResult {
    String name;
    long count;

    public SupplierResult(String name, Long count) {
        this.name = name;
        this.count = count;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SupplierResult{");
        sb.append("name='").append(name).append('\'');
        sb.append(", count=").append(count);
        sb.append('}');
        return sb.toString();
    }
}