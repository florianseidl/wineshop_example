package at.fseidl.wineshop.shared.service;

public record WineDTO(String name, String orignCountry, String originRegion, String wineType, String wineTypeColor,
                      double price) {

    public static class WineDTOBuilder {
        private final String name;
        String orignCountry;
        String originRegion;
        String wineType;
        String wineTypeColor;
        double price;

        public WineDTOBuilder(String name) {
            this.name = name;
        }

        public void setOrignCountry(String orignCountry) {
            this.orignCountry = orignCountry;
        }

        public void setOriginRegion(String originRegion) {
            this.originRegion = originRegion;
        }

        public void setWineType(String wineType) {
            this.wineType = wineType;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public void setWineTypeColor(String wineTypeColor) {
            this.wineTypeColor = wineTypeColor;
        }

        public WineDTO build() {
            return new WineDTO(name, orignCountry, originRegion, wineType, wineTypeColor, price);
        }
    }
}
