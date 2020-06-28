package server.models;

public class WeaponRule {
    private final String id;
    private final boolean allow;
    private final transient String name;
    private final transient String categories;

    public WeaponRule(String id, boolean allow, String name, String categories) {
        this.id = id;
        this.allow = allow;
        this.name = name;
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public boolean isAllow() {
        return allow;
    }

    public String getName() {
        return name;
    }

    public String getCategories() {
        return categories;
    }
}
