package fr.orgpro.api.orgzly.datetime;

import fr.orgpro.api.orgzly.OrgPatterns;
import fr.orgpro.api.orgzly.OrgStringUtils;

import java.util.Calendar;
import java.util.regex.Matcher;

/**
 * Org mode range.
 *
 * For example {@literal <2004-08-23 Mon>} or {@literal <2004-08-23 Mon>--<2004-08-26 Thu>}.
 */
public class OrgRange {
    private OrgDateTime startTime;
    private OrgDateTime endTime;
    private Long tempsDeb;
    private Long tempsFin;

    public static OrgRange parseOrNull(String str) {
        if (OrgStringUtils.isEmpty(str)) {
            return null;
        }

        return parse(str);
    }

    public static OrgRange parse(String str) {
        if (str == null) {
            throw new IllegalArgumentException("OrgRange cannot be created from null string");
        }

        if (str.length() == 0) {
            throw new IllegalArgumentException("OrgRange cannot be created from null string");
        }

        OrgRange t = new OrgRange();

        Matcher m = OrgPatterns.DT_OR_RANGE_P.matcher(str);

        if (m.find()) {
//            for (int i = 0; i < m.groupCount() + 1; i++) {
//                System.out.println("group(" + i + ") " + m.group(i));
//            }

            if (m.groupCount() == 6 && m.group(6) != null) { // Range - two timestamps
                t.startTime = OrgDateTime.parse(m.group(2));
                t.endTime = OrgDateTime.parse(m.group(5));

            } else { // Single timestamp
                t.startTime = OrgDateTime.parse(m.group(2));
                t.endTime = null;
            }

            return t;

        } else {
            throw new IllegalArgumentException(
                    "String " + str +
                    " cannot be parsed as OrgRange using pattern " + OrgPatterns.DT_OR_RANGE_P);
        }
    }

    // TODO: Rename to parse, rename other methods to getInstance, add *orThrow methods if needed
    public static OrgRange doParse(String str) {
        try {
            // Make sure both OrgDateTime are actually parsed.
            // This is pretty bad, clean these classes.
            OrgRange range = OrgRange.parse(str);
            range.startTime.getCalendar();
            if (range.endTime != null) {
                range.endTime.getCalendar();
            }
            return range;
        } catch (Exception e) {
            return null;
        }
    }

    private OrgRange() {
    }

    public OrgRange(OrgDateTime fromTime) {
        this(fromTime, null);
    }

    public OrgRange(OrgDateTime fromTime, OrgDateTime endTime) {
        if (fromTime == null) {
            throw new IllegalArgumentException("OrgRange cannot be created from null OrgDateTime");
        }

        this.startTime = fromTime;
        this.endTime = endTime;
    }

    public OrgRange(OrgDateTime fromTime, OrgDateTime endTime, Long tempsDeb, Long tempsFin) {
        if (fromTime == null) {
            throw new IllegalArgumentException("OrgRange cannot be created from null OrgDateTime");
        }

        this.startTime = fromTime;
        this.endTime = endTime;
        this.tempsDeb = tempsDeb;
        this.tempsFin = tempsFin;
    }


    public OrgDateTime getStartTime() {
        return startTime;
    }

    /**
     * @return last time of the range, can be {@code null}
     */
    public OrgDateTime getEndTime() {
        return endTime;
    }

    public boolean isSet() {
        return startTime != null;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("- ");
        s.append(startTime);

        if (endTime != null) {
            s.append("--");
            s.append(endTime);

            if(tempsDeb != null && tempsFin !=null){
                Long temps = tempsFin - tempsDeb;
                int heure = (int)(temps / 1000 / 60 / 60);
                int minute = (int)(temps / 1000 / 60  - (heure * 60));
                s.append(" ");
                s.append(heure);
                s.append(":");
                s.append(minute);
            }
        }

        return s.toString();
    }

    public String toStringWithoutBrackets() {
        StringBuilder s = new StringBuilder();

        s.append(startTime.toStringWithoutBrackets());

        if (endTime != null) {
            s.append("--");
            s.append(endTime.toStringWithoutBrackets());
        }

        return s.toString();
    }

    public boolean shift() {
        return shift(Calendar.getInstance());
    }

    /**
     * Shifts both timestamps by their repeater intervals.
     *
     * @param now current time
     * @return {@code true} if shift was performed
     */
    public boolean shift(Calendar now) {
        boolean shifted = false;

        if (startTime != null) {
            if (startTime.shift(now)) {
                shifted = true;
            }
        }

        if (endTime != null) {
            if (endTime.shift(now)) {
                shifted = true;
            }
        }

        return shifted;
    }
}
