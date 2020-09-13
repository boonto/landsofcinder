package de.loc.event;

public class Event {
    public EventSystem.EventType eventType;
    public Object[] args;

    public Event(EventSystem.EventType type, Object... arg) {
        this.eventType = type;
        this.args = arg;
    }

    public boolean compareTo(Event e) {

        if ( e.eventType == this.eventType ) {
            for ( int i = 0; i < this.args.length; i++ ) {
                if ( e.args[i] == null ) {
                    return false;
                } else if ( !this.args[i].equals(e.args[i]) ) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) {
            return false;
        }
        if ( obj == this ) {
            return true;
        }
        if ( !(obj instanceof Event) ) {
            return false;
        }

        Event e = (Event) obj;
        return this.compareTo(e);
    }

    public String toString() {
        String s = this.eventType.toString();
        for ( Object arg : this.args ) {
            s += ":" + arg;
        }
        return s;
    }
}
