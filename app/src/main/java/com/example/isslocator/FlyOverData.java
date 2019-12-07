package com.example.isslocator;

import java.util.Date;

public class FlyOverData {
    private String duration;
    private Date riseTime;

    public FlyOverData(String aDuration, Date aRiseTime) {
        duration = aDuration;
        riseTime = aRiseTime;
    }

    public String getDuration() {
        return duration;
    }

    public Date getRiseTime() {
        return riseTime;
    }

    public void setDuration(String aDuration) {
        duration = aDuration;
    }

    public void setRiseTime(Date aRiseTime) {
        riseTime = aRiseTime;
    }
}
