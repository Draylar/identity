package draylar.identity.api.property;

public class IdentityProperties {

    private boolean burnsInSun = false;
    private boolean canFly = false;
    private boolean breathesAir = false;
    private boolean breathesWater = true;
    private float swimSpeedModifier = 1.0f;

    public IdentityProperties() {

    }

    private IdentityProperties(Builder builder) {
        this.burnsInSun = builder.burnsInSun;
        this.canFly = builder.canFly;
        this.breathesAir = builder.breathesAir;
        this.breathesWater = builder.breathesWater;
        this.swimSpeedModifier = builder.swimSpeedModifier;
    }

    public static Builder builder() {
        return new Builder();
    }


    public boolean getBurnsInSun() {
        return burnsInSun;
    }

    public boolean getCanFly() {
        return canFly;
    }

    public boolean getBreathesAir() {
        return breathesAir;
    }

    public boolean getBreathesWater() {
        return breathesWater;
    }

    public float getSwimSpeedModifier() {
        return swimSpeedModifier;
    }

    public static class Builder {

        private boolean burnsInSun;
        private boolean canFly;
        private boolean breathesAir;
        private boolean breathesWater;
        private float swimSpeedModifier;

        private Builder() {

        }

        public Builder burnsInSun(boolean burnsInSun) {
            this.burnsInSun = burnsInSun;
            return this;
        }

        public Builder setCanFly(boolean canFly) {
            this.canFly = canFly;
            return this;
        }

        public Builder breathesAir(boolean suffocatesInAir) {
            this.breathesAir = suffocatesInAir;
            return this;
        }

        public Builder breathesWater(boolean suffocatesInWater) {
            this.breathesWater = suffocatesInWater;
            return this;
        }

        public Builder setSwimSpeedModifier(float swimSpeedModifier) {
            this.swimSpeedModifier = swimSpeedModifier;
            return this;
        }

        public Builder of(IdentityProperties IdentityProperties) {
            this.burnsInSun = IdentityProperties.burnsInSun;
            this.canFly = IdentityProperties.canFly;
            this.breathesAir = IdentityProperties.breathesAir;
            this.breathesWater = IdentityProperties.breathesWater;
            this.swimSpeedModifier = IdentityProperties.swimSpeedModifier;
            return this;
        }

        public IdentityProperties build() {
            return new IdentityProperties(this);
        }
    }
}
