package cc.sleek.client.event.impl;

import cc.sleek.client.event.Event;

/**
 * @author Divine and Copilot
 * @since 2022-04-22
 * @see net.minecraft.entity.EntityLivingBase#moveEntityWithHeading(float, float)
 */
public class FrictionEvent extends Event {

    // create variables for air friction and ground friction
    private float airFriction;
    private float groundFriction;

    // create a constructor for the event
    public FrictionEvent(float airFriction, float groundFriction) {
        this.airFriction = airFriction;
        this.groundFriction = groundFriction;
    }

    // create getters for air and ground friction
    public float getAirFriction() {
        return airFriction;
    }

    public float getGroundFriction() {
        return groundFriction;
    }

    // create a toString method for the event
    @Override
    public String toString() {
        return "FrictionEvent [airFriction=" + airFriction + ", groundFriction=" + groundFriction + "]";
    }

    // create a clone method for the event
    @Override
    public FrictionEvent clone() {
        return new FrictionEvent(airFriction, groundFriction);
    }

    // create a equals method for the event
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FrictionEvent other = (FrictionEvent) obj;
        if (Float.floatToIntBits(airFriction) != Float.floatToIntBits(other.airFriction))
            return false;
        return Float.floatToIntBits(groundFriction) == Float.floatToIntBits(other.groundFriction);
    }

    // create a hashCode method for the event
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(airFriction);
        result = prime * result + Float.floatToIntBits(groundFriction);
        return result;
    }

    // create a compareTo method for the event

    public int compareTo(Event o) {
        return 0;
    }

    // create a getClass method for the event

    public Class<? extends Event> getClass0() {
        return FrictionEvent.class;
    }

    // create setters for air and ground friction
    public void setAirFriction(float airFriction) {
        this.airFriction = airFriction;
    }

    public void setGroundFriction(float groundFriction) {
        this.groundFriction = groundFriction;
    }

}



